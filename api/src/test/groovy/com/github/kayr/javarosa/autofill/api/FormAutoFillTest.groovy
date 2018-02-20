package com.github.kayr.javarosa.autofill.api

import org.junit.Assert
import org.junit.Test
import org.openxdata.markup.Converter
import org.openxdata.markup.FLAGS
import org.openxdata.markup.Form
import org.openxdata.markup.IFormElement

import static TestUtils.resourceText
import static com.github.kayr.javarosa.autofill.api.TestUtils.formatXML

class FormAutoFillTest implements LogConfig {

    @Test
    void testAutoFillSimpleForm() {

        def form = Converter.markup2Form(resourceText('/simpleform.mkp'))


        def result = TestUtils.formAutoFillFromMkp(form)
                              .autoFill()
                              .getSubmissionXml()


        assertAllNodeAnswers(form, result)


    }

    private static Iterable<IFormElement> assertAllNodeAnswers(Form form, String xml) {
        def node = new XmlParser().parseText(xml)

        return assertAllNodesHaveData(node, form)
    }

    private static Iterable<IFormElement> assertAllNodesHaveData(Node node, Form form) {
        def allNodes = node.'**'

        return form.allElementsWithIds.each { q ->
            assert allNodes.find { it.name() == q.id }.text()
        }
    }

    @Test
    void testWithValidationTelNumberShouldFail() {

        def mkp = '''## Form

                      @validif regex(., "07[0-9]{10}")
                      @message Provide right format
                      Tel Number'''

        def form = Converter.markup2Form(mkp)
        try {
            TestUtils.formAutoFillFromMkp(form)
                     .autoFill()
                     .getSubmissionXml()
            Assert.fail("Should not pass with jjs")
        } catch (Exception x) {
            assert x.message.contains("Invalid Answer")
        }

    }

    @Test
    void testWithGenerexNumberShouldPass() {

        def mkp = '''
                      @bindxpath generex
                      ## Form

                      @validif regex(., "07[0-9]{10}")
                      @message Provide right format
                      @bind:generex random-regex('07[0-9]{10}')
                      Tel Number'''

        def form = Converter.markup2Form(mkp)
        def xml = TestUtils.formAutoFillFromMkp(form)
                           .autoFill()
                           .getSubmissionXml()

        assertAllNodeAnswers(form, xml)

    }

    @Test
    void textLargeForm() {

        def form = Converter.markup2Form(resourceText('/long-form1.mkp'), FLAGS.of(FLAGS.ODK_OXD_MODE))



        100.times {
            TestUtils.formAutoFillFromMkp(form)
                     .autoFill()
                     .getSubmissionXml()
        }


    }

    @Test
    void testFormWithPics() {

        def form = '''
            ## Form
            
            @picture
            Picture
            
            @audio
            Audio
            
            @video
            Video
'''

        def xml = TestUtils.formAutoFillFromMkp(Converter.markup2Form(form))
                           .autoFill()
                           .getSubmissionXml()

        def node = new XmlParser().parseText(xml)

        assert node.picture.text().endsWith('.jpg')
        assert node.audio.text().endsWith('.mp3')
        assert node.video.text().endsWith('.mp4')


    }


    @Test
    void testGenerexInRepeat() {

        def f = '''
                    @bindxpath generex
                    @id f
                    ## f
                    
                    One 
                    
                    repeat { Details
                    
                        @bind:generex 'tt'
                        R1
                        
                        R2
                    
                    } '''

        def xml = TestUtils.formAutoFillFromMkp(Converter.markup2Form(f))
                           .autoFill()
                           .getSubmissionXml()

        def node = new XmlParser().parseText(xml)

        assert node.details.r1
        assert node.details.r1.every { !it.text().isEmpty() && it.text() == 'tt' }
    }

    @Test
    void testFixedRepeatSizeSimple() {
        def f = '''
                    @bindxpath generex
                    ## f
                    
                    One 
                    
                    @bind:generex count($details) < 3
                    repeat { Details
                    
                        R1
                        
                        R2
                    
                    } '''

        def xml = TestUtils.formAutoFillFromMkp(Converter.markup2Form(f))
                           .autoFill()
                           .getSubmissionXml()

        def node = new XmlParser().parseText(xml)
        assert node.details.r1
        assert node.details.size() == 3
        assert node.details.r1.every { !it.text().isEmpty() }
        assert node.details.details_3.r11.every { !it.text().isEmpty() }

    }

    //test prefilling with a value outside repeat

    @Test
    void testPrefilValueInRepeatReferencingOuterQuestion() {
        def f = '''
                    @bindxpath generex
                    ## f
                    
                    One 
                    
                    @bind:generex count($details) < 3
                    repeat { Details
                    
                        @bind:generex $one
                        R1
                        
                    } '''

        def xml = TestUtils.formAutoFillFromMkp(Converter.markup2Form(f))
                           .autoFill()
                           .getSubmissionXml()

        def node = new XmlParser().parseText(xml)
        assert node.details.r1
        assert node.details.size() == 3
        assert node.details.r1.every { it.text() == node.one.text() }
    }


    @Test
    void testPrefillRepeatCountOnInnerRepeatReferencingInnerQn() {
        def f = '''
                    @bindxpath generex
                    ## f
                    
                    One 
                    
                    @bind:generex count(.) < 3
                    repeat { Details
                    
                        @bind:generex $one
                        R1
                        
                        @bind:generex count(details_2) < 2
                        repeat{ Details 2
                            R3
                        }
                       
                        
                    } '''

        def xml = TestUtils.formAutoFillFromMkp(Converter.markup2Form(f))
                           .autoFill()
                           .getSubmissionXml()

        println(formatXML(xml))
        def node = new XmlParser().parseText(xml)
        assert node.details.r1
        assert node.details.size() == 3
        assert node.details[0].details_2.size() == 2
        assert node.details.r1.every { it.text() == node.one.text() }

    }

    @Test
    void testFixedRepeatSize() {

        def f = '''
                    @bindxpath generex
                    ## f
                    
                    One 
                    
                    @bind:generex count(../details) < 3
                    repeat { Details
                    
                        @bind:generex 'tt'
                        R1
                        
                        R2
                        
                        @bind:generex count(details_3) < 2
                        repeat{ Details 3
                             @bind:generex 'r22'
                             R11
                        
                        }
                    
                    } '''

        def xml = TestUtils.formAutoFillFromMkp(Converter.markup2Form(f))
                           .autoFill()
                           .getSubmissionXml()

        def node = new XmlParser().parseText(xml)
        assert node.details.r1
        assert node.details.size() == 3
        assert node.details.r1.every { !it.text().isEmpty() && it.text() == 'tt' }
        assert node.details.details_3.r11.every { !it.text().isEmpty() && it.text() == 'r22' }
    }

    @Test
    void testSettingValueInMemory() {

        def f = '''
                    @bindxpath generex
                    ## f
                    
                    
                    @id pr
                    repeat{ Patient Records
                    
                      @bind:generex select-cell(val('patient-record',random-select-from-file('src/test/resources/data/patientdata.csv')),0)
                      Name
                      
                      @bind:generex select-cell(val('patient-record'),1)
                      ID

                      @bind:generex select-cell(val('patient-record'),2)                      
                      Disease
                        
                    
                    } 
                  
                   
                    '''

        def xml = TestUtils.formAutoFillFromMkp(Converter.markup2Form(f))
                           .autoFill()
                           .getSubmissionXml()

        def node = new XmlParser().parseText(xml)

        node.pr.every {
            assert it.text() in ['John Johons1Malaria', 'Richard Galix2Polio', 'Galiwango3Cold']
            assert it.'*'.every { !it.text().isEmpty() }
        }
    }

    @Test
    void testExternalGenerex() {
        def m = """
                ## F
                
                One 
                
                One2
                
                Two
                
                repeat{ Repeat
                    Three
                    
                   
                    Four
                }              
"""

        def xml = TestUtils.formAutoFillFromMkp(Converter.markup2Form(m))
                           .addGenerex('one', "'ONE'")
                           .addGenerex('repeat/four', "'FOUR'")
                           .addGenerex('two', '    ')
                           .addGenerex('one2', 'string(current()/../one)')
                           .autoFill()
                           .getSubmissionXml()

        def node = new XmlSlurper().parseText(xml)

        assert node.one.text() == 'ONE'
        assert node.one2.text() == 'ONE', "The generex xpath should be executed in right context"
        assert node.repeat.four[0].text() == 'FOUR'

    }

    @Test
    void testGenerexWithRepeat() {
        def f = '''
            @bindxpath generex
            ## F
            
            One 
            
            @bind:generex count($repeat) < 2
            repeat{ Repeat
                
                @id boys
                @number
                Boy in school
                
                
                @number
                @validif . <= current()/../boys
                @message Should be less than boys
                @bind:generex random-number(0,number(../boys))
                Boys attended
                
                @id girls
                @number
                Girls in school
                
                @number
                @validif  . <= current()/../girls
                @message Should be less than girls
                @bind:generex random-number(0,number(../girls))
                Girls attended
            }
            '''
        def form = Converter.markup2Form(f)
        def xmlText = TestUtils.formAutoFillFromMkp(form)
                               .autoFill()
                               .getSubmissionXml()

        def node = new XmlParser().parseText(xmlText)

        assertAllNodesHaveData(node, form)

        assert node.repeat.size() == 2
        assert (node.repeat[1].boys_attended.text() as int) <= (node.repeat[1].boys.text() as int)


        println(formatXML(xmlText))

    }

//    @Test
//    void testSelectMulti() {
//        def items = [1, 2]
//
//        Map<Integer, Integer> stats = items.collectEntries { [it, 0] }
//
//        Map<Integer, Integer> singleStats = items.collectEntries { [it, 0] }
//
//
//        100.times {
//            def many = Fakers.getRandomMany(items)
//            items.each { num -> if (many.contains(num)) stats[num] = stats[num] + 1 }
//
//            def one = Fakers.getRandom(items)
//
//            singleStats[one] = singleStats[one] + 1
//        }
//
//
//
//        println("Multiple Starts")
//        println(stats)
//
//        println("Single stats")
//        println(singleStats)
//    }
}
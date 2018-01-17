package com.github.kayr.javarosa.autofill.api

import groovy.test.GroovyAssert
import org.junit.Test
import org.openxdata.markup.Converter
import org.openxdata.markup.FLAGS
import org.openxdata.markup.Form
import org.openxdata.markup.IFormElement

import static TestUtils.resourceText

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
        GroovyAssert.shouldFail(IllegalArgumentException) {
            TestUtils.formAutoFillFromMkp(form)
                     .autoFill()
                     .getSubmissionXml()
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

        assert node.picture.text().startsWith('data:image/jpg')
        assert node.audio.text().startsWith('data:audio/mp3')
        assert node.video.text().startsWith('data:video/mp4')


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
    void testFixedRepeatSize() {

        def f = '''
                    @bindxpath generex
                    ## f
                    
                    One 
                    
                    @bind:generex count($details) < 3
                    repeat { Details
                    
                        @bind:generex 'tt'
                        R1
                        
                        R2
                        
                        @bind:generex random-boolean()
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
                
                Two
                
                repeat{ Repeat
                    Three
                    
                   
                    Four
                }
                
"""

        def xml = TestUtils.formAutoFillFromMkp(Converter.markup2Form(m))
                           .addGenerex('one', "'ONE'")
                           .addGenerex('four', "'FOUR'")
                           .autoFill()
                           .getSubmissionXml()

        def node = new XmlSlurper().parseText(xml)

        assert node.one.text() == 'ONE'
        assert node.repeat.four[0].text() == 'FOUR'

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
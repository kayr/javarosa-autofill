package com.github.kayr.javarosa.autofill.test

import com.github.kayr.javarosa.autofill.api.functions.Fakers
import org.junit.Ignore
import org.junit.Test

import java.lang.reflect.Method
import java.lang.reflect.Modifier

class ApiOutPut {

    @Test
    @Ignore
    void outPutAll() {

        printAllMethods(0, [], Fakers.faker.class)

    }

    def b2 = ['equals', 'hashCode', 'toString', 'notify', 'wait', 'notifyAll', 'getClass']

    void printAllMethods(int level, List parents, Class aClass) {

        def indent = "    " * level
        def parentString = parents.collect { "'$it'" }.join(', ')
        for (method in aClass.methods.sort { it.name }) {
            def isInIgnoreList = method.name in b2

            if (!Modifier.isStatic(method.getModifiers()) && !isInIgnoreList && method.parameterCount == 0) {

                String gerenx = "fake($parentString,'$method.name')"

                if (level == 0) {
                    println(indent + method.name)

                } else {
                    println(indent + method.name.padRight(22) + ': ' + gerenx)
                }
                if (method.returnType.name.startsWith('com.github.javafaker')) {
                    def newParents = [*parents, method.name]
                    printAllMethods(level + 1, newParents, method.returnType)
                }

                if (level == 0) println("-" * 40)

            }


        }

    }


    static String methodString(Method method) {
        def types = method.parameterTypes

        def methodName = method.name
        def returnType = method.returnType.simpleName

        if (types) {
            return "** $methodName(${types*.simpleName.join(', ')}) => ${returnType}"
        } else {
            return "$methodName => ${returnType}"

        }

    }

}

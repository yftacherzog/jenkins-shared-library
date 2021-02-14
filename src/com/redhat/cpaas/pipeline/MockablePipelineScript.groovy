package com.redhat.cpaas.pipeline

import com.redhat.cpaas.pipeline.JobParams
import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import com.cloudbees.groovy.cps.NonCPS

class MockablePipelineScript extends PipelineScript {
    MockablePipelineScript() {
        setupStepMethodMockups()
    }

    @NonCPS
    def setupStepMethodMockups(String str = 'default') {
        def theEnv
        try {
            theEnv = env
        } catch (groovy.lang.MissingPropertyException e1) {
            theEnv = null
        } catch (groovy.lang.MissingMethodException e2) {
            theEnv = null
        }

        System.out.println("str: $str")
        System.out.println("env: $theEnv")
        def dumpList = theEnv?.TEST_SUITE_MOCK?.split(',')?.collect(
            {it.trim()}) ?: []

        dumpList.each { method ->
            System.out.println("creating method: $method")
            this.metaClass."$method" = createStepMethodMockup(method)
        }
    }

    @NonCPS
    def createStepMethodMockup(String name) {
        return { Object... args ->
            // get existing parameter value
            String dumpStr = JobParams.getParameterValue(
                currentBuild,
                "TEST_SUITE_MOCK_TRACE"
            )

            JsonSlurper jsonSlurper = new JsonSlurper()
            List dump = dumpStr ? jsonSlurper.parseText(dumpStr) : []

            // add new dump
            Map newEntry = [name: name, args: args]
            dump.add(newEntry)
            System.out.println("dump: $dump")

            // push new parameter
            JobParams.modifySetParam(
                currentBuild,
                "TEST_SUITE_MOCK_TRACE",
                JsonOutput.toJson(dump)
            )
        }
    }
}
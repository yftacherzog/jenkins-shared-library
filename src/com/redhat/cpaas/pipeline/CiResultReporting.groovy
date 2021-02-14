package com.redhat.cpaas.pipeline

import com.redhat.cpaas.pipeline.JobParams
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

class CiResultReporting extends MockablePipelineScript {

    private static CiResultReporting _moduleInstance = null
    private static CiResultReporting _moduleInstanceOrig = null
    private static String foobar = 'fooz'

    /* XXX:
    How to avoid instance name (_moduleInstance) when calling the step?
    How to make use of the original method without creating another instance?
    How to do this through the spec?
    --> Need to implement methodMissing/invokeMethod
    https://javadoc.jenkins.io/plugin/workflow-cps/org/jenkinsci/plugins/workflow/cps/CpsScript.html
    invokeMethod() cannot be overriden
     */
    static CiResultReporting getModuleInstance() {
        if (!_moduleInstance) {
            _moduleInstance = new CiResultReporting()
            /* _moduleInstanceOrig = new CiResultReporting()

            _moduleInstance.metaClass.baz = {-> 'bar'}

            _moduleInstance.metaClass.echo = { str ->
                _moduleInstanceOrig.echo "XYZ $str" }

            _moduleInstance.metaClass.build = { 
                params ->
                _moduleInstanceOrig.build job:
                    params.get('job'),
                    wait: params.get('wait'),
                    quietPeriod: 15} */
        }
        return _moduleInstance
    }

    static def withTrigger(Closure action) {
        return getModuleInstance().wrapClosure(action)
    }

    def wrapClosure(Closure action) {
        def returnedValue = null
        triggerBuild {
            returnedValue = action.call()
        }
        return returnedValue
    }

    void triggerBuild(Closure action) {
        //_moduleInstance.echo "baz: " + baz()
        echo "baz: " + baz() + " " + foobar
        //_moduleInstance.build job: "foo-folder/foo-pipeline", wait: false, quietPeriod: 3
        for (i = 0; i<33; i++) {
            build job: "foo-folder/foo-pipeline", wait: false, quietPeriod: 3
        }
    }

    String baz() {
        return 'baz'
    }

    /* CiResultReporting() {
        setupStepMethodMockups()
    } */

    /*@NonCPS
    def setupStepMethodMockups() {
        def theEnv
        try {
            theEnv = env
        } catch (groovy.lang.MissingPropertyException e1) {
            theEnv = null
        } catch (groovy.lang.MissingMethodException e2) {
            theEnv = null
        }

        def dumpList = theEnv?.TEST_SUITE_MOCK?.split(',')?.collect(
            {it.trim()}) ?: []
        dumpList.each { method ->
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

            // push new parameter
            JobParams.modifySetParam(
                currentBuild,
                "TEST_SUITE_MOCK_TRACE",
                JsonOutput.toJson(dump)
            )
        }
    } */

    /* def methodMissing(String name, def args) {
        def dumpList = !env.TEST_SUITE_MOCK ? "" :
            env.TEST_SUITE_MOCK.split(',').collect { it.trim() }

        if (dumpList.contains(name)) {
            echo "dumping " + name
            dumpInvocation(name, args.toString())
        } else {
            // invokeMethod(name, args)
            // getZuper().methodMissing(name, args)
            // throw new MissingMethodException(name, this.class, args)
            super.invokeMethod(name, args)
        }
    }

    @NonCPS
    private def getZuper() {
        println("this: " + this.getClass())
        println("super: " + super.getClass())
        return super
    } */

    /* void dumpInvocation(String name, String args) {
        // get existing parameter value
        // TODO: find the modifySetParam equivalent for reading
        String dumpStr = env.TEST_SUITE_MOCK_TRACE
        JsonSlurper jsonSlurper = new JsonSlurper()
        List dump = dumpStr ? jsonSlurper.parseText(dumpStr) : []
      
        echo "oldJson: " + dump

        // add new dump
        Map newEntry = [name: name, args: args]
        dump.add(newEntry)
        echo "newJson: " + dump

        // push new parameter
        JobParams.modifySetParam(
            currentBuild,
            "TEST_SUITE_MOCK_TRACE",
            JsonOutput.toJson(dump)
        )
    }

    List getDumpedTrace() {
        String dumps = env.TEST_SUITE_MOCK_TRACE
        JsonSlurper jsonSlurper = new JsonSlurper()
        return dumps ? jsonSlurper.parseText(dumps) : []
    } */
}

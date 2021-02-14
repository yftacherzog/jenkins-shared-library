package com.redhat.cpaas.pipeline

import static com.redhat.cpaas.pipeline.CiResultReporting.withTrigger
import com.lesfurets.jenkins.unit.BasePipelineTest

class fooTester extends BasePipelineTest {

    public void setUp() {
        super.setUp();

        helper.registerAllowedMethod('echo', [String], { String message -> 
            println('ZZZ' + message)
        })
    }

    /* public void testEcho(String str) {

    } */
}




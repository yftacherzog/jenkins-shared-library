import static com.redhat.cpaas.pipeline.CiResultReporting.withTrigger

def call(Closure body) {
    node {
        withTrigger {
            stage("Trigger") {
                echo 'stage trigger'
            }
        }
    }
}


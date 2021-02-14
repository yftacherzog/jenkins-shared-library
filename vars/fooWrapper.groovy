import com.redhat.cpaas.pipeline.fooTester

def call(Closure body) {
    fooTester tester = new fooTester()
    tester.setUp()
    echo 'bar'
}


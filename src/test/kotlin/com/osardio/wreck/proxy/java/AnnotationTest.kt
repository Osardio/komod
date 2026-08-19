package com.osardio.wreck.proxy.java

import com.osardio.wreck.classes
import com.osardio.wreck.files
import com.osardio.wreck.methods
import com.osardio.wreck.modifyJavaTest
import org.junit.jupiter.api.Test

class AnnotationTest {

    @Test
    fun addAnnotationWithFqn() = modifyJavaTest(
        input = """
            package com.example;
            public class MyService {
                public void someMethod() {}
            }
        """.trimIndent(),
        expected = """
            package com.example;
            import java.lang.annotation.Native;

            public class MyService {
                @Native
                public void someMethod() {}
            }
        """.trimIndent()
    ) {
        files.forEach { it.imports += Import("java.lang.annotation.Native") }
        classes.methods {
            name == "someMethod" &&
            type.fqn.endsWith("void")
        }.forEach {
            it.annotations += Annotation("Native")
        }
    }
}

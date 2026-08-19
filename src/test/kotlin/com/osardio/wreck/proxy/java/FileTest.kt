package com.osardio.wreck.proxy.java

import com.osardio.wreck.modifyJavaTest
import org.junit.jupiter.api.Test

class FileTest {

    @Test
    fun addImport() = modifyJavaTest(
        input = """
            package com.example;
            public class MyService {
                public void someMethod() {}
            }
        """.trimIndent(),
        expected = """
            package com.example;
            import java.util.List;

            public class MyService {
                public void someMethod() {}
            }
        """.trimIndent()
    ) {
        imports += Import("java.util.List")
    }
}
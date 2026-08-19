package com.osardio.komod.proxy.java

import com.osardio.komod.addAnnotation
import com.osardio.komod.classes
import com.osardio.komod.methods
import com.osardio.komod.modifyJavaTest
import com.osardio.komod.parameters
import org.junit.jupiter.api.Test

class AnnotationTest {

    @Test
    fun addAnnotationToClass() = modifyJavaTest(
        input = """
            package com.example;
            public class MyService {
                public void someMethod() {}
            }
        """.trimIndent(),
        expected = """
            package com.example;
            import java.lang.annotation.Native;

            @Native
            public class MyService {
                public void someMethod() {}
            }
        """.trimIndent()
    ) {
        classes({ name == "MyService" }) {
            addAnnotation("java.lang.annotation.Native")
        }
    }

    @Test
    fun addAnnotationToMethod() = modifyJavaTest(
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
        classes({ name == "MyService" }) {
            methods({ name == "someMethod" && type.fqn.endsWith("void") }) {
                addAnnotation("java.lang.annotation.Native")
            }
        }
    }

    @Test
    fun addAnnotationToParameter() = modifyJavaTest(
        input = """
            package com.example;
            public class MyService {
                public void someMethod(int num) { System.out.println(num); }
            }
        """.trimIndent(),
        expected = """
            package com.example;
            import java.lang.annotation.Native;

            public class MyService {
                public void someMethod(@Native int num) { System.out.println(num); }
            }
        """.trimIndent()
    ) {
        classes({ name == "MyService" }) {
            methods({ name == "someMethod" }) {
                parameters({ type.fqn == "int" }) {
                    addAnnotation("java.lang.annotation.Native")
                }
            }
        }
    }
}

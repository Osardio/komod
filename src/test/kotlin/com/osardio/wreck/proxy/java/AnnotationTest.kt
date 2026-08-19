package com.osardio.wreck.proxy.java

import com.osardio.wreck.addAnnotation
import com.osardio.wreck.classes
import com.osardio.wreck.methods
import com.osardio.wreck.modifyJavaTest
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
        classes {
            name == "MyService"
        }.forEach {
            it.addAnnotation("java.lang.annotation.Native")
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
        classes.methods {
            name == "someMethod" &&
            type.fqn.endsWith("void")
        }.forEach {
            it.addAnnotation("java.lang.annotation.Native")
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
        classes.methods {
            name == "someMethod" &&
            parameters.firstOrNull()?.type?.fqn == "int"
        }.forEach { method ->
            method.parameters.firstOrNull()?.addAnnotation("java.lang.annotation.Native")
        }
    }
}

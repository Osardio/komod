package com.osardio.komod.proxy.java

import com.osardio.komod.classes
import com.osardio.komod.methods
import com.osardio.komod.modifyJavaTest
import org.junit.jupiter.api.Test

class MethodTest {

    @Test
    fun renameMethod() = modifyJavaTest(
        input = """
            package com.example;
            public class MyService {
                public void someMethod(String arg) { System.out.println(arg); }
                public void someMethod(int num) { System.out.println(num); }
            }
        """.trimIndent(),
        expected = """
            package com.example;
            public class MyService {
                public void someNewMethod(String arg) { System.out.println(arg); }
                public void someMethod(int num) { System.out.println(num); }
            }
        """.trimIndent()
    ) {
        classes({ name == "MyService" }) {
            methods({
                name == "someMethod" &&
                modifiers.contains(Modifier.Keyword.PUBLIC) &&
                parameters.firstOrNull()?.type?.fqn == "String"
            }) {
                name = "someNewMethod"
            }
        }
    }

    @Test
    fun changeAccessModifier() = modifyJavaTest(
        input = """
            package com.example;
            public class MyService {
                public void someNewMethod(String arg) { System.out.println(arg); }
                public void someMethod(int num) { System.out.println(num); }
            }
        """.trimIndent(),
        expected = """
            package com.example;
            public class MyService {
                public void someNewMethod(String arg) { System.out.println(arg); }
                private void someMethod(int num) { System.out.println(num); }
            }
        """.trimIndent()
    ) {
        classes({ name == "MyService" }) {
            methods({
                name == "someMethod" &&
                modifiers.contains(Modifier.Keyword.PUBLIC) &&
                parameters.firstOrNull()?.type?.fqn == "int"
            }) {
                modifiers = setOf(Modifier.Keyword.PRIVATE)
            }
        }
    }

    @Test
    fun addStaticModifier() = modifyJavaTest(
        input = """
            package com.example;
            public class MyService {
                public void someNewMethod(String arg) { System.out.println(arg); }
                public void someMethod(int num) { System.out.println(num); }
            }
        """.trimIndent(),
        expected = """
            package com.example;
            public class MyService {
                public static void someNewMethod(String arg) { System.out.println(arg); }
                public void someMethod(int num) { System.out.println(num); }
            }
        """.trimIndent()
    ) {
        classes({ name == "MyService" }) {
            methods({
                name == "someNewMethod" &&
                modifiers.contains(Modifier.Keyword.PUBLIC) &&
                parameters.firstOrNull()?.type?.fqn == "String"
            }) {
                modifiers += Modifier.Keyword.STATIC
            }
        }
    }

    @Test
    fun removeAccessModifier() = modifyJavaTest(
        input = """
            package com.example;
            public class MyService {
                public void someNewMethod(String arg) { System.out.println(arg); }
                private void someMethod(int num) { System.out.println(num); }
            }
        """.trimIndent(),
        expected = """
            package com.example;
            public class MyService {
                public void someNewMethod(String arg) { System.out.println(arg); }
                void someMethod(int num) { System.out.println(num); }
            }
        """.trimIndent()
    ) {
        classes({ name == "MyService" }) {
            methods({
                name == "someMethod" &&
                modifiers.contains(Modifier.Keyword.PRIVATE) &&
                parameters.firstOrNull()?.type?.fqn == "int"
            }) {
                modifiers = emptySet()
            }
        }
    }

    @Test
    fun addStatement() = modifyJavaTest(
        input = """
            package com.example;
            public class MyService {
                public void someNewMethod(String arg) { System.out.println(arg); }
                void someMethod(int num) { System.out.println(num); }
            }
        """.trimIndent(),
        expected = """
            package com.example;
            public class MyService {
                public void someNewMethod(String arg) { System.out.println(arg); }
                void someMethod(int num) {
                    System.out.println(num);
                    System.out.println("Test!");
                }
            }
        """.trimIndent()
    ) {
        classes({ name == "MyService" }) {
            methods({
                name == "someMethod" &&
                parameters.firstOrNull()?.type?.fqn == "int"
            }) {
                statements += Statement("System.out.println(\"Test!\");")
            }
        }
    }

    @Test
    fun changeReturnType() = modifyJavaTest(
        input = """
            package com.example;
            public class MyService {
                public void someMethod(int num) { System.out.println(num); }
                public void someNewMethod(String arg) { System.out.println(arg); }
            }
        """.trimIndent(),
        expected = """
            package com.example;
            public class MyService {
                public int someMethod(int num) { System.out.println(num); }
                public void someNewMethod(String arg) { System.out.println(arg); }
            }
        """.trimIndent()
    ) {
        classes({ name == "MyService" }) {
            methods({
                name == "someMethod" &&
                parameters.firstOrNull()?.type?.fqn == "int" &&
                type.fqn.endsWith("void")
            }) {
                type = Type("int")
            }
        }
    }

    @Test
    fun addParameter() = modifyJavaTest(
        input = """
            package com.example;
            public class MyService {
                public void someMethod(int num) { System.out.println(num); }
                public void someNewMethod(String arg) { System.out.println(arg); }
            }
        """.trimIndent(),
        expected = """
            package com.example;
            public class MyService {
                public void someMethod(int num, String arg) { System.out.println(num); }
                public void someNewMethod(String arg) { System.out.println(arg); }
            }
        """.trimIndent()
    ) {
        classes({ name == "MyService" }) {
            methods({
                name == "someMethod" &&
                parameters.firstOrNull()?.type?.fqn == "int" &&
                type.fqn.endsWith("void")
            }) {
                parameters += Parameter("String arg")
            }
        }
    }

    @Test
    fun addAnnotation() = modifyJavaTest(
        input = """
            package com.example;
            public class MyService {
                public void someMethod(int num) { System.out.println(num); }
                public void someNewMethod(String arg) { System.out.println(arg); }
            }
        """.trimIndent(),
        expected = """
            package com.example;
            public class MyService {
                public void someMethod(int num) { System.out.println(num); }
                @Deprecated
                public void someNewMethod(String arg) { System.out.println(arg); }
            }
        """.trimIndent()
    ) {
        classes({ name == "MyService" }) {
            methods({ name == "someNewMethod" && type.fqn.endsWith("void") }) {
                annotations += Annotation("Deprecated")
            }
        }
    }
}

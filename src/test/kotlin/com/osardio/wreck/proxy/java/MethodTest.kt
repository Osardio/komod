package com.osardio.wreck.proxy.java

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.stmt.ExpressionStmt
import com.osardio.wreck.classes
import com.osardio.wreck.methods
import com.osardio.wreck.modifyJavaTest
import org.junit.jupiter.api.Test

class MethodTest {

    @Test
    fun renameMethod() {
        val input = """
            package com.example;
            public class MyService {
                public void someMethod(String arg) { System.out.println(arg); }
                public void someMethod(int num) { System.out.println(num); }
            }
        """.trimIndent()

        val expected = """
            package com.example;
            public class MyService {
                public void someNewMethod(String arg) { System.out.println(arg); }
                public void someMethod(int num) { System.out.println(num); }
            }
        """.trimIndent()

        modifyJavaTest(input, expected) {
            classes.methods {
                name == "someMethod" &&
                modifiers.contains(Modifier.Keyword.PUBLIC) &&
                parameters.firstOrNull()?.type?.fqn == "String"
            }.forEach {
                it.name = "someNewMethod"
            }
        }
    }

    @Test
    fun changeAccessModifier() {
        val input = """
            package com.example;
            public class MyService {
                public void someNewMethod(String arg) { System.out.println(arg); }
                public void someMethod(int num) { System.out.println(num); }
            }
        """.trimIndent()

        val expected = """
            package com.example;
            public class MyService {
                public void someNewMethod(String arg) { System.out.println(arg); }
                private void someMethod(int num) { System.out.println(num); }
            }
        """.trimIndent()

        modifyJavaTest(input, expected) {
            classes.methods {
                name == "someMethod" &&
                modifiers.contains(Modifier.Keyword.PUBLIC) &&
                parameters.firstOrNull()?.type?.fqn == "int"
            }.forEach {
                it.modifiers = setOf(Modifier.Keyword.PRIVATE)
            }
        }
    }

    @Test
    fun removeAccessModifier() {
        val input = """
            package com.example;
            public class MyService {
                public void someNewMethod(String arg) { System.out.println(arg); }
                private void someMethod(int num) { System.out.println(num); }
            }
        """.trimIndent()

        val expected = """
            package com.example;
            public class MyService {
                public void someNewMethod(String arg) { System.out.println(arg); }
                void someMethod(int num) { System.out.println(num); }
            }
        """.trimIndent()

        modifyJavaTest(input, expected) {
            classes.methods {
                name == "someMethod" &&
                modifiers.contains(Modifier.Keyword.PRIVATE) &&
                parameters.firstOrNull()?.type?.fqn == "int"
            }.forEach {
                it.modifiers = emptySet()
            }
        }
    }

    @Test
    fun addStatement() {
        val input = """
            package com.example;
            public class MyService {
                public void someNewMethod(String arg) { System.out.println(arg); }
                void someMethod(int num) { System.out.println(num); }
            }
        """.trimIndent()

        val expected = """
            package com.example;
            public class MyService {
                public void someNewMethod(String arg) { System.out.println(arg); }
                void someMethod(int num) {
                    System.out.println(num);
                    System.out.println("Test!");
                }
            }
        """.trimIndent()

        modifyJavaTest(input, expected) {
            classes.methods {
                name == "someMethod" &&
                parameters.firstOrNull()?.type?.fqn == "int"
            }.forEach {
                it.statements += ExpressionStmt(StaticJavaParser.parseExpression("System.out.println(\"Test!\")"))
            } // TODO более удобное создание ExpressionStmt
        }
    }

    @Test
    fun changeReturnType() {
        val input = """
            package com.example;
            public class MyService {
                public void someMethod(int num) { System.out.println(num); }
                public void someNewMethod(String arg) { System.out.println(arg); }
            }
        """.trimIndent()

        val expected = """
            package com.example;
            public class MyService {
                public int someMethod(int num) { System.out.println(num); }
                public void someNewMethod(String arg) { System.out.println(arg); }
            }
        """.trimIndent()

        modifyJavaTest(input, expected) {
            classes.methods {
                name == "someMethod" &&
                parameters.firstOrNull()?.type?.fqn == "int"
            }.forEach {
                it.type = Type("int")
            }
        }
    }
}

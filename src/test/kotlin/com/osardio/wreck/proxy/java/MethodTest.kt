package com.osardio.wreck.proxy.java

import com.osardio.wreck.classes
import com.osardio.wreck.methods
import com.osardio.wreck.modifyJavaTest
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
        classes.methods {
            name == "someMethod" &&
            modifiers.contains(Modifier.Keyword.PUBLIC) &&
            parameters.firstOrNull()?.type?.fqn == "String"
        }.forEach {
            it.name = "someNewMethod"
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
        classes.methods {
            name == "someMethod" &&
            modifiers.contains(Modifier.Keyword.PUBLIC) &&
            parameters.firstOrNull()?.type?.fqn == "int"
        }.forEach {
            it.modifiers = setOf(Modifier.Keyword.PRIVATE)
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
        classes.methods {
            name == "someMethod" &&
            modifiers.contains(Modifier.Keyword.PRIVATE) &&
            parameters.firstOrNull()?.type?.fqn == "int"
        }.forEach {
            it.modifiers = emptySet()
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
        classes.methods {
            name == "someMethod" &&
            parameters.firstOrNull()?.type?.fqn == "int"
        }.forEach {
            it.statements += Statement("System.out.println(\"Test!\");")
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
        classes.methods {
            name == "someMethod" &&
            parameters.firstOrNull()?.type?.fqn == "int" &&
            type.fqn.endsWith("void")
        }.forEach {
            it.type = Type("int")
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
        classes.methods {
            name == "someMethod" &&
            parameters.firstOrNull()?.type?.fqn == "int" &&
            type.fqn.endsWith("void")
        }.forEach {
            it.parameters += Parameter("String arg")
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
        classes.methods {
            name == "someNewMethod" &&
            parameters.firstOrNull()?.type?.fqn == "String" &&
            type.fqn.endsWith("void")
        }.forEach {
            it.annotations += Annotation("Deprecated")
        }
    }
}

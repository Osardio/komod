/*
 * Copyright (C) 2026 Osardio
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.osardio.komod.proxy.java

import com.osardio.komod.classes
import com.osardio.komod.methods
import com.osardio.komod.modifyJavaTest
import com.osardio.komod.parameters
import org.junit.jupiter.api.Test

// TODO field modifier tests
class ModifierTest {

    @Test
    fun changeClassModifier() = modifyJavaTest(
        input = """
            package com.example;
            public class MyService {
                public void someMethod(int num) { System.out.println(num); }
            }
        """.trimIndent(),
        expected = """
            package com.example;
            protected class MyService {
                public void someMethod(int num) { System.out.println(num); }
            }
        """.trimIndent()
    ) {
        classes({ name == "MyService" && modifiers.contains(Modifier.Keyword.PUBLIC) }) {
            modifiers = setOf(Modifier.Keyword.PROTECTED)
        }
    }

    @Test
    fun changeMethodModifier() = modifyJavaTest(
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
    fun changeParameterModifier() = modifyJavaTest(
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
                public void someNewMethod(final String arg) { System.out.println(arg); }
            }
        """.trimIndent()
    ) {
        classes({ name == "MyService" }) {
            methods({ name == "someNewMethod" && type.fqn.endsWith("void") }) {
                parameters({ name == "arg" && type.fqn == "String" }) {
                    modifiers = setOf(Modifier.Keyword.FINAL)
                }
            }
        }
    }

    @Test
    fun addClassModifier() = modifyJavaTest(
        input = """
            package com.example;
            public class MyService {
                public void someNewMethod(String arg) { System.out.println(arg); }
                public void someMethod(int num) { System.out.println(num); }
            }
        """.trimIndent(),
        expected = """
            package com.example;
            public final class MyService {
                public void someNewMethod(String arg) { System.out.println(arg); }
                public void someMethod(int num) { System.out.println(num); }
            }
        """.trimIndent()
    ) {
        classes({ name == "MyService" && modifiers.contains(Modifier.Keyword.PUBLIC) }) {
            modifiers += Modifier.Keyword.FINAL
        }
    }

    @Test
    fun addMethodModifier() = modifyJavaTest(
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
    fun removeClassModifier() = modifyJavaTest(
        input = """
            package com.example;
            public final class MyService {
                public void someNewMethod(String arg) { System.out.println(arg); }
                private void someMethod(int num) { System.out.println(num); }
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
        classes({ name == "MyService" && modifiers.contains(Modifier.Keyword.PUBLIC) }) {
            modifiers -= Modifier.Keyword.FINAL
        }
    }

    @Test
    fun removeMethodModifier() = modifyJavaTest(
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
    fun removeParameterModifier() = modifyJavaTest(
        input = """
            package com.example;
            public class MyService {
                public void someNewMethod(final String arg) { System.out.println(arg); }
                private void someMethod(int num) { System.out.println(num); }
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
            methods({ name == "someNewMethod" && type.fqn.endsWith("void") }) {
                parameters({ name == "arg" && type.fqn == "String" }) {
                    modifiers -= Modifier.Keyword.FINAL
                }
            }
        }
    }

}

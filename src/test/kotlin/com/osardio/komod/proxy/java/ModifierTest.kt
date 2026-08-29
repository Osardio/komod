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
import org.junit.jupiter.api.Test

class ModifierTest {

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

}

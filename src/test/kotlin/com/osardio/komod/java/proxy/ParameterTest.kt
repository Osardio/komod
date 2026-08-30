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

package com.osardio.komod.java.proxy

import com.osardio.komod.java.classes
import com.osardio.komod.java.methods
import com.osardio.komod.modifyJavaTest
import com.osardio.komod.java.parameters
import org.junit.jupiter.api.Test

class ParameterTest {

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
            methods({ name == "someMethod" }) {
                parameters += Parameter("String arg")
            }
        }
    }

    @Test
    fun removeParameter() = modifyJavaTest(
        input = """
            package com.example;
            public class MyService {
                public void someMethod(int num, String arg) { System.out.println(num); }
                public void someNewMethod(String arg) { System.out.println(arg); }
            }
        """.trimIndent(),
        expected = """
            package com.example;
            public class MyService {
                public void someMethod(int num) { System.out.println(num); }
                public void someNewMethod(String arg) { System.out.println(arg); }
            }
        """.trimIndent()
    ) {
        classes({ name == "MyService" }) {
            methods({ name == "someMethod" }) {
                parameters({ name == "arg" }) {
                    remove()
                }
            }
        }
    }

}

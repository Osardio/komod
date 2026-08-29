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

import com.osardio.komod.*
import org.junit.jupiter.api.Test

// TODO remove annotations test
// TODO tests with simple Java annotations - they do not require import
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
    fun addAnnotationToField() = modifyJavaTest(
        input = """
            package com.example;
            public class MyService {
                public String name;
                private int count;
            }
        """.trimIndent(),
        expected = """
            package com.example;
            import java.lang.annotation.Native;
            
            public class MyService {
                @Native
                public String name;
                private int count;
            }
        """.trimIndent()
    ) {
        classes({ name == "MyService" }) {
            fields({ name == "name" && type.fqn.endsWith("String") }) {
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

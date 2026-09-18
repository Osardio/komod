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
import com.osardio.komod.java.fields
import com.osardio.komod.modifyJavaTest
import org.junit.jupiter.api.Test

class FieldTest {

    @Test
    fun renameField() = modifyJavaTest(
        input = """
            package com.example;
            public class MyService {
                public String name;
                private int count;
            }
        """.trimIndent(),
        expected = """
            package com.example;
            public class MyService {
                public String newName;
                private int count;
            }
        """.trimIndent()
    ) {
        classes({ name == "MyService" }) {
            fields({ name == "name" && modifiers.contains(Modifier.Keyword.PUBLIC) }) {
                name = "newName"
            }
        }
    }

    @Test
    fun removeField() = modifyJavaTest(
        input = """
            package com.example;
            public class MyService {
                public String name;
                private int count;
            }
        """.trimIndent(),
        expected = """
            package com.example;
            public class MyService {
                private int count;
            }
        """.trimIndent()
    ) {
        classes({ name == "MyService" }) {
            fields({ name == "name" && modifiers.contains(Modifier.Keyword.PUBLIC) }) {
                remove()
            }
        }
    }
}

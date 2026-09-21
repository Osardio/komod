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

import com.osardio.komod.createTempDir
import com.osardio.komod.java.addAnnotation
import com.osardio.komod.java.classes
import com.osardio.komod.java.java
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JavaFileTest {
    private val input = """
        package com.example;

        public class MyService {
            public void someMethod() {}
        }
    """.trimIndent()

    @Test
    fun commitRewritesOnlyChangedFiles() {
        val dir = createTempDir("MyService.java", input)
        val otherContent = """
            package com.example;

            public class Other {
            }
        """.trimIndent()
        File(dir, "Other.java").writeText(otherContent)
        val service = JavaFile(dir.resolve("MyService.java"))
        val other = JavaFile(dir.resolve("Other.java"))

        service.java {
            classes({ name == "MyService" }) {
                addAnnotation("java.lang.annotation.Native")
            }
        }

        assertTrue(service.commit())
        assertFalse(other.commit())
        val written = dir.resolve("MyService.java").readText()
        assertTrue(written.contains("@Native"))
        assertTrue(written.contains("import java.lang.annotation.Native;"))
        assertTrue(dir.resolve("Other.java").readText() == otherContent)
    }

    @Test
    fun commitWithoutAccumulatedChangesIsNoOp() {
        val dir = createTempDir("MyService.java", input)
        val service = JavaFile(dir.resolve("MyService.java"))

        // no selector ever touches the AST: nothing accumulated, file is neither parsed nor written
        assertFalse(service.commit())
        assertTrue(dir.resolve("MyService.java").readText() == input)
    }

    @Test
    fun lazinessParsingHappensOnFirstAstAccessOnly() {
        val dir = createTempDir("Broken.java", "this is not java !!!")
        val broken = JavaFile(dir.resolve("Broken.java"))

        // an invalid file is only rejected once its AST is actually requested
        assertFalse(broken.commit())
        assertFailsWith<Exception> {
            broken.classes { }
        }
    }

    @Test
    fun addAnnotationIsIdempotent() = com.osardio.komod.modifyJavaTest(
        input = """
            package com.example;
            import java.lang.annotation.Native;

            @Native
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
}

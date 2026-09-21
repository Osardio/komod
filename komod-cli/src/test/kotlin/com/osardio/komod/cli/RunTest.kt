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

package com.osardio.komod.cli

import com.osardio.komod.Modification
import com.osardio.komod.java.addAnnotation
import com.osardio.komod.java.classes
import com.osardio.komod.java.java
import com.osardio.komod.ProjectFile
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Recipe that adds an annotation to every class touching the AST of each file. */
class AddNative : Modification {
    override fun modification(): ProjectFile.() -> Unit = {
        java {
            classes {
                addAnnotation("java.lang.annotation.Native")
            }
        }
    }
}

class RunTest {
    private val fakeJars = listOf(File("nonexistent.jar"))
    private val resolver: (String) -> ResolveOutcome = { Resolved(fakeJars) }
    private val loader: (String, List<File>) -> Result<Recipe> =
        { _, _ -> Result.success(Recipe(AddNative(), AddNative::class.java.classLoader)) }

    private fun recipeArgs(target: File): Array<String> =
        arrayOf("-a", "g:recipe:1.0", "-m", AddNative::class.java.name, "-t", target.absolutePath)

    @Test
    fun parseSkipIsNotFatal() {
        val root = File(System.getProperty("java.io.tmpdir"), "komod-skip-" + java.util.UUID.randomUUID())
        root.mkdirs()
        File(root, "Ok.java").writeText("public class Ok {}")
        File(root, "Broken.java").writeText("this is not java !!!")

        val code = runMain(recipeArgs(root), resolver, loader)

        assertEquals(0, code)
        assertTrue(File(root, "Ok.java").readText().contains("@Native"))
        assertTrue(File(root, "Broken.java").readText() == "this is not java !!!")
    }

    @Test
    fun recipeIsIdempotent() {
        val root = File(System.getProperty("java.io.tmpdir"), "komod-e2e-" + java.util.UUID.randomUUID())
        root.mkdirs()
        File(root, "Service.java").writeText("public class Service { public void go() {} }")

        assertEquals(0, runMain(recipeArgs(root), resolver, loader))
        val once = File(root, "Service.java").readText()
        assertTrue(once.contains("@Native"))
        assertTrue(once.contains("import java.lang.annotation.Native;"))
        assertEquals(1, once.count { it == '@' }, "annotation added exactly once")

        // second run on the already modified file: no change, no duplicate annotation
        assertEquals(0, runMain(recipeArgs(root), resolver, loader))
        val twice = File(root, "Service.java").readText()
        assertEquals(once, twice, "second run must not alter the file")
        assertEquals(1, twice.count { it == '@' }, "annotation must remain single after re-run")
    }
}

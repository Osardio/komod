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
import com.osardio.komod.ProjectFile
import com.osardio.komod.loadProjectFileFactories
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Builds a small project tree on disk for scanning. */
private fun projectTree(): File {
    val root = File(System.getProperty("java.io.tmpdir"), "komod-walk-" + java.util.UUID.randomUUID())
    root.mkdirs()
    File(root, "A.java").writeText("public class A {}")
    File(root, "B.txt").writeText("not java")
    val build = File(root, "build"); build.mkdirs()
    File(build, "C.java").writeText("public class C {}")
    val git = File(root, ".git"); git.mkdirs()
    File(git, "D.java").writeText("public class D {}")
    val out = File(root, "out"); out.mkdirs()
    File(out, "E.java").writeText("public class E {}")
    val sub = File(root, "src/main"); sub.mkdirs()
    File(sub, "F.java").writeText("public class F {}")
    val target = File(root, "target"); target.mkdirs()
    File(target, "G.java").writeText("public class G {}")
    return root
}

class WalkTest {
    private val factories = loadProjectFileFactories(Modification::class.java.classLoader)

    @Test
    fun scanProjectSkipsExcludedDirs() {
        val root = projectTree()
        val files = scanProject(root, noExcludes = false, factories = factories)
        val names = files.map { it.name }.toSet()
        assertTrue("A.java" in names)
        assertTrue("F.java" in names)
        assertTrue("C.java" !in names, "build dir excluded")
        assertTrue("D.java" !in names, ".git dir excluded")
        assertTrue("E.java" !in names, "out dir excluded")
        assertTrue("G.java" !in names, "target dir excluded")
        assertTrue(files.all { it.file.extension == "java" }, "non-java files skipped")
    }

    @Test
    fun scanProjectWithNoExcludesIncludesEverything() {
        val root = projectTree()
        val files = scanProject(root, noExcludes = true, factories = factories)
        val names = files.map { it.name }.toSet()
        assertTrue("C.java" in names || "D.java" in names || "E.java" in names || "G.java" in names)
        assertTrue(files.all { it.file.extension == "java" })
    }
}

class ExitCode {
    /** No-op modification: touches no file, so nothing is committed. */
    class Noop : Modification {
        override fun modification(): ProjectFile.() -> Unit = { }
    }

    private val fakeJars = listOf(File("nonexistent.jar"))
    private val loader: (String, List<File>) -> Result<Recipe> =
        { _, _ -> Result.success(Recipe(Noop(), Noop::class.java.classLoader)) }
    private val resolver: (String) -> ResolveOutcome = { Resolved(fakeJars) }

    @Test
    fun targetNotADirectoryIsFailure() {
        val root = projectTree()
        val code = runMain(
            arrayOf("-a", "g:a:1", "-m", Noop::class.java.name, "-t", "${root}/A.java"),
            resolver, loader
        )
        assertEquals(1, code)
    }

    @Test
    fun noopModificationOnValidTargetSucceeds() {
        val root = projectTree()
        val code = runMain(
            arrayOf("-a", "g:a:1", "-m", Noop::class.java.name, "-t", root.absolutePath),
            resolver, loader
        )
        assertEquals(0, code)
    }

    @Test
    fun resolveFailureIsFailure() {
        val root = projectTree()
        val badResolver: (String) -> ResolveOutcome = { ResolveError("not found $it") }
        val code = runMain(
            arrayOf("-a", "g:a:1", "-m", Noop::class.java.name, "-t", root.absolutePath),
            badResolver, loader
        )
        assertEquals(1, code)
    }

    @Test
    fun loadFailureIsFailure() {
        val root = projectTree()
        val badLoader: (String, List<File>) -> Result<Recipe> = { _, _ -> Result.failure(IllegalStateException("bad")) }
        val code = runMain(
            arrayOf("-a", "g:a:1", "-m", "Missing", "-t", root.absolutePath),
            resolver, badLoader
        )
        assertEquals(1, code)
    }
}

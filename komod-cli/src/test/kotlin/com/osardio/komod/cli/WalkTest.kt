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

import com.osardio.komod.core.Modification
import com.osardio.komod.core.loadProjectFileFactories
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class WalkTest {

    private val factories = loadProjectFileFactories(Modification::class.java.classLoader)

    @Test
    fun scanProjectSkipsExcludedDirs() {
        val root = projectTree()
        val files = scanProject(root, noExcludes = false, factories = factories)
        val names = files.map { it.name }.toSet()
        Assertions.assertTrue("A.java" in names)
        Assertions.assertTrue("F.java" in names)
        Assertions.assertTrue("C.java" !in names, "build dir excluded")
        Assertions.assertTrue("D.java" !in names, ".git dir excluded")
        Assertions.assertTrue("E.java" !in names, "out dir excluded")
        Assertions.assertTrue("G.java" !in names, "target dir excluded")
        Assertions.assertTrue(files.all { it.file.extension == "java" }, "non-java files skipped")
    }

    @Test
    fun scanProjectWithNoExcludesIncludesEverything() {
        val root = projectTree()
        val files = scanProject(root, noExcludes = true, factories = factories)
        val names = files.map { it.name }.toSet()
        Assertions.assertTrue("C.java" in names || "D.java" in names || "E.java" in names || "G.java" in names)
        Assertions.assertTrue(files.all { it.file.extension == "java" })
    }
}

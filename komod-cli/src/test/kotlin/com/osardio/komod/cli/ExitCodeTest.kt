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
import com.osardio.komod.core.ProjectFile
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class ExitCodeTest {

    private val fakeJars = listOf(File("nonexistent.jar"))
    private val goodResolver = mockk<ArtifactResolver>()
    private val badResolver = mockk<ArtifactResolver>()
    private val badLoader = mockk<ModificationLoader>()

    /** No-op modification: touches no file, so nothing is committed. */
    class Noop : Modification {

        override fun modification(): ProjectFile.() -> Unit = { }
    }

    @BeforeEach
    fun setup() {
        every { goodResolver.resolve(any<String>()) } returns fakeJars
        every { badResolver.resolve(any()) } throws ResolutionException("error")
        every { badLoader.loadModification(any(), any()) } throws IllegalStateException("bad")
    }

    @Test
    fun targetNotADirectoryIsFailure() {
        val root = projectTree()
        val code = ModificationRunner().run(
            arrayOf("-a", "g:a:1", "-m", Noop::class.java.name, "-t", "${root}/A.java")
        )
        assertEquals(1, code)
    }

    @Test
    fun noopModificationOnValidTargetSucceeds() {
        val root = projectTree()
        val code = ModificationRunner(goodResolver).run(
            arrayOf("-a", "g:a:1", "-m", Noop::class.java.name, "-t", root.absolutePath)
        )
        assertEquals(0, code)
    }

    @Test
    fun resolveFailureIsFailure() {
        val root = projectTree()
        val code = ModificationRunner(badResolver, ModificationLoader()).run(
            arrayOf("-a", "g:a:1", "-m", Noop::class.java.name, "-t", root.absolutePath)
        )
        assertEquals(1, code)
    }

    @Test
    fun loadFailureIsFailure() {
        val root = projectTree()
        val code = ModificationRunner(ArtifactResolver(), badLoader).run(
            arrayOf("-a", "g:a:1", "-m", "Missing", "-t", root.absolutePath)
        )
        assertEquals(1, code)
    }
}

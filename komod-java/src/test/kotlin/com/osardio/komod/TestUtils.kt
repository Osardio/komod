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

package com.osardio.komod

import com.osardio.komod.java.proxy.JavaFile
import java.io.File
import kotlin.test.assertEquals

/**
 * Helper for testing modifications on a single Java file.
 * @param input original file content
 * @param expected expected content after the modification
 * @param block DSL block that runs in the context of this file
 */
fun modifyJavaTest(input: String, expected: String, block: JavaFile.() -> Unit) {
    val testFile = JavaFile("Test.java", input)
    testFile.block()
    testFile.applyChanges()
    val actual = testFile.getContent()
    assertEquals(expected, actual)
}

/** Creates a unique temp directory containing `name` with the given [content]; returns the directory. */
fun createTempDir(name: String, content: String): File {
    val dir = File(System.getProperty("java.io.tmpdir"), "komod-" + java.util.UUID.randomUUID())
    check(dir.mkdirs())
    File(dir, name).writeText(content)
    return dir
}

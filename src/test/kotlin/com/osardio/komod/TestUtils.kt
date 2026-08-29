package com.osardio.komod

import com.osardio.komod.proxy.java.File
import kotlin.test.assertEquals

/**
 * Helper for testing modifications on a single Java file.
 * @param input original file content
 * @param expected expected content after the modification
 * @param block DSL block that runs in the context of this file
 */
fun modifyJavaTest(input: String, expected: String, block: File.() -> Unit) {
    val testFile = File("Test.java", input)
    testFile.block()
    testFile.applyChanges()
    val actual = testFile.getContent()
    assertEquals(expected, actual)
}

package com.osardio.wreck

import com.osardio.wreck.proxy.java.File
import kotlin.test.assertEquals

/**
 * Хелпер для тестирования модификаций над одним Java-файлом.
 * @param input исходное содержимое файла
 * @param expected ожидаемое содержимое после применения модификации
 * @param block DSL-блок, который выполняется в контексте этого файла
 */
fun modifyJavaTest(input: String, expected: String, block: File.() -> Unit) {
    val testFile = File("Test.java", input)
    testFile.block()
    testFile.applyChanges()
    val actual = testFile.getContent()
    assertEquals(expected, actual)
}
package com.osardio.wreck

import com.osardio.wreck.context.ModificationContext
import com.osardio.wreck.proxy.java.File
import kotlin.test.assertEquals

/**
 * Хелпер для тестирования модификаций над одним Java-файлом.
 * @param input исходное содержимое файла
 * @param expected ожидаемое содержимое после применения модификации
 * @param block DSL-блок, который будет применён к контексту с этим файлом
 */
fun modifyJavaTest(input: String, expected: String, block: ModificationContext.() -> Unit) {
    val testFile = File("Test.java", input)
    val context = ModificationContext(JavaFileSet(listOf(testFile)))
    context.block()
    context.javaFiles.applyAll()
    val actual = testFile.getContent()
    assertEquals(expected, actual)
}

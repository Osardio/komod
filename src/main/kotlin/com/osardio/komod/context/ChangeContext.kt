package com.osardio.komod.context

class ChangeContext {
    private val changes = mutableListOf<() -> Unit>()
    fun add(change: () -> Unit) { changes.add(change) }
    fun applyAll() { changes.forEach { it() } }
}

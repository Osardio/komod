package com.osardio.komod.proxy

import com.osardio.komod.context.ChangeContext

abstract class NodeProxy<T>(protected val ctx: ChangeContext, internal open val ast: T) {
    protected fun update(action: T.() -> Unit) {
        ctx.add { ast.action() }
    }
}

package com.osardio.wreck.proxy

import com.osardio.wreck.context.ChangeContext

abstract class NodeProxy<T>(protected val ctx: ChangeContext, internal open val ast: T) {
    protected fun update(action: T.() -> Unit) {
        ctx.add { ast.action() }
    }
}

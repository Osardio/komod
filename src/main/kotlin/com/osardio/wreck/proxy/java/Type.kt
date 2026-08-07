package com.osardio.wreck.proxy.java

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.type.Type
import com.osardio.wreck.context.ChangeContext
import com.osardio.wreck.proxy.NodeProxy

class Type(
    ctx: ChangeContext,
    override val ast: Type
) : NodeProxy<Type>(ctx, ast) {
    constructor(value: String) : this(ChangeContext(), StaticJavaParser.parseType(value))

    val fqn: String get() = ast.asString()
}

package com.osardio.komod.proxy.java

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.ImportDeclaration
import com.osardio.komod.context.ChangeContext
import com.osardio.komod.proxy.NodeProxy

class Import(
    ctx: ChangeContext,
    override val ast: ImportDeclaration
) : NodeProxy<ImportDeclaration>(ctx, ast) {
    constructor(fqn: String) : this(ChangeContext(), StaticJavaParser.parseImport("import $fqn;"))
}

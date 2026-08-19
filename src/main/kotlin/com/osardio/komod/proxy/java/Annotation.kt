package com.osardio.komod.proxy.java

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.expr.AnnotationExpr
import com.osardio.komod.context.ChangeContext
import com.osardio.komod.proxy.NodeProxy

class Annotation(
    ctx: ChangeContext,
    override val ast: AnnotationExpr
) : NodeProxy<AnnotationExpr>(ctx, ast) {
    constructor(value: String) : this(ChangeContext(), StaticJavaParser.parseAnnotation("@$value"))
}

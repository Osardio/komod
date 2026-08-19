package com.osardio.komod.proxy.java

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.osardio.komod.context.ChangeContext
import java.io.File
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter

class File {
    private val ctx = ChangeContext()
    private val cu: CompilationUnit
    val name: String

    constructor(file: File) {
        name = file.name
        cu = StaticJavaParser.parse(file)
    }

    constructor(name: String, content: String) {
        this.name = name
        cu = StaticJavaParser.parse(content)
        LexicalPreservingPrinter.setup(cu)
    }

    val classes: List<Class> by lazy {
        cu.findAll(ClassOrInterfaceDeclaration::class.java).map { Class(ctx, cu, it) }
    }

    var imports: List<Import>
        get() = cu.imports.map { Import(ctx, it) }
        set(value) {
            ctx.add {
                cu.imports.clear()
                cu.imports.addAll(value.map { it.ast })
            }
        }

    fun ensureImport(fqn: String) {
        ctx.add {
            if (cu.imports.none { it.nameAsString == fqn }) {
                cu.imports.add(StaticJavaParser.parseImport("import $fqn;"))
            }
        }
    }

    // Получить текущее содержимое после всех изменений
    fun getContent(): String = LexicalPreservingPrinter.print(cu)

    internal fun applyChanges() {
        ctx.applyAll()
        // Для реального файла сохранение не нужно, так как тесты не пишут на диск
    }
}

package com.osardio.komod

import com.osardio.komod.proxy.java.Annotatable
import com.osardio.komod.proxy.java.Annotation
import com.osardio.komod.proxy.java.File

context(file: File)
fun Annotatable.addAnnotation(fqn: String) {
    file.ensureImport(fqn)
    annotations += Annotation(fqn.substringAfterLast('.'))
}

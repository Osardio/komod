/*
 * Copyright (C) 2026 Osardio
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.osardio.komod.cli

import com.osardio.komod.core.Modification
import com.osardio.komod.core.ProjectFile
import com.osardio.komod.core.ProjectFileFactory
import java.io.File

/** Classloader that can see komod-core, used as the parent of the recipe classloader. */
internal val CoreClassLoader: ClassLoader
    get() = Modification::class.java.classLoader

internal val EXCLUDED_DIRECTORIES = setOf("build", "out", "target")

internal fun parseCoordinates(coordinates: String): Triple<String, String, String>? =
    coordinates.split(':').takeIf { it.size == 3 && it.all { part -> part.isNotBlank() } }
        ?.let { Triple(it[0], it[1], it[2]) }

/**
 * Lazily walks [root] collecting [ProjectFile] instances for the kinds the given [factories] support
 * (files are parsed on first AST access). [noExcludes] disables the default directory exclusion rules.
 */
internal fun scanProject(root: File, noExcludes: Boolean, factories: List<ProjectFileFactory>): List<ProjectFile> {
    return root.walkTopDown().filter { it.isFile }.mapNotNull { file ->
        if (noExcludes || !isExcluded(root, file)) {
            factories.firstNotNullOfOrNull { it.create(file.absoluteFile) }
        } else null
    }.toList()
}

private fun isExcluded(root: File, file: File): Boolean {
    val path = file.parentFile!!.relativeTo(root).path
    return path.split(File.separatorChar).any { name ->
        name.startsWith('.') || name in EXCLUDED_DIRECTORIES
    }
}

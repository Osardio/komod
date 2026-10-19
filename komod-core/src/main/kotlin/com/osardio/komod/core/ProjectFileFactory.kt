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

package com.osardio.komod.core

import java.io.File
import java.util.ServiceLoader

/**
 * Creates [ProjectFile] instances for the file kinds a specific implementation supports.
 * Language modules (e.g. `komod-java`) register a factory via `META-INF/services/com.osardio.komod.ProjectFileFactory`,
 * so the orchestration layer (CLI) stays independent of any particular file kind.
 */
fun interface ProjectFileFactory {
    /** @return a [ProjectFile] for [file] if this factory handles the file kind, otherwise `null`. */
    fun create(file: File): ProjectFile?
}

/** Loads all [ProjectFileFactory] providers visible from [classLoader]. */
fun loadProjectFileFactories(classLoader: ClassLoader): List<ProjectFileFactory> =
    ServiceLoader.load(ProjectFileFactory::class.java, classLoader).toList()

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
import java.io.File
import java.net.URLClassLoader

/**
 * Loads a [Modification] implementation from resolved artifact jars using an isolated classloader.
 */
class ModificationLoader {

    fun loadModification(fqn: String, jars: List<File>): LoadedModification {
        val classLoader = URLClassLoader(jars.map { it.toURI().toURL() }.toTypedArray(), CoreClassLoader)
        val type = try {
            Class.forName(fqn, true, classLoader)
        } catch (_: ClassNotFoundException) {
            throw IllegalArgumentException("Class $fqn not found in the resolved artifact")
        }
        require(Modification::class.java.isAssignableFrom(type)) {
            "$fqn does not implement ${Modification::class.java.name}"
        }
        val modification = try {
            type.getConstructor().newInstance() as Modification
        } catch (_: NoSuchMethodException) {
            throw IllegalArgumentException("$fqn has no default constructor")
        }
        return LoadedModification(modification, classLoader)
    }
}

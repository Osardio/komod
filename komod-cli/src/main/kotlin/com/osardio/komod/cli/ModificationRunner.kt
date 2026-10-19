/* Copyright (C) 2026 Osardio
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

import com.osardio.komod.core.loadProjectFileFactories
import java.io.File

/**
 * Encapsulates the full modification workflow:
 *  - argument parsing
 *  - artifact coordinate resolution via [ArtifactResolver]
 *  - Modification loading via [ModificationLoader]
 *  - project file scanning
 *  - modification application + commit
 */
internal class ModificationRunner(
    private val artifactResolver: ArtifactResolver = ArtifactResolver(),
    private val modificationLoader: ModificationLoader = ModificationLoader()
) {

    internal fun run(args: Array<String>): Int {
        val cmd = CommandLine()
        try {
            cmd.parse(args)
        } catch (e: Exception) {
            return fail(e.message ?: e.toString())
        }
        val coordinates = cmd.artifact
        val modificationFqn = cmd.modification
        val target = File(cmd.target)
        if (!target.isDirectory) return fail("Target '${target.absolutePath}' is not a directory")

        if (cmd.verbose) System.err.println("Resolving $coordinates...")
        val files = try {
            artifactResolver.resolve(coordinates)
        } catch (e: ResolutionException) {
            return fail(e.message ?: "Resolution failed")
        }

        if (cmd.verbose) {
            files.forEach { System.err.println("  ${it.absolutePath}") }
        }

        val loadedModification = try {
            modificationLoader.loadModification(modificationFqn, files)
        } catch (e: Exception) {
            return fail("Failed to load $modificationFqn: ${e.message}")
        }
        val modification = loadedModification.modification

        val projectFiles = try {
            scanProject(target, cmd.noExcludes, loadProjectFileFactories(loadedModification.classLoader))
        } catch (e: Exception) {
            return fail(e.message ?: e.toString())
        }

        if (cmd.verbose) System.err.println("Found ${projectFiles.size} file(s) in ${target.absolutePath}")

        val block = runCatching { modification.modification() }
            .getOrElse { e -> return fail("Modification.modification() failed: ${e.stackTraceToString()}") }

        val skipped = mutableListOf<String>()
        projectFiles.forEach { projectFile ->
            try {
                projectFile.block()
            } catch (e: Exception) {
                skipped += "${projectFile.file.absolutePath}: ${shortReason(e)}"
            }
        }

        val modified = mutableListOf<String>()
        for (projectFile in projectFiles) {
            try {
                if (projectFile.commit()) modified += projectFile.file.absolutePath
            } catch (e: Exception) {
                return fail("Failed to write ${projectFile.file.absolutePath}: ${e.message}")
            }
        }

        skipped.forEach { System.err.println("Skip $it") }
        if (cmd.verbose) {
            modified.forEach { println("Modified $it") }
        }
        println(if (modified.isEmpty()) "No changes." else "Modified ${modified.size} file(s).")
        return 0
    }

    private fun fail(message: String): Int {
        System.err.println("Error: $message")
        return 1
    }

    private fun shortReason(e: Exception): String {
        val text = firstExceptionMessage(e) ?: e::class.simpleName.orEmpty()
        val firstSentence = text.lines().first().substringBefore('.').trim()
        return firstSentence.ifEmpty { text }
    }

    private fun firstExceptionMessage(e: Throwable): String? {
        var cause: Throwable = e
        while ((cause.cause != null) && (cause.cause !== cause)) cause = cause.cause!!
        return cause.message
    }
}

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

import com.osardio.komod.loadProjectFileFactories
import java.io.File
import kotlin.system.exitProcess
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required

internal class CommandLine {
    val parser = ArgParser("komod")
    val artifact by parser.option(
        ArgType.String, "artifact", "a",
        "Maven coordinates of the recipe artifact: groupId:artifactId:version"
    ).required()
    val modification by parser.option(
        ArgType.String, "modification", "m",
        "FQN of the Modification implementation class"
    ).required()
    val target by parser.option(
        ArgType.String, "target", "t",
        "Directory of the target project (default: the current directory)"
    ).default(File(".").absoluteFile.path)
    val verbose by parser.option(
        ArgType.Boolean, "verbose", "v",
        "Print changed/skipped files with absolute paths"
    ).default(false)
    val noExcludes by parser.option(
        ArgType.Boolean, "no-excludes", "w",
        "Disable the default directory exclusions of the target scan (e.g., build, .gradle)"
    ).default(false)

    fun parse(args: Array<String>) {
        parser.parse(args)
    }
}

fun main(args: Array<String>) {
    exitProcess(runMain(args))
}

internal fun runMain(
    args: Array<String>,
    resolve: (String) -> ResolveOutcome = { ArtifactResolver().resolve(it) },
    load: (String, List<File>) -> Result<Recipe> = ::loadModification,
): Int {
    val cmd = CommandLine()
    runCatching { cmd.parse(args) }
    val coordinates = cmd.artifact ?: return fail("--artifact is required")
    val modificationFqn = cmd.modification ?: return fail("--modification is required")
    val target = File(cmd.target)
    if (!target.isDirectory) return fail("Target '${target.absolutePath}' is not a directory")

    if (cmd.verbose) System.err.println("Resolving $coordinates...")
    val resolved = when (val outcome = resolve(coordinates)) {
        is Resolved -> outcome
        is ResolveError -> return fail(outcome.message)
    }
    if (cmd.verbose) {
        resolved.files.forEach { System.err.println("  ${it.absolutePath}") }
    }

    val recipe = load(modificationFqn, resolved.files).getOrElse { e ->
        return fail("Failed to load $modificationFqn: ${e.message}")
    }
    val modification = recipe.modification

    val projectFiles = try {
        scanProject(target, cmd.noExcludes, loadProjectFileFactories(recipe.classLoader))
    } catch (e: Exception) {
        return fail(e.message ?: e.toString())
    }
    if (cmd.verbose) System.err.println("Found ${projectFiles.size} file(s) in ${target.absolutePath}")

    val block = runCatching { modification.modification() }
        .getOrElse { e ->
            return fail("Modification.modification() failed: ${e.stackTraceToString()}")
        }

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

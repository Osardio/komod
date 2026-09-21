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

import com.osardio.komod.Modification
import org.eclipse.aether.DefaultRepositorySystemSession
import org.eclipse.aether.artifact.Artifact
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.collection.CollectRequest
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.graph.DependencyNode
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.ArtifactRequest
import java.io.File
import java.net.URLClassLoader

sealed interface ResolveOutcome

data class Resolved(val files: List<File>) : ResolveOutcome

data class ResolveError(val message: String) : ResolveOutcome

internal fun parseCoordinates(coordinates: String): Triple<String, String, String>? =
    coordinates.split(':').takeIf { it.size == 3 && it.all { part -> part.isNotBlank() } }
        ?.let { Triple(it[0], it[1], it[2]) }

class ArtifactResolver(localRepository: File? = null) {
    private val localRepoFile: File = localRepository
        ?: File(System.getProperty("user.home"), ".m2/repository").apply { mkdirs() }
    private val repositorySystem = KomodRepositorySystemSupplier().get()
    private val session = DefaultRepositorySystemSession().apply {
        localRepositoryManager = repositorySystem.newLocalRepositoryManager(this, LocalRepository(localRepoFile))
    }
    private val central = RemoteRepository.Builder("central", "default", "https://repo1.maven.org/maven2").build()

    /**
     * Resolves the full dependency graph of `groupId:artifactId:version` (local repository first,
     * then Maven Central) and returns all collected jar files cached in the local repository.
     */
    fun resolve(coordinates: String): ResolveOutcome {
        val (group, artifact, version) = parseCoordinates(coordinates)
            ?: return ResolveError("Invalid artifact coordinates '$coordinates', expected groupId:artifactId:version")
        val root = Dependency(DefaultArtifact(group, artifact, "", "jar", version), "compile")
        val coords = "$group:$artifact:$version"
        return try {
            val collectResult = repositorySystem.collectDependencies(session, CollectRequest(root, listOf(central)))
            if (collectResult.exceptions.isNotEmpty()) {
                return ResolveError("Failed to resolve $coords: ${rootCauseMessage(collectResult.exceptions.first())}")
            }

            val jars = linkedMapOf<Artifact, Artifact>()
            collectJars(collectResult.root, jars)
            if (jars.isEmpty()) {
                return ResolveError("Resolved $coords contains no jar artifacts")
            }

            val results = repositorySystem.resolveArtifacts(
                session,
                jars.keys.map { ArtifactRequest(it, listOf(central), null) }
            )
            val missing = results.filter { it.isMissing || !it.isResolved }
            if (missing.isNotEmpty()) {
                val detail = missing.first().exceptions.firstOrNull()?.let { rootCauseMessage(it) }
                    ?: "artifact not found"
                return ResolveError("Failed to resolve $coords: $detail")
            }
            Resolved(results.map { it.artifact.file })
        } catch (e: Exception) {
            ResolveError("Failed to resolve $coords: ${rootCauseMessage(e)}")
        }
    }

    private fun rootCauseMessage(t: Throwable): String {
        var cause: Throwable = t
        while ((cause.cause != null) && (cause.cause !== cause)) cause = cause.cause!!
        return cause.message ?: cause::class.simpleName.orEmpty()
    }

    private fun collectJars(node: DependencyNode, out: MutableMap<Artifact, Artifact>) {
        val artifact = node.artifact
        if (artifact.extension.isNotEmpty() && artifact.extension != "pom") {
            out.putIfAbsent(artifact, artifact)
        }
        if (node.dependency?.optional != true) {
            node.children.forEach { collectJars(it, out) }
        }
    }
}

/** Classloader that can see kommod-core, used as the parent of the recipe classloader. */
internal val CoreClassLoader: ClassLoader
    get() = Modification::class.java.classLoader

/** A loaded [Modification] together with the classloader that can see the implementation (and its file-kind providers). */
data class Recipe(val modification: Modification, val classLoader: ClassLoader)

internal fun loadModification(fqn: String, jars: List<File>): Result<Recipe> = runCatching {
    val classLoader = URLClassLoader(jars.map { it.toURI().toURL() }.toTypedArray(), CoreClassLoader)
    val type = try {
        Class.forName(fqn, true, classLoader)
    } catch (e: ClassNotFoundException) {
        throw IllegalArgumentException("Class $fqn not found in the resolved artifact")
    }
    require(Modification::class.java.isAssignableFrom(type)) {
        "$fqn does not implement ${Modification::class.java.name}"
    }
    try {
        val modification = type.getConstructor().newInstance() as Modification
        Recipe(modification, classLoader)
    } catch (e: NoSuchMethodException) {
        throw IllegalArgumentException("$fqn has no default constructor")
    }
}

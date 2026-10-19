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

class ArtifactResolver {

    private val localRepoFile: File = File(System.getProperty("user.home"), ".m2/repository").apply { mkdirs() }
    private val repositorySystem = KomodRepositorySystemSupplier().get()
    private val session = DefaultRepositorySystemSession().apply {
        localRepositoryManager = repositorySystem.newLocalRepositoryManager(this, LocalRepository(localRepoFile))
    }
    private val central = RemoteRepository.Builder("central", "default", "https://repo1.maven.org/maven2").build()

    /**
     * Resolves the full dependency graph of `groupId:artifactId:version` (local repository first,
     * then Maven Central) and returns all collected jar files cached in the local repository.
     */
    fun resolve(coordinates: String): List<File> {
        val (group, artifact, version) = parseCoordinates(coordinates)
            ?: throw ResolutionException("Invalid artifact coordinates '$coordinates', expected groupId:artifactId:version")
        val root = Dependency(DefaultArtifact(group, artifact, "", "jar", version), "compile")
        val coords = "$group:$artifact:$version"
        return try {
            val collectResult = repositorySystem.collectDependencies(session, CollectRequest(root, listOf(central)))
            if (collectResult.exceptions.isNotEmpty()) {
                throw ResolutionException("Failed to resolve $coords: ${rootCauseMessage(collectResult.exceptions.first())}")
            }

            val jars = linkedMapOf<Artifact, Artifact>()
            collectJars(collectResult.root, jars)
            if (jars.isEmpty()) {
                throw ResolutionException("Resolved $coords contains no jar artifacts")
            }

            val results = repositorySystem.resolveArtifacts(
                session,
                jars.keys.map { ArtifactRequest(it, listOf(central), null) }
            )
            val missing = results.filter { it.isMissing || !it.isResolved }
            if (missing.isNotEmpty()) {
                val detail = missing.first().exceptions.firstOrNull()?.let { rootCauseMessage(it) }
                    ?: "artifact not found"
                throw ResolutionException("Failed to resolve $coords: $detail")
            }
            return results.map { it.artifact.file }
        } catch (e: Exception) {
            throw ResolutionException("Failed to resolve $coords: ${rootCauseMessage(e)}")
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


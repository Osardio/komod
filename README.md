# Komod

[![English](https://img.shields.io/badge/English-README-green?style=flat-square)](README.md)
[![Русский](https://img.shields.io/badge/Русский-README-blue?style=flat-square)](README.ru.md)

Komod is a Kotlin DSL library for automatic code refactoring built on top of [JavaParser](https://github.com/javaparser/javaparser). It lets you write small, 
type-safe code transformation recipes and apply them to Java projects from the command line.

## Writing a modification

Code modification is a Kotlin class implementing `Modification`:

```kotlin
class MyModifier : Modification {
    override fun modification(): ProjectFile.() -> Unit = {
        java {
            classes({ name == "FooService" }) {
                methods({ name == "someMethod" }) {
                    name = "someNewMethod"
                }
            }
        }
    }
}
```

- `java { }` opens a single Java file; selectors (`classes`, `methods`, `fields`, `parameters`, `statements`) narrow the scope.
- Selector syntax: `classes({ filter }) { action }` — the filter goes in parentheses, the action in braces.
- Actions are **idempotent**: running the same recipe twice changes nothing the second time.

Compile your recipe against Komod and install the artifact to your local Maven repository (`~/.m2`) or publish it to any Maven repository.

## Building the CLI

```bash
./gradlew :komod-cli:shadowJar
# → komod-cli/build/libs/komod-cli-<version>-all.jar
```

Or with a launcher script:

```bash
./gradlew :komod-cli:installDist    # → komod-cli/build/install/komod/bin/komod
```

## Running the CLI

```bash
komod -a groupId:artifactId:version -m com.example.MyModifier -t /path/to/project [-v] [-w]
```

| Flag | Meaning |
|------|---------|
| `-a/--artifact` | Maven coordinates of your recipe artifact (`groupId:artifactId:version`) |
| `-m/--modification` | Fully qualified class name of your `Modification` |
| `-t/--target` | Target project directory (default: current directory) |
| `-v/--verbose` | Print modified and skipped files with absolute paths |
| `-w/--no-excludes` | Disable default directory exclusions (`.git`, `build`, `target`, etc.) |
| `-h/--help` | Show help |

Exit codes: `0` — success; `1` — setup failure (unresolvable artifact, bad class or target directory).

## Verifying the CLI

The fastest way to check that everything works end-to-end is to run the included smoke test script:

```bash
./e2e-cli-test.sh
```

It publishes `komod-core`, `komod-java` and the sample recipe to `~/.m2`, builds the CLI distribution, and runs the `komod` binary against a sample Java file (`FooService`). The script assumes `java` on PATH is JDK 25 (or `JAVA_HOME` points to it).

Expected output:

```
Modified 1 file(s).
```

Requires `JAVA_HOME` pointing to JDK 25 (the `komod` launcher is compiled for JDK 25).

## Example

Before:

```java
package com.example;

public class FooService {
    public String someMethod() {
        return "hello";
    }
}
```

After running the recipe above:

```java
package com.example;

public class FooService {
    public String someNewMethod() {
        return "hello";
    }
}
```

## Status

Early development (alpha). The CLI works end-to-end; not yet production-ready.

## License

Apache License 2.0 — see [LICENSE](LICENSE) for details.

## Acknowledgments

This project uses [JavaParser](https://github.com/javaparser/javaparser) under the terms of the Apache License, Version 2.0.

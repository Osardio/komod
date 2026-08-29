# Komod

A Kotlin DSL library for automatic Java code refactoring, built on top
of [JavaParser](https://github.com/javaparser/javaparser).

## Why?

Most Java refactoring tools, like OpenRewrite, require verbose configuration. Komod provides a type-safe Kotlin DSL that
lets you express transformations concisely:

```kotlin
// Modification: rename all methods with "oldName" to "newName"
classes {
    methods({ name == "oldName" }) {
        name = "newName"
    }
}
```

## Plans

1. Interface which contains modification code, to be used with CLI tool
2. CLI tool for applying modifications written by library user, as like OpenRewrite recipes

## Status

Early development (alpha) — not ready for production use.

## License

Apache License 2.0 — see [LICENSE](LICENSE) for details.

## Acknowledgments

This project uses [JavaParser](https://github.com/javaparser/javaparser)
under the terms of the Apache License, Version 2.0.

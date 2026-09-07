# Komod

[![English](https://img.shields.io/badge/English-README-green?style=flat-square)](README.md)
[![Русский](https://img.shields.io/badge/Русский-README-blue?style=flat-square)](README.ru.md)

Komod is a Kotlin DSL library for automatic code refactoring, built on top of [JavaParser](https://github.com/javaparser/javaparser).
Currently, only modifications to Java files are supported.

## Why?

Most Java refactoring tools, like OpenRewrite, require verbose configuration. Komod provides a type-safe Kotlin DSL that lets you express transformations concisely.
The goal of this project is to provide a DSL that is as convenient and intuitive as possible for developers.

## Example

### Modification

If you want to rename all methods with name "someMethod" to "someNewMethod", you can write this simple modification function:

```kotlin
modification {
    classes {
        methods({ name == "someMethod" }) {
            name = "someNewMethod"
        }
    }
}
```

### Input

```java
package com.example;
public class MyService {
    public void someMethod(String arg) { System.out.println(arg); }
}
```

### Output

```java
package com.example;
public class MyService {
    public void someNewMethod(String arg) { System.out.println(arg); }
}
```

## Plans

1. Interface which contains modification code, to be used with CLI tool
2. CLI tool for applying modifications written by library user, as like OpenRewrite recipes
3. Other languages support (JSON, YAML, XML, Kotlin...)

## Status

Early development (alpha) — not ready for production use.

## License

Apache License 2.0 — see [LICENSE](LICENSE) for details.

## Acknowledgments

This project uses [JavaParser](https://github.com/javaparser/javaparser)
under the terms of the Apache License, Version 2.0.

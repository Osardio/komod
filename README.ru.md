# Komod

Komod - библиотека Kotlin DSL для автоматического рефакторинга кода, построенная поверх [JavaParser](https://github.com/javaparser/javaparser).
На данный момент поддерживаются модификации только для Java-файлов.

## Зачем?

Большинство инструментов рефакторинга Java, таких как OpenRewrite, требуют слишком тяжёлой конфигурации. Komod предоставляет типобезопасный Kotlin DSL, 
позволяющий составлять такие трансформации лаконично.
Цель проекта — создать DSL, который будет максимально удобным и интуитивно понятным для разработчиков.

## Пример

### Модификация

Если вы хотите переименовать все методы с именем "someMethod" в "someNewMethod", вы можете написать такую лёгкую функцию модификации:

```kotlin
modification {
    classes {
        methods({ name == "someMethod" }) {
            name = "someNewMethod"
        }
    }
}
```

### Входные данные

```java
package com.example;
public class MyService {
    public void someMethod(String arg) { System.out.println(arg); }
}
```

### Выходные данные

```java
package com.example;
public class MyService {
    public void someNewMethod(String arg) { System.out.println(arg); }
}
```

## Планы

1. Интерфейс, содержащий код модификации, для использования с CLI-инструментом
2. CLI-инструмент для применения модификаций, написанных пользователем библиотеки, аналогично OpenRewrite рецептам
3. Поддержка других языков (JSON, YAML, XML, Kotlin...)

## Статус

Ранняя разработка (alpha) — не готово для продакшена.

## Лицензия

Apache License 2.0 — подробности см. в [LICENSE](LICENSE).

## Благодарности

Этот проект использует [JavaParser](https://github.com/javaparser/javaparser)
на условиях Apache License, Version 2.0.

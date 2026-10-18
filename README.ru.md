# Komod

Komod — библиотека Kotlin DSL для автоматического рефакторинга кода, построенная поверх [JavaParser](https://github.com/javaparser/javaparser). Она позволяет 
писать небольшие, типобезопасные рецепты трансформаций кода и применять их к Java-проектам из командной строки.

## Написание модификации

Модификация кода — класс Kotlin, реализующий `Modification`:

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

- `java { }` открывает один Java-файл; селекторы (`classes`, `methods`, `fields`, `parameters`, `statements`) сужают область.
- Синтаксис селектора: `classes({ фильтр }) { действие }` — фильтр в скобках, действие в фигурных скобках.
- Действия **идемпотентны**: повторный запуск того же рецепта ничего не меняет.

Скомпилируйте рецепт с Komod и установите артефакта в локальный Maven-репозиторий (`~/.m2`) или опубликуйте в любом Maven-репозитории.

## Сборка CLI

```bash
./gradlew :komod-cli:shadowJar
# → komod-cli/build/libs/komod-cli-<версия>-all.jar
```

Или с лаунчер-скриптом:

```bash
./gradlew :komod-cli:installDist    # → komod-cli/build/install/komod/bin/komod
```

## Запуск CLI

```bash
komod -a groupId:artifactId:version -m com.example.MyModifier -t /путь/к/проекту [-v] [-w]
```

| Флаг | Значение |
|------|----------|
| `-a/--artifact` | Maven-координаты артефакта рецепта (`groupId:artifactId:version`) |
| `-m/--modification` | Полное имя класса вашего `Modification` |
| `-t/--target` | Директория целевого проекта (по умолчанию текущая директория) |
| `-v/--verbose` | Печатать изменённые и пропущенные файлы с абсолютными путями |
| `-w/--no-excludes` | Отключить дефолтные исключения каталогов (`.git`, `build`, `target` и др.) |
| `-h/--help` | Показать справку |

Коды завершения: `0` — успех; `1` — сбой настройки (артефакт не найден, неверный класс или целевая директория).

## Проверка CLI

Самый быстрый способ проверить работу «от начала до конца» — запустить входящий в комплект скрипт:

```bash
./e2e-cli-test.sh
```

Он публикует `komod-core`, `komod-java` и пример рецепта в `~/.m2`, собирает дистрибутив CLI и запускает бинарник `komod` на примере Java-файла (`FooService`). Скрипт предполагает, что `java` в PATH — это JDK 25 (или `JAVA_HOME` указывает на неё).

Ожидаемый вывод:

```
Modified 1 file(s).
```

Требуется, чтобы `JAVA_HOME` указывал на JDK 25 (лаунчер `komod` скомпилирован для JDK 25).

## Пример

До:

```java
package com.example;

public class FooService {
    public String someMethod() {
        return "hello";
    }
}
```

После запуска рецепта выше:

```java
package com.example;

public class FooService {
    public String someNewMethod() {
        return "hello";
    }
}
```

## Статус

Ранняя разработка (alpha). CLI работает end-to-end; пока не production-ready.

## Лицензия

Apache License 2.0 — подробности см. в [LICENSE](LICENSE).

## Благодарности

Этот проект использует [JavaParser](https://github.com/javaparser/javaparser) на условиях Apache License, Version 2.0.

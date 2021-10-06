# Micro Template

A very tiny and simple text templating library for Kotlin. It has very limited features, and it's intended to be used for short templates that don't need any logic or advanced formatting.

## Usage

```kotlin
// create a reusable template
val greeting = MicroTemplate("Hello, {name}!")

// the values to be applied
val context = mapOf("name" to "Matteo")
greeting(context) // Hello, Matteo!
```
a context can contain values of any type:
```kotlin
val status = MicroTemplate("Welcome back {user}! You have {messages} unread messages. Your crypto balance: {balance}")

val context = mapOf(
    "user" to "Tom",
    "messages" to 99,
    "balance" to Coin(10_000)
)
status(context) // Welcome back Tom! You have 99 unread messages. Your crypto balance: 10,000©
```

## Features

Current features:
- basic token interpolation: `"Hello, {thing}!" => Hello, world!`
- all types are converted using their `toString()` method
- missing values are replaced with an empty string by default
- custom default value, both global and per token

Micro Template is useful if you need a quick and basic template support, hard-coded in your source (template files).

It does **not** support:
- custom formatting
- logic (no conditional or loops)
- template composition or inclusion
- sub-template/macros

## Distribution
This library is contained in a single file and has no 3rd party dependencies, so you can just copy it in your project.


**TODO**: 
- Find a better name
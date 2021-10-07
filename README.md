# Micro Template 📃

A very tiny and simple text templating library for Kotlin. It has very limited features, and it's intended to be used for short templates that don't need any logic or advanced formatting. Not suitable for rendering web views or pages. It's not optimized for speed, but as long as you produce short strings performance shouldn't be a problem.

## Usage

```kotlin
// create a reusable template
val greeting = MicroTemplate("Hello, {name}!")

// the values to be applied, define a context
val context = mapOf("name" to "Matteo")
greeting(context) // Hello, Matteo!
```
a context can contain values of any type:

```kotlin
// raw strings make multi-line templates more readable
val status = MicroTemplate(
    """
    Welcome back {user}! 
    You have {messages} unread messages. 
    Your crypto balance: {balance}
    """
)

val context = mapOf(
    "user" to "Tom",
    "messages" to 99,
    "balance" to Coin(10_000)
)
status(context) //  Welcome back Tom! 
                //  You have 99 unread messages.
                //  Your crypto balance: 10000©
```

## Features

Current features:
- basic token interpolation
- all types are converted using their `toString()` method
- missing values are replaced with an empty string by default
- a custom default value can be configured globally

Micro Template is useful if you need a quick and basic template support, hard-coded in your source (template files).

It does **not** support:
- custom formatting
- logic (no conditional or loops)
- functions or code execution
- template composition or inclusion
- sub-templates/macros

## Distribution

This library is contained in a single file and has no 3rd-party dependencies, so you can just copy it directly into your project.


## TODO

- [ ] Default value per token
- [ ] Basic formatting and transformation
- [ ] Find a better name for the project
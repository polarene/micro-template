# Micro Template 📃

A very tiny and simple text templating library for Kotlin. It has very limited features, so it's intended to be used for short templates that don't need any logic or advanced formatting. All it does is replace every named token (ex: `{name}`) with a matching value from a map (context), or provide a default for missing ones. Not suitable for rendering web views or pages, in general. 

**NOTE**: It's not optimized for speed, but as long as you render short strings performance shouldn't be a problem.

## Usage

A simple "hello" example:

```kotlin
// create a reusable template
val greeting = MicroTemplate("Hello, {name}!")

// the values to be applied define a 'context'
val context = mapOf("name" to "Matteo")
greeting(context) // Hello, Matteo!
```
A context can contain values of any type:

```kotlin
// raw strings make multi-line templates more readable
val status = MicroTemplate(
    """
    Welcome back {user}! 
    You have {messages} unread messages. 
    Your crypto balance is: {balance}
    """
)

val context = mapOf(
    "user" to "Tom",
    "messages" to 99,
    "balance" to Coin(10_000)
)
status(context) //  Welcome back Tom! 
                //  You have 99 unread messages.
                //  Your crypto balance is: 10000©
```

Handling missing values:

```kotlin
// by default missing values are replaced with an empty string
val greeting = MicroTemplate("Hello, {name}!")
greeting(emptyMap<String, Any>()) // "Hello, !"

// you can set a default value for the whole template
val scores = MicroTemplate(
    """
    Leaderboard
    ---
    Team A      {scoreA}
    Team B      {scoreB}
    Team C      {scoreC}
    """,
    default = "N/A"
)
scores(mapOf("scoreA" to 99)) // Leaderboard
                              // ---
                              // Team A      99
                              // Team B      N/A
                              // Team C      N/A

// or you can specify a default for a token
val greeting = MicroTemplate("Hello, {title:Buana }{name}!")
val context = mapOf<String, Any>("name" to "Matteo")
greeting(context) shouldBe "Hello, Buana Matteo!"
```

## Features

Current features:
- basic token interpolation
- all types are converted using their `toString()` function
- iterables and arrays are converted by joining their elements with a comma (`,`)
- missing values are replaced with an empty string by default
- a custom default value can be configured globally or per token

Micro Template is useful if you need a quick and basic template support, hard-coded in your source code (it doesn't support loading template files).

It does **not** support:
- custom formatting
- logic (no conditional or loops)
- functions or code execution
- template composition or inclusion
- sub-templates/macros
- nested interpolation (ex: {{name}})
- loading templates from external files

## Motivation

Why use this library if Kotlin already has built-in support for string templates? The problem with template expressions is that they can't be created and used dynamically, since they are evaluated at compile time, so their bindings (context) must be visible in the scope of their declaration.

Evaluation happens eagerly at compile time:

```kotlin
val i = 10
println("i = $i") // OK, 'i' is visible in this scope

// you can't store a string template for later reuse
// --> this doesn't compile!
val template = "$foo is like $bar"
```

To achieve lazy usage, we could wrap the template in a lambda:

```kotlin
val template = { foo:String, bar:Int -> "$foo is like $bar" }

template("The Answer", 42) // The Answer is like 42
```

this is better, but you still have to declare the template in your code, so you can't create it dynamically from a string or  resource:

```kotlin
val template = readFromFile("view.tpl") // contains the text 'Hello, ${user}!'
template.? // what do we do now? 
```

This behavior can be enough for many applications, but it's still limiting. We could try and compile the code for a template lambda at runtime to circumvent this limitation, but it seems overkill for a simple text substitution matter. Anyway, even if feasible, we must provide all of its bindings explicitly as parameters, or it won't compile again.

Instead, using a simple solution like micro-template, you can dynamically create a template and apply it lazily, passing arbitrary parameters.

## Distribution

This library is contained in a single file and has no 3rd-party dependencies, so you can just copy it directly into your project.

## TODO

For the list of next features, see the [TODO](https://github.com/polarene/micro-template/projects/1) kanban.  

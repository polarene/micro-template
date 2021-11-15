# Micro Template 📃

A very tiny and simple text templating library for Kotlin. It has very limited features, so it's intended to be used for short templates that don't need any logic or advanced formatting. All it does is replace every named token (ex: `{name}`) with a matching value from a map (context), or provide a default for missing ones. Not recommended for rendering web views or pages, in general. 

**NOTE**: It's not optimized for speed, but as long as you render short strings performance shouldn't be a problem.

## Features

Current features:
- basic token interpolation
- all types are converted using their `toString()` function
- iterables and arrays are converted by joining their elements with a comma (`,`)
- missing values are replaced with an empty string by default
- a custom default value can be configured globally or per token
- escaping of reserved characters
- a type-safe wrapper around templates

Micro Template is useful when you need a quick and basic support for string templates, inlined in your source (it doesn't support loading template files).

It does **not** support:
- custom formatting
- logic (no conditional or loops)
- functions or code execution
- template composition or inclusion
- sub-templates/macros
- nested interpolation (ex: `{{name}}`)
- loading templates from external files

## Usage

### A simple "hello" example

You can create a reusable template and apply it like a function.

```kotlin
// create a reusable template
val greeting = MicroTemplate("Hello, {name}!")

// the values to be applied define a 'context'
val context = mapOf("name" to "Matteo")
greeting(context) // Hello, Matteo!
```

A context can contain values of any type. By default, they will be converted using their `toString()` method. Nullable values are not allowed. 
For convenience a `Context` type alias is available.


```kotlin
// raw strings make multi-line templates more readable
val status = MicroTemplate(
    """
    Welcome back {user}! 
    You have {messages} unread messages. 
    Your crypto balance is: {balance}
    """
)
// alias of Map<String, Any>
val context: Context = mapOf(
    "user" to "Tom",
    "messages" to 99,
    "balance" to Coin(10_000)
)
status(context) //  Welcome back Tom! 
                //  You have 99 unread messages.
                //  Your crypto balance is: 10000©
```

Iterables and arrays are converted by joining their elements with a comma.

```kotlin
val fruits = MicroTemplate("Fruit list: {fruits}")
val context = mapOf("fruits" to listOf("apple", "banana", "grape"))
fruits(context) // Fruit list: apple,banana,grape
```

### Handling missing values

Missing values are replaced with an empty string by default.

```kotlin
val greeting = MicroTemplate("Hello, {name}!")
greeting(emptyMap<String, Any>()) // Hello, !
```

You can set a default value for the whole template,

```kotlin
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
```

or you can specify the default for a single token.

```kotlin
val greeting = MicroTemplate("Hello, {title:Buana }{name}!")
val context = mapOf("name" to "Matteo")
greeting(context) // Hello, Buana Matteo!
```

Nullable types are not allowed inside a context. If you have a `null` value, simply leave it out of the context and it will be replaced with a default during interpolation.

### Escaping 

To render reserved characters in the text they must be escaped.

```kotlin
// raw strings make escaping less verbose
val literalToken = MicroTemplate("""Look {ma}, I need a literal \{token\} here!""")
val context = mapOf("ma" to "Mama")
literalToken(context) // Look Mama, I need a literal {token} here!
```

It works inside default values too. 

```kotlin
val literalDefault = MicroTemplate("""My placeholder is {ph:\{\}}""")
literalDefault(emptyMap<String, Any>()) // My placeholder is {}
```

### Typed templates

Normally a template would accept a dynamically typed context (`Map<String, Any>`), so you can pass it any value you want.
If you like, you can create a statically typed template by wrapping an existing one. The wrapper will only accept instances of a fixed type `T` instead of a generic `Context`. All the public properties from `T` are interpolated in the template, except for null values that will be replaced with defaults.

```kotlin
class BusinessCard(val name: String, val title: String)

val hello = MicroTemplate("Hello, {title}{name}")
val typedHello = TypedMicroTemplate(hello, BusinessCard::class)

typedHello(BusinessCard(name = "Smith", title = "Mr.")) // Hello, Mr.Smith
typedHello(mapOf("name" to "Smith")) // won't compile!
```

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

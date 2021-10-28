package io.github.polarene

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch

/**
 * Template features.
 * @author mmirk
 */
class MicroTemplateSpec : StringSpec({
    "should replace a single token" {
        val context = mapOf("name" to "Matteo")
        val greeting = MicroTemplate("Hello, {name}!")

        greeting(context) shouldBe "Hello, Matteo!"
    }

    "should replace a repeated token" {
        val context = mapOf("name" to "Matteo")
        val greeting = MicroTemplate("Hello, {name}-{name}!")

        greeting(context) shouldBe "Hello, Matteo-Matteo!"
    }

    "should replace multiple tokens" {
        val context = mapOf("name" to "Matteo", "title" to "Mr.")
        val greeting = MicroTemplate("Hello, {title}{name}!")

        greeting(context) shouldBe "Hello, Mr.Matteo!"
    }

    "should replace any missing token with an empty string" {
        val context = emptyMap<String, Any>()
        val greeting = MicroTemplate("Hello, {title}{name}!")

        greeting(context) shouldBe "Hello, !"
    }

    "should replace any missing token with a global default value" {
        val context = emptyMap<String, Any>()
        val greeting = MicroTemplate("Hello, {title}{name}!", globalDefault = "-")

        greeting(context) shouldBe "Hello, --!"
    }

    "should replace any missing token with its local default value" {
        val context = mapOf("name" to "Matteo")
        val greeting = MicroTemplate("Hello, {title:Buana} {name}!")

        greeting(context) shouldBe "Hello, Buana Matteo!"
    }

    "should render a literal token inside text" {
        val context = mapOf("ma" to "Mama", "token" to "IGNORE ME")
        val literalToken = MicroTemplate("""Look {ma}, I need a literal \{token\} here!""")

        literalToken(context) shouldBe "Look Mama, I need a literal {token} here!"
    }

    listOf(
        """Hello, {name}! Give me a \{""",
        """Hello, {name}! Give me a \}""",
        """Hello, {name}! Give me a \{\}""",
        """Hello, {name}! Give me a \{\{""",
        """Hello, {name}! Give me a \}\}""",
        """Hello, {name}! Give me a \{\{\}\}"""
    ).forEach {
        val context = mapOf("name" to "Springfield")
        "should render reserved characters when escaped: ${it.substringAfterLast(' ')}" {
            MicroTemplate(it)(context) shouldMatch ".+ [{}]+"
        }
    }

    "should render reserved characters in default token value when escaped" {
        val context = emptyMap<String, Any>()
        val literalDefault = MicroTemplate("""My placeholder is {ph:\{\}}""")

        literalDefault(context) shouldBe "My placeholder is {}"
    }

    "should render an iterable" {
        val context = mapOf("fruits" to listOf("apple", "banana", "grape"))
        val fruits = MicroTemplate("Fruit list: {fruits}")

        fruits(context) shouldBe "Fruit list: apple,banana,grape"
    }

    "should render an array" {
        val context = mapOf("fruits" to arrayOf("apple", "banana", "grape"))
        val fruits = MicroTemplate("Fruit list: {fruits}")

        fruits(context) shouldBe "Fruit list: apple,banana,grape"
    }

    "should render a primitive array" {
        val context = mapOf("extraction" to intArrayOf(5, 22, 17, 80, 3))
        val bingo = MicroTemplate("Winning numbers: {extraction}")

        bingo(context) shouldBe "Winning numbers: 5,22,17,80,3"
    }
})

/**
 * Template error cases.
 * @author mmirk
 */
class MicroTemplateErrorSpec : StringSpec({
    "should reject a template without tokens" {
        shouldThrow<IllegalArgumentException> {
            MicroTemplate("Eleates peregrinationes, tanquam teres bursa.")
        }
    }

    listOf(
        "{}",
        "{token",
        "token}",
        "Fermium de neuter pars, {token perdere musa!",
        "Fermium de {bad-token1 pars, bad_token2} perdere musa!",
        """\{\}""",
        """\{token\}""",
        """\{token""",
        """token\}"""
    ).forEach {
        "should reject a template containing only malformed or escaped tokens: $it" {
            shouldThrow<IllegalArgumentException> {
                MicroTemplate(it)
            }
        }
    }
})

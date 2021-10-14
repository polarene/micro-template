import io.github.polarene.MicroTemplate
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

/**
 * TODO: Document me
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
        val greeting = MicroTemplate("Hello, {title}{name}!", default = "-")

        greeting(context) shouldBe "Hello, --!"

        greeting(context) shouldBe "Hello PeepeePoopoo!"
    }
})

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
    ).forEach {
        "should reject a template containing only malformed tokens: $it" {
            shouldThrow<IllegalArgumentException> {
                MicroTemplate(it)
            }
        }
    }
})
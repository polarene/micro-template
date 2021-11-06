@file:Suppress("unused")

package io.github.polarene

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

/**
 * Typed template features.
 * @author mmirk
 */
class TypedMicroTemplateSpec : StringSpec({
    "should accept a class with public properties" {
        val context = BusinessCard(name = "Smith", title = "Mr.")
        val typedHello = TypedMicroTemplate(hello, BusinessCard::class)

        typedHello(context) shouldBe "Hello, Mr.Smith!"
    }

    "should accept a class with nullable public properties" {
        val context = BusinessCardNil(name = "Smith")
        val typedHello = TypedMicroTemplate(hello, BusinessCardNil::class)

        typedHello(context) shouldBe "Hello, Smith!"
    }

    "should accept a data class with nullable public properties" {
        val context = BusinessCardDC(name = "Smith")
        val typedHello = TypedMicroTemplate(hello, BusinessCardDC::class)

        typedHello(context) shouldBe "Hello, Smith!"
    }

    "should accept an interface with properties" {
        val context = User("Smith")
        val typedHello = TypedMicroTemplate(hello, Id::class)
        withClue("Only properties from the interface are used") {
            typedHello(context) shouldBe "Hello, Smith!"
        }
    }

    "should accept an abstract class with properties" {
        val context = TitledUser(title = "Mr.", name = "Smith")
        val typedHello = TypedMicroTemplate(hello, Title::class)
        withClue("Only properties from the parent are used") {
            typedHello(context) shouldBe "Hello, Mr.!"
        }
    }
})

class TypedMicroTemplateErrorSpec : StringSpec({
    "should reject a class without properties" {
        shouldThrow<IllegalArgumentException> {
            TypedMicroTemplate(hello, Empty::class)
        }
    }

    "should reject a class without public properties" {
        shouldThrow<IllegalArgumentException> {
            TypedMicroTemplate(hello, NoPublicProperties::class)
        }
    }

    "should reject a class without at least one property matching a token" {
        shouldThrow<IllegalArgumentException> {
            TypedMicroTemplate(hello, NoMatchingProperties::class)
        }
    }
})

// the reference template to be wrapped
val hello = MicroTemplate("Hello, {title}{name}!")

/* --- simple classes --- */
class BusinessCard(val name: String, val title: String)
class BusinessCardNil(val name: String, val title: String? = null)
data class BusinessCardDC(val name: String, val title: String? = null)

/* --- interfaces and parents --- */
interface Id {
    val name: String
}
class User(override val name: String) : Id
abstract class Title(val title: String)
class TitledUser(val name: String, title: String) : Title(title)

/* --- no properties --- */
class Empty
class NoPublicProperties(private val name: String)
class NoMatchingProperties(val id: Long, val updated: Boolean)

package io.github.polarene

import io.kotest.assertions.throwables.shouldThrow
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

        typedHello(context) shouldBe "Hello, Smith!"
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
            TypedMicroTemplate(hello, NoPublic::class)
        }
    }
})

val hello = MicroTemplate("Hello, {title}{name}!")

// simple classes
class BusinessCard(val name: String, val title: String)
class BusinessCardNil(val name: String, val title: String? = null)
data class BusinessCardDC(val name: String, val title: String? = null)

// with interfaces
interface Id {
    val name: String
}
class User(override val name: String) : Id

// no properties
class Empty
class NoPublic(private val name: String)

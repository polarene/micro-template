@file:Suppress("unused")

package io.github.polarene

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beOfType

/**
 * Template factory functions.
 * @author mmirk
 */
class FactoriesSpec : StringSpec({
    "should create a template accepting a Context" {
        val hello: Template<Context> = templateOf(definition)

        hello should beOfType<MicroTemplate>()
    }

    "should create a template accepting a Context from the given configuration" {
        val hello = templateOf(definition) {
            globalDefault = "N/A"
        }

        hello(emptyMap()) shouldBe "Hello, N/A!"
    }

    "should create a template accepting an instance of the specified type" {
        val hello: Template<Box> = templateOf<Box>(definition)

        hello should beOfType<TypedMicroTemplate<Box>>()
    }

    "should create a template accepting an instance of the specified type from the given configuration" {
        val hello = templateOf<Box>(definition) {
            globalDefault = "N/A"
        }

        hello(Box(null)) shouldBe "Hello, N/A!"
    }
})

private const val definition = "Hello, {thing}!"

class Box(val thing: String?)

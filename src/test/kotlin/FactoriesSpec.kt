@file:Suppress("unused")

package io.github.polarene

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.types.beOfType

/**
 * Template factory functions.
 * @author mmirk
 */
class FactoriesSpec : StringSpec({
    val definition = "Hello, {thing}!"

    "should create a template accepting a Context" {
        val hello = templateOf(definition)

        hello should beOfType<MicroTemplate>()
    }

    "should create a template accepting a context of the specified type" {
        val hello = templateOf<Box>(definition)

        hello should beOfType<TypedMicroTemplate<Box>>()
    }
})

class Box(val thing: String)

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
    "should create a template accepting a Context" {
        val hello: Template<Context> = templateOf(definition)

        hello should beOfType<MicroTemplate>()
    }

    "should create a template accepting the specified type" {
        val hello: Template<Box> = templateOf<Box>(definition)

        hello should beOfType<TypedMicroTemplate<Box>>()
    }
})

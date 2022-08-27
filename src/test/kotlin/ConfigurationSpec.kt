package io.github.polarene

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ConfigurationSpec : StringSpec({
    "should replace any missing token with a global default value" {
        val hello = templateOf(definition) {
            globalDefault = "N/A"
        }

        hello(emptyMap()) shouldBe "Hello, N/A!"
    }

    "should render an iterable with the given separator" {
        val context = mapOf("fruits" to listOf("apple", "banana", "grape"))
        val fruits = templateOf("Fruit list: {fruits}") {
            separator = " | "
        }

        fruits(context) shouldBe "Fruit list: apple | banana | grape"
    }

    "should apply the configuration to a typed template as well" {
        val hello = templateOf<Box>(definition) {
            globalDefault = "N/A"
        }

        hello(Box(null)) shouldBe "Hello, N/A!"
    }
})

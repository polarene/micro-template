package io.github.polarene

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.kotest.property.checkAll

class NumberFormatSpec : StringSpec({
    "Default format should return the string representation of a number" {
        checkAll<Int> { i ->
            NumberFormat.Default.convert(i) shouldBe i.toString()
        }
        checkAll<Double> { d ->
            NumberFormat.Default.convert(d) shouldBe d.toString()
        }
    }

    "Round format should return a number rounded to the nearest integer" {
        checkAll<Int> { i ->
            NumberFormat.Round.convert(i) shouldBe i.toString()
        }
        checkAll<Double> { d ->
            NumberFormat.Round.convert(d) shouldMatch "-?\\d+" //FIXME
        }
    }

})

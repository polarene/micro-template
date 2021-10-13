package io.github.polarene

private val TOKEN = """\{[-\w]+}""".toRegex()

class MicroTemplate(val template: String, val default: String = "") {
    init {
        require(TOKEN.containsMatchIn(template)) {
            "A template definition must contain at least one token matching $TOKEN"
        }
    }

    operator fun invoke(context: Map<String, Any>) = template.replace(TOKEN) {
        context[it.value.tokenName()]?.toString() ?: default
    }
}

private fun String.tokenName() = this.substring(1, this.length - 1)

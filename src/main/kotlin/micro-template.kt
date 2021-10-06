package io.github.polarene


val TOKEN = """\{[-\w]+}""".toRegex()

class MicroTemplate(val template: String) {
    init {
        require(TOKEN.containsMatchIn(template)) { "A template definition must contain at least one token" }
    }

    operator fun invoke(context: Map<String, Any>) = template.replace(TOKEN) {
        context[it.value.tokenName()]?.toString() ?: ""
    }
}

private fun String.tokenName() = this.substring(1, this.length - 1)

package io.github.polarene

typealias Context = Map<String, Any>

/**
 * Matches a single token inside a template.
 * A token has the form:
 *
 *   {identifier:default_value}
 */
private val TOKEN = """\{([-\w]+)(?::([^}]*))?}""".toRegex()

/**
 * Matches a reserved character escaped by a backslash (\).
 */
private val ESCAPED_RESERVED = """\\([{}])""".toRegex()

/**
 * A micro template.
 * @property template A template definition
 * @property globalDefault the default value to be used for any missing token
 * @constructor create a reusable template
 */
class MicroTemplate(val template: String, val globalDefault: String = "") {
    init {
        require(TOKEN.containsMatchIn(template)) {
            "A template definition must contain at least one token matching $TOKEN"
        }
    }

    /**
     * Applies this template to the given context.
     * @param context the values to be replaced in this template
     * @return the resulting string after interpolation
     */
    operator fun invoke(context: Context) = template
        .interpolate(context)
        .unescape()

    private fun String.interpolate(context: Context) = replace(TOKEN) {
        Token(it).lookFrom(context) ?: globalDefault
    }

    private fun String.unescape() = replace(ESCAPED_RESERVED, "$1")
}

/**
 * A token is a region in the template to be replaced with the corresponding value from a context.
 * If the token name isn't found in a context, then its default value is used.
 * If a token doesn't specify a default value, then null is used.
 */
private class Token(m: MatchResult) {
    val name: String = m.groups[1]!!.value
    val default: String? = m.groups[2]?.value

    fun lookFrom(context: Context): String? = context[name]?.toString() ?: default
}

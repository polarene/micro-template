package io.github.polarene

typealias Context = Map<String, Any>

/**
 * Matches a single token inside a template.
 * A token has the form:
 *
 *   {identifier:default_value}
 */
internal val TOKEN = """\{([-\w]+)(?::([^}]*))?}""".toRegex()

/**
 * A micro template.
 * @property template A template definition
 * @property default the default value to be used for any missing token
 * @constructor create a reusable template
 */
class MicroTemplate(val template: String, val default: String = "") {
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
    operator fun invoke(context: Context) = template.replace(TOKEN) {
        Token(it, default).lookFrom(context)
    }
}

/**
 * A token is a region in the template to be replaced with the corresponding value from a context.
 * If the token name isn't found in a context, then its default value is used.
 * If a token doesn't specify a default value, then globalDefault is used.
 * @property name String
 * @property default String
 * @constructor
 */
internal class Token(m: MatchResult, globalDefault: String) {
    val name: String = m.groups[1]!!.value
    val default: String = m.groups[2]?.value ?: globalDefault

    fun lookFrom(context: Context) = context[name]?.toString() ?: default
}

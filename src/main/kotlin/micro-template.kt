package io.github.polarene

import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

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
 * @property definition A template definition
 * @property globalDefault the default value to be used for any missing token
 * @constructor create a reusable template
 * @throws IllegalArgumentException if template doesn't contain at least one token
 */
class MicroTemplate(val definition: String, val globalDefault: String = "") {
    init {
        require(TOKEN.containsMatchIn(definition)) {
            "A template definition must contain at least one token matching $TOKEN"
        }
    }

    /**
     * Applies this template to the given context.
     * @param context the values to be replaced in this template
     * @return the resulting string after interpolation
     */
    operator fun invoke(context: Context) = definition
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
 * If a token doesn't specify a default value, then null is returned.
 */
private class Token(m: MatchResult) {
    val name: String = m.groups[1]!!.value
    val default: String? = m.groups[2]?.value

    fun lookFrom(context: Context) = context[name]?.let { Format.byType(it) } ?: default
}

/**
 * Format determines how a value is converted to string.
 */
private object Format {
    private const val separator = ","

    /**
     * Converts a value to a string depending on its type.
     */
    fun byType(value: Any) = when (value) {
        is Iterable<*> -> value.joinToString(separator)
        is Array<*> -> value.joinToString(separator)
        is IntArray -> value.joinToString(separator)
        is DoubleArray -> value.joinToString(separator)
        is FloatArray -> value.joinToString(separator)
        is LongArray -> value.joinToString(separator)
        is CharArray -> value.joinToString(separator)
        is ShortArray -> value.joinToString(separator)
        is ByteArray -> value.joinToString(separator)
        is BooleanArray -> value.joinToString(separator)
        else -> value.toString()
    }
}

/**
 * Constructs a type-safe wrapper around the specified [template].
 * This template will accept only instances of [T] as the context.
 * @param T the type to be used for context
 * @constructor create a reusable template
 * @throws IllegalArgumentException if contextType doesn't have any public properties
 */
class TypedMicroTemplate<T : Any>(val template: MicroTemplate, contextType: KClass<T>) {
    private val publicProperties =
        contextType.memberProperties.filter { it.visibility == KVisibility.PUBLIC }

    init {
        require(publicProperties.isNotEmpty()) {
            "The context type ${contextType.qualifiedName} doesn't have any public properties"
        }
    }

    /**
     * Applies this template to the given context.
     * @param context an object containing the values to be replaced in this template
     * @return the resulting string after interpolation
     */
    operator fun invoke(context: T) = template(context.toMap())

    /**
     * Creates a Map with the properties names as keys and their actual values,
     * without nulls.
     */
    @Suppress("UNCHECKED_CAST")
    private fun T.toMap() = publicProperties
        .associateBy({ p -> p.name }, { p -> p.get(this) })
        .filter { e -> e.value != null } as Map<String, Any>
}

package io.github.polarene

import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

/**
 * A context is the set of values that replaces tokens in a template.
 */
typealias Context = Map<String, Any>

/**
 * A Template produces a text by replacing one or more placeholder (tokens) in a definition
 * with the values contained in a context object.
 * @param T the type of the context
 */
interface Template<T : Any> {
    /**
     * Applies the values from [context] to produce a text.
     */
    operator fun invoke(context: T): String
}

/**
 * Returns a new [MicroTemplate] with the given [definition].
 */
fun templateOf(definition: String) = MicroTemplate(definition)

/**
 * Returns a new [TypedMicroTemplate] with the given [definition].
 */
inline fun <reified T : Any> templateOf(definition: String) =
    TypedMicroTemplate(MicroTemplate(definition), T::class)

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
class MicroTemplate(val definition: String, val globalDefault: String = "") : Template<Context> {
    init {
        require(TOKEN.containsMatchIn(definition)) {
            "A template definition must contain at least one token matching $TOKEN"
        }
    }

    /**
     * A set of all the token names in this template.
     */
    private val tokens = TOKEN.findAll(definition)
        .map { Token(it).name }
        .toSet()

    /**
     * Applies this template to the given [context], replacing each token occurrence
     * with the given value.
     * Null values are not allowed so if a token is missing from the context, it will be replaced
     * with a default value.
     * @param context the values to be replaced in this template
     * @return the resulting string after interpolation
     */
    override operator fun invoke(context: Context) = definition
        .interpolate(context)
        .unescape()

    /**
     * Checks if this template contains one or more tokens with the given [name].
     */
    fun hasToken(name: String) = tokens.contains(name)

    private fun String.interpolate(context: Context) = replace(TOKEN) {
        Token(it).lookFrom(context) ?: globalDefault
    }

    private fun String.unescape() = replace(ESCAPED_RESERVED, "$1")
}

/**
 * A token is a region in the template to be replaced with the corresponding value from a context.
 * If the token name isn't found in a context, then its default value is used.
 * If a token doesn't specify a default value, then null is returned.
 * @param m a token match in the template definition
 * @property name the name of the token
 * @property default the default value for the token, if declared
 */
private class Token(m: MatchResult) {
    val name: String = m.groups[1]!!.value
    val default: String? = m.groups[2]?.value

    /**
     * Looks up the corresponding value for this token from a [context].
     */
    fun lookFrom(context: Context) = context[name]?.let { Format.byType(it) } ?: default
}

/**
 * Format determines how a value is converted to a string.
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
 * or none of them matches at least one token
 */
class TypedMicroTemplate<T : Any>(val template: MicroTemplate, contextType: KClass<T>) :
    Template<T> {
    private val publicProperties =
        contextType.memberProperties.filter { it.visibility == KVisibility.PUBLIC }

    init {
        require(publicProperties.isNotEmpty()) {
            "The context type ${contextType.qualifiedName} must have at least one public property"
        }
        require(publicProperties.any { template.hasToken(it.name) }) {
            "The context type ${contextType.qualifiedName} must have at least one property " +
                    "matching any of the template tokens"
        }
    }

    /**
     * Applies this template to the given [context].
     * @param context an object containing the values to be replaced in this template
     * @return the resulting string after interpolation
     */
    override operator fun invoke(context: T) = template(context.toMap())

    /**
     * Converts an instance of [T] to a [Map] indexed by its properties names
     * and containing the properties values.
     * The properties having a `null` value are not included in the Map.
     */
    @Suppress("UNCHECKED_CAST")
    private fun T.toMap() = publicProperties
        .associateBy({ p -> p.name }, { p -> p.get(this) })
        .filter { e -> e.value != null } as Map<String, Any>
}

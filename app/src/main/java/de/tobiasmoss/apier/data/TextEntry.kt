package de.tobiasmoss.apier.data

/**
 * An entry item representing a piece of API text content.
 */
data class TextEntry(var id: Int, var content: String, val details: String, var faved: Boolean) {
    override fun toString(): String = content
}
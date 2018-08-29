package com.pape.utils

/**
 * author：huangchen on 2018/8/29 21:30
 * email：huangchen@yonghui.cn
 * desc  : utils about string
 */
object StringUtils {


    /**
     * Return whether the string is null or 0-length.
     *
     * @param s The string.
     * @return {@code true}: yes<br> {@code false}: no
     */
    fun isEmpty(s: CharSequence?): Boolean {
        return s == null || s.isEmpty()
    }

    /**
     * Return whether the string is null or whitespace.
     *
     * @param s The string.
     * @return {@code true}: yes<br> {@code false}: no
     */
    fun isTrimEmpty(s: String?): Boolean {
        return s == null || s.trim { it <= ' ' }.isEmpty()
    }

    /**
     * Return whether the string is null or white space.
     *
     * @param s The string.
     * @return {@code true}: yes<br> {@code false}: no
     */
    fun isSpace(s: String?): Boolean {
        if (s == null) return true
        var i = 0
        val len = s.length
        while (i < len) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
            ++i
        }
        return true
    }

    /**
     * Return whether string1 is equals to string2.
     *
     * @param s1 The first string.
     * @param s2 The second string.
     * @return {@code true}: yes<br>{@code false}: no
     */
    fun equals(a: CharSequence?, b: CharSequence?): Boolean {
        if (a === b) return true
        if (a != null && b != null && a.length == b.length) {
            if (a is String && b is String) {
                return a == b
            } else {
                val length = a.length
                return (0 until length).none { a[it] != b[it] }
            }
        }
        return false
    }

    /**
     * Return whether string1 is equals to string2, ignoring case considerations..
     *
     * @param a The first string.
     * @param b The second string.
     * @return {@code true}: yes<br>{@code false}: no
     */
    fun equalsIgnoreCase(a: String?, b: String?): Boolean {
        return a?.equals(b!!, ignoreCase = true) ?: (b == null)
    }

    /**
     * Return {@code ""} if string equals null.
     *
     * @param s The string.
     * @return {@code ""} if string equals null
     */
    fun null2Length0(s: String?): String {
        return s ?: ""
    }

    /**
     * Return the length of string.
     *
     * @param s The string.
     * @return the length of string
     */
    fun length(s: CharSequence?): Int {
        return s?.length ?: 0
    }

    /**
     * Set the first letter of string upper.
     *
     * @param s The string.
     * @return the string with first letter upper.
     */
    fun upperFirstLetter(s: String): String? {
        return if (isEmpty(s) || !Character.isLowerCase(s[0])) s else (s[0].toInt() - 32).toChar().toString() + s.substring(1)
    }

    /**
     * Set the first letter of string lower.
     *
     * @param s The string.
     * @return the string with first letter lower.
     */
    fun lowerFirstLetter(s: String): String? {
        return if (isEmpty(s) || !Character.isUpperCase(s[0])) s else (s[0].toInt() + 32).toChar().toString() + s.substring(1)
    }

    /**
     * Reverse the string.
     *
     * @param s The string.
     * @return the reverse string.
     */
    fun reverse(s: String): String {
        val len = length(s)
        if (len <= 1) return s
        val mid = len shr 1
        val chars = s.toCharArray()
        var c: Char
        for (i in 0 until mid) {
            c = chars[i]
            chars[i] = chars[len - i - 1]
            chars[len - i - 1] = c
        }
        return String(chars)
    }

    /**
     * Convert string to DBC.
     *
     * @param s The string.
     * @return the DBC string
     */
    fun toDBC(s: String): String? {
        if (isEmpty(s)) return s
        val chars = s.toCharArray()
        var i = 0
        val len = chars.size
        while (i < len) {
            if (chars[i].toInt() == 12288) {
                chars[i] = ' '
            } else if (chars[i].toInt() in 65281..65374) {
                chars[i] = (chars[i].toInt() - 65248).toChar()
            } else {
                chars[i] = chars[i]
            }
            i++
        }
        return String(chars)
    }

    /**
     * Convert string to SBC.
     *
     * @param s The string.
     * @return the SBC string
     */
    fun toSBC(s: String): String? {
        if (isEmpty(s)) return s
        val chars = s.toCharArray()
        var i = 0
        val len = chars.size
        while (i < len) {
            if (chars[i] == ' ') {
                chars[i] = 12288.toChar()
            } else if (chars[i].toInt() in 33..126) {
                chars[i] = (chars[i].toInt() + 65248).toChar()
            } else {
                chars[i] = chars[i]
            }
            i++
        }
        return String(chars)
    }
}
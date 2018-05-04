package com.pape.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import com.pape.utils.ConvertUtils.Companion.bytes2Bitmap
import com.pape.utils.constant.MemoryConstants
import java.io.*
import java.nio.charset.Charset
import kotlin.experimental.or

/**
 * 类描述:utils about convert
 * @author chenzebang
 * @version v1.0
 * Date：2018/5/3 11:56
 */
class ConvertUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    /**
     * Output stream to input stream.
     *
     * @param out The output stream.
     * @return input stream
     */
    fun output2InputStream(out: OutputStream?): ByteArrayInputStream? {
        return if (out == null) null else ByteArrayInputStream((out as ByteArrayOutputStream).toByteArray())
    }

    companion object {

        private val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

        /**
         * Bytes to bits.
         *
         * @param bytes The bytes.
         * @return bits
         */
        fun bytes2Bits(bytes: ByteArray): String {
            val sb = StringBuilder()
            for (aByte in bytes) {
                for (j in 7 downTo 0) {
                    sb.append(if ((aByte shr j).toInt() and 0x01 == 0) '0' else '1')
                }
            }
            return sb.toString()
        }

        /**
         * Bits to bytes.
         *
         * @param bits The bits.
         * @return bytes
         */
        fun bits2Bytes(bits: String): ByteArray {
            var varBits = bits
            val lenMod = varBits.length % 8
            var byteLen = varBits.length / 8
            // add "0" until length to 8 times
            if (lenMod != 0) {
                for (i in lenMod..7) {
                    varBits = "0" + varBits
                }
                byteLen++
            }
            val bytes = ByteArray(byteLen)
            for (i in 0 until byteLen) {
                for (j in 0..7) {
                    bytes[i] = bytes[i] shl 1
                    bytes[i] = bytes[i] or (varBits[i * 8 + j] - '0').toByte()
                }
            }
            return bytes
        }

        /**
         * Bytes to chars.
         *
         * @param bytes The bytes.
         * @return chars
         */
        fun bytes2Chars(bytes: ByteArray?): CharArray? {
            if (bytes == null) return null
            val len = bytes.size
            if (len <= 0) return null
            val chars = CharArray(len)
            for (i in 0 until len) {
                chars[i] = (bytes[i].toInt() and 0xff).toChar()
            }
            return chars
        }

        /**
         * Chars to bytes.
         *
         * @param chars The chars.
         * @return bytes
         */
        fun chars2Bytes(chars: CharArray?): ByteArray? {
            if (chars == null || chars.isEmpty()) return null
            val len = chars.size
            val bytes = ByteArray(len)
            for (i in 0 until len) {
                bytes[i] = chars[i].toByte()
            }
            return bytes
        }

        /**
         * Bytes to hex string.
         *
         * e.g. bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns "00A8"
         *
         * @param bytes The bytes.
         * @return hex string
         */
        fun bytes2HexString(bytes: ByteArray?): String? {
            if (bytes == null) return null
            val len = bytes.size
            if (len <= 0) return null
            val ret = CharArray(len shl 1)
            var i = 0
            var j = 0
            while (i < len) {
                ret[j++] = hexDigits[bytes[i].ushr(4).toInt() and 0x0f]
                ret[j++] = hexDigits[bytes[i].toInt() and 0x0f]
                i++
            }
            return String(ret)
        }

        /**
         * Hex string to bytes.
         *
         * e.g. hexString2Bytes("00A8") returns { 0, (byte) 0xA8 }
         *
         * @param hexString The hex string.
         * @return the bytes
         */
        fun hexString2Bytes(hexString: String): ByteArray? {
            var varHexString = hexString
            if (isSpace(varHexString)) return null
            var len = varHexString.length
            if (len % 2 != 0) {
                varHexString = "0" + varHexString
                len++
            }
            val hexBytes = varHexString.toUpperCase().toCharArray()
            val ret = ByteArray(len shr 1)
            var i = 0
            while (i < len) {
                ret[i shr 1] = (hex2Int(hexBytes[i]) shl 4 or hex2Int(hexBytes[i + 1])).toByte()
                i += 2
            }
            return ret
        }

        private fun hex2Int(hexChar: Char): Int {
            return when (hexChar) {
                in '0'..'9' -> hexChar - '0'
                in 'A'..'F' -> hexChar - 'A' + 10
                else -> throw IllegalArgumentException()
            }
        }

        /**
         * Size of memory in unit to size of byte.
         *
         * @param memorySize Size of memory.
         * @param unit       The unit of memory size.
         *
         *  * [MemoryConstants.BYTE]
         *  * [MemoryConstants.KB]
         *  * [MemoryConstants.MB]
         *  * [MemoryConstants.GB]
         *
         * @return size of byte
         */
        fun memorySize2Byte(memorySize: Long,
                            @MemoryConstants.Unit unit: Int): Long {
            return if (memorySize < 0) -1 else memorySize * unit
        }

        /**
         * Size of byte to size of memory in unit.
         *
         * @param byteSize Size of byte.
         * @param unit     The unit of memory size.
         *
         *  * [MemoryConstants.BYTE]
         *  * [MemoryConstants.KB]
         *  * [MemoryConstants.MB]
         *  * [MemoryConstants.GB]
         *
         * @return size of memory in unit
         */
        fun byte2MemorySize(byteSize: Long,
                            @MemoryConstants.Unit unit: Int): Double {
            return if (byteSize < 0) -1.0 else byteSize.toDouble() / unit
        }

        /**
         * Size of byte to fit size of memory.
         *
         * to three decimal places
         *
         * @param byteSize Size of byte.
         * @return fit size of memory
         */
        fun byte2FitMemorySize(byteSize: Long): String {
            return when {
                byteSize < 0 -> "shouldn't be less than zero!"
                byteSize < MemoryConstants.KB -> String.format("%.3fB", byteSize.toDouble())
                byteSize < MemoryConstants.MB -> String.format("%.3fKB", byteSize.toDouble() / MemoryConstants.KB)
                byteSize < MemoryConstants.GB -> String.format("%.3fMB", byteSize.toDouble() / MemoryConstants.MB)
                else -> String.format("%.3fGB", byteSize.toDouble() / MemoryConstants.GB)
            }
        }

        /**
         * Time span in unit to milliseconds.
         *
         * @param timeSpan The time span.
         * @param unit     The unit of time span.
         *
         *  * [TimeConstants.MSEC]
         *  * [TimeConstants.SEC]
         *  * [TimeConstants.MIN]
         *  * [TimeConstants.HOUR]
         *  * [TimeConstants.DAY]
         *
         * @return milliseconds
         */
        fun timeSpan2Millis(timeSpan: Long, @TimeConstants.Unit unit: Int): Long {
            return timeSpan * unit
        }

        /**
         * Milliseconds to time span in unit.
         *
         * @param millis The milliseconds.
         * @param unit   The unit of time span.
         *
         *  * [TimeConstants.MSEC]
         *  * [TimeConstants.SEC]
         *  * [TimeConstants.MIN]
         *  * [TimeConstants.HOUR]
         *  * [TimeConstants.DAY]
         *
         * @return time span in unit
         */
        fun millis2TimeSpan(millis: Long, @TimeConstants.Unit unit: Int): Long {
            return millis / unit
        }

        /**
         * Milliseconds to fit time span.
         *
         * @param millis    The milliseconds.
         *
         * millis &lt;= 0, return null
         * @param precision The precision of time span.
         *
         *  * precision = 0, return null
         *  * precision = 1, return 天
         *  * precision = 2, return 天, 小时
         *  * precision = 3, return 天, 小时, 分钟
         *  * precision = 4, return 天, 小时, 分钟, 秒
         *  * precision &gt;= 5，return 天, 小时, 分钟, 秒, 毫秒
         *
         * @return fit time span
         */
        fun millis2FitTimeSpan(millis: Long, precision: Int): String? {
            var varMillis = millis
            var varPrecision = precision
            if (varMillis <= 0 || varPrecision <= 0) return null
            val sb = StringBuilder()
            val units = arrayOf("天", "小时", "分钟", "秒", "毫秒")
            val unitLen = intArrayOf(86400000, 3600000, 60000, 1000, 1)
            varPrecision = Math.min(varPrecision, 5)
            for (i in 0 until varPrecision) {
                if (varMillis >= unitLen[i]) {
                    val mode = varMillis / unitLen[i]
                    varMillis -= mode * unitLen[i]
                    sb.append(mode).append(units[i])
                }
            }
            return sb.toString()
        }

        /**
         * Input stream to output stream.
         *
         * @param is The input stream.
         * @return output stream
         */
        fun input2OutputStream(`is`: InputStream?): ByteArrayOutputStream? {
            if (`is` == null) return null
            val os = ByteArrayOutputStream()
            val b = ByteArray(MemoryConstants.KB)
            var len: Int = -1
            `is`.use {
                while (`is`.read(b, 0, MemoryConstants.KB).apply { len = this } != -1) {
                    os.write(b, 0, len)
                }
            }
            return os
        }

        /**
         * Input stream to bytes.
         *
         * @param is The input stream.
         * @return bytes
         */
        fun inputStream2Bytes(`is`: InputStream?): ByteArray? {
            return if (`is` == null) null else input2OutputStream(`is`)!!.toByteArray()
        }

        /**
         * Bytes to input stream.
         *
         * @param bytes The bytes.
         * @return input stream
         */
        fun bytes2InputStream(bytes: ByteArray?): InputStream? {
            return if (bytes == null || bytes.isEmpty()) null else ByteArrayInputStream(bytes)
        }

        /**
         * Output stream to bytes.
         *
         * @param out The output stream.
         * @return bytes
         */
        fun outputStream2Bytes(out: OutputStream?): ByteArray? {
            return if (out == null) null else (out as ByteArrayOutputStream).toByteArray()
        }

        /**
         * Bytes to output stream.
         *
         * @param bytes The bytes.
         * @return output stream
         */
        fun bytes2OutputStream(bytes: ByteArray?): OutputStream? {
            if (bytes == null || bytes.isEmpty()) return null
            val os = ByteArrayOutputStream()
            os.use {
                os.write(bytes)
                return os
            }
        }

        /**
         * Input stream to string.
         *
         * @param is          The input stream.
         * @param charsetName The name of charset.
         * @return string
         */
        fun inputStream2String(`is`: InputStream?, charsetName: Charset): String? {
            if (`is` == null || isSpace(charsetName.toString())) return null
            return try {
                String(inputStream2Bytes(`is`)!!, charsetName)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                null
            }

        }

        /**
         * String to input stream.
         *
         * @param string      The string.
         * @param charsetName The name of charset.
         * @return input stream
         */
        fun string2InputStream(string: String?, charsetName: String): InputStream? {
            if (string == null || isSpace(charsetName)) return null
            return try {
                ByteArrayInputStream(string.toByteArray(charset(charsetName)))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                null
            }

        }

        /**
         * Output stream to string.
         *
         * @param out         The output stream.
         * @param charsetName The name of charset.
         * @return string
         */
        fun outputStream2String(out: OutputStream?, charsetName: Charset): String? {
            if (out == null || isSpace(charsetName.toString())) return null
            return try {
                String(outputStream2Bytes(out)!!, charsetName)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                null
            }

        }

        /**
         * String to output stream.
         *
         * @param string      The string.
         * @param charsetName The name of charset.
         * @return output stream
         */
        fun string2OutputStream(string: String?, charsetName: String): OutputStream? {
            if (string == null || isSpace(charsetName)) return null
            return try {
                bytes2OutputStream(string.toByteArray(charset(charsetName)))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                null
            }

        }

        /**
         * Bitmap to bytes.
         *
         * @param bitmap The bitmap.
         * @param format The format of bitmap.
         * @return bytes
         */
        fun bitmap2Bytes(bitmap: Bitmap?, format: Bitmap.CompressFormat): ByteArray? {
            if (bitmap == null) return null
            val baos = ByteArrayOutputStream()
            bitmap.compress(format, 100, baos)
            return baos.toByteArray()
        }

        /**
         * Bytes to bitmap.
         *
         * @param bytes The bytes.
         * @return bitmap
         */
        fun bytes2Bitmap(bytes: ByteArray?): Bitmap? {
            return if (bytes == null || bytes.isEmpty())
                null
            else
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

        /**
         * Drawable to bitmap.
         *
         * @param drawable The drawable.
         * @return bitmap
         */
        fun drawable2Bitmap(drawable: Drawable): Bitmap {
            if (drawable is BitmapDrawable) {
                if (drawable.bitmap != null) {
                    return drawable.bitmap
                }
            }
            val bitmap: Bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
                Bitmap.createBitmap(1, 1,
                        if (drawable.opacity != PixelFormat.OPAQUE)
                            Bitmap.Config.ARGB_8888
                        else
                            Bitmap.Config.RGB_565)
            } else {
                Bitmap.createBitmap(drawable.intrinsicWidth,
                        drawable.intrinsicHeight,
                        if (drawable.opacity != PixelFormat.OPAQUE)
                            Bitmap.Config.ARGB_8888
                        else
                            Bitmap.Config.RGB_565)
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }

        /**
         * Drawable to bytes.
         *
         * @param drawable The drawable.
         * @param format   The format of bitmap.
         * @return bytes
         */
        fun drawable2Bytes(drawable: Drawable?,
                           format: Bitmap.CompressFormat): ByteArray? {
            return if (drawable == null) null else bitmap2Bytes(drawable2Bitmap(drawable), format)
        }

        /**
         * View to bitmap.
         *
         * @param view The view.
         * @return bitmap
         */
        fun view2Bitmap(view: View?): Bitmap? {
            if (view == null) return null
            val ret = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(ret)
            val bgDrawable = view.background
            if (bgDrawable != null) {
                bgDrawable.draw(canvas)
            } else {
                canvas.drawColor(Color.WHITE)
            }
            view.draw(canvas)
            return ret
        }

        private fun isSpace(s: String?): Boolean {
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
    }
}

private fun Byte.ushr(i: Int): Byte {
    return this.toInt().ushr(i).toByte()
}

private infix fun Byte.shr(j: Int): Byte {
    return this.toInt().shr(j).toByte()
}

private infix fun Byte.shl(j: Int): Byte {
    return this.toInt().shl(j).toByte()
}

/**
 * Bitmap to drawable.
 *
 * @param bitmap The bitmap.
 * @return drawable
 */
private infix fun Context.bitmap2Drawable(bitmap: Bitmap?): Drawable? {
    return BitmapDrawable(resources, bitmap)
}

/**
 * Bytes to drawable.
 *
 * @param bytes The bytes.
 * @return drawable
 */
private infix fun Context.bytes2Drawable(bytes: ByteArray?): Drawable? {
    return if (bytes == null) null else bitmap2Drawable(bytes2Bitmap(bytes))
}

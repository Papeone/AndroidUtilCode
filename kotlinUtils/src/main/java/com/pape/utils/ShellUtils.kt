package com.pape.utils

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader

/**
 * author：huangchen on 2018/8/29 20:30
 * email：huangchen@yonghui.cn
 * desc  : utils about shell
 */
object ShellUtils {


    /**
     * The result of command.
     */
    class CommandResult(var result: Int?, var successMsg: String?, var errorMsg: String?)


    private val LINE_SEP = System.getProperty("line.separator")

    /**
     * Execute the command.
     *
     * @param command The command.
     * @param isRoot  True to use root, false otherwise.
     * @return the single [CommandResult] instance
     */
    fun execCmd(command: String, isRoot: Boolean): CommandResult {
        return execCmd(arrayOf(command), isRoot, true)
    }

    /**
     * Execute the command.
     *
     * @param commands The commands.
     * @param isRoot   True to use root, false otherwise.
     * @return the single [CommandResult] instance
     */
    fun execCmd(commands: List<String>?, isRoot: Boolean): CommandResult {
        return execCmd(commands?.toTypedArray(), isRoot, true)
    }

    /**
     * Execute the command.
     *
     * @param command         The command.
     * @param isRoot          True to use root, false otherwise.
     * @param isNeedResultMsg True to return the message of result, false otherwise.
     * @return the single [CommandResult] instance
     */
    fun execCmd(command: String,
                isRoot: Boolean,
                isNeedResultMsg: Boolean): CommandResult {
        return execCmd(arrayOf(command), isRoot, isNeedResultMsg)
    }

    /**
     * Execute the command.
     *
     * @param commands        The commands.
     * @param isRoot          True to use root, false otherwise.
     * @param isNeedResultMsg True to return the message of result, false otherwise.
     * @return the single [CommandResult] instance
     */
    fun execCmd(commands: List<String>?,
                isRoot: Boolean,
                isNeedResultMsg: Boolean): CommandResult {
        return execCmd(commands?.toTypedArray(),
                isRoot,
                isNeedResultMsg)
    }

    /**
     * Execute the command.
     *
     * @param commands        The commands.
     * @param isRoot          True to use root, false otherwise.
     * @param isNeedResultMsg True to return the message of result, false otherwise.
     * @return the single [CommandResult] instance
     */
    @JvmOverloads
    fun execCmd(commands: Array<String>?,
                isRoot: Boolean,
                isNeedResultMsg: Boolean = true): CommandResult {
        var result = -1
        if (commands == null || commands.size == 0) {
            return CommandResult(result, null, null)
        }
        var process: Process? = null
        var successResult: BufferedReader? = null
        var errorResult: BufferedReader? = null
        var successMsg: StringBuilder? = null
        var errorMsg: StringBuilder? = null
        var os: DataOutputStream? = null
        try {
            process = Runtime.getRuntime().exec(if (isRoot) "su" else "sh")
            os = DataOutputStream(process!!.outputStream)
            for (command in commands) {
                if (command == null) continue
                os.write(command.toByteArray())
                os.writeBytes(LINE_SEP)
                os.flush()
            }
            os.writeBytes("exit$LINE_SEP")
            os.flush()
            result = process.waitFor()
            if (isNeedResultMsg) {
                successMsg = StringBuilder()
                errorMsg = StringBuilder()
                successResult = BufferedReader(InputStreamReader(process.inputStream,
                        "UTF-8"))
                errorResult = BufferedReader(InputStreamReader(process.errorStream,
                        "UTF-8"))
                var line: String? = null
                if (successResult.readLine().let { line = it;it != null }) {
                    successMsg.append(line)
                    while (successResult.readLine().let { line = it;it != null }) {
                        successMsg.append(LINE_SEP).append(line)
                    }
                }
                if (errorResult.readLine().let { line = it;it != null }) {
                    errorMsg.append(line)
                    while (errorResult.readLine().let { line = it;it != null }) {
                        errorMsg.append(LINE_SEP).append(line)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                os?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                successResult?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                errorResult?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            process?.destroy()
        }
        return CommandResult(
                result,
                successMsg?.toString(),
                errorMsg?.toString()
        )
    }
}

package com.pape.utils

import java.io.DataOutputStream
import java.nio.charset.Charset

/**
 * 功能描述：
 * Created by Administrator on 2018/5/4.
 */
data class CommandResult(var result: Int, var successMsg: String?, var errorMsg: String?)

object ShellExt {
    private val LINE_SEP by lazy { System.getProperty("line.separator") }

    fun execCmd(commands: List<String>): CommandResult = execCmd(*commands.toTypedArray())

    // commands 不接收null
    fun execCmd(vararg commands: String, isRoot: Boolean = true, isNeedResultMsg: Boolean = true): CommandResult {
        var result = -1
        val process: Process = Runtime.getRuntime().exec(if (isRoot) "su" else "sh")
        val os = DataOutputStream(process.outputStream)
        for (command in commands) {
            os.write(command.toByteArray())
            os.writeBytes(LINE_SEP)
            os.flush()
        }
        os.writeBytes("exit" + LINE_SEP)
        os.flush()
        result = process.waitFor()
        if (isNeedResultMsg) {
            try {
                val successMsg = StringBuilder()
                val errorMsg = StringBuilder()
                val successResult = process.inputStream.bufferedReader(Charset.forName("UTF-8"))
                val errorResult = process.errorStream.bufferedReader(Charset.forName("UTF-8"))
                successResult.use {
                    successResult.readLine()?.let {
                        successMsg.append(this)
                        while (true) {
                            successResult.readLine()?.let { successMsg.append(LINE_SEP).append(this) } ?: break
                        }
                    }
                }
                errorResult.use {
                    errorResult.readLine()?.let {
                        errorMsg.append(this)
                        while (true) {
                            errorResult.readLine()?.let { errorMsg.append(LINE_SEP).append(this) } ?: break
                        }
                    }
                }
                return CommandResult(result, successMsg.toString(), errorMsg.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                process.destroy()
            }
        }
        return CommandResult(result, null, null)
    }
}
package com.pape.utils

import android.support.v4.app.NavUtils
import java.io.FileOutputStream
import java.util.zip.ZipOutputStream

/**
 * 功能描述：
 * Created by Administrator on 2018/4/17.
 */
class ZipExt {

    val BUFFER_LEN: Int = 8192

    private constructor() {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    fun zipFiles(srcFiles: Collection<String>, zipFilePaths: String): Boolean {
        return zipFiles(srcFiles, zipFilePaths, null)
    }

    fun zipFiles(srcFilePaths: Collection<String>?, zipFilePath: String?, comment: String):Boolean {
        if (srcFilePaths == null || zipFilePath == null) return false
        val zos:ZipOutputStream
        try {
            zos = ZipOutputStream(FileOutputStream(zipFilePath))
            srcFilePaths.map { if (!zipFile(getFileByPath(it), "", zos, comment)) return false}
        }finally {

        }
    }


}
package com.ruoyi.common.utils

import java.nio.charset.StandardCharsets
import jakarta.servlet.ServletOutputStream
import jakarta.servlet.http.HttpServletResponse

fun HttpServletResponse.download(fileName: String, block: (ServletOutputStream) -> Unit) {
    this.contentType = "application/octet-stream"
    this.setHeader(
        "Content-Disposition",
        "attachment; filename=${String(fileName.toByteArray(), StandardCharsets.ISO_8859_1)}"
    )
    this.outputStream.use {
        block(it)
    }
}

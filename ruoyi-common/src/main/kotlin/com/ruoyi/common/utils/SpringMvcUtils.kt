package com.ruoyi.common.utils

import java.nio.charset.StandardCharsets
import jakarta.servlet.ServletOutputStream
import jakarta.servlet.http.HttpServletResponse
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.Closeable
import java.util.function.Consumer

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

fun HttpServletResponse.download(fileName: String, consumer: Consumer<ServletOutputStream>) {
    this.contentType = "application/octet-stream"
    this.setHeader(
        "Content-Disposition",
        "attachment; filename=${String(fileName.toByteArray(), StandardCharsets.ISO_8859_1)}"
    )
    this.outputStream.use {
        consumer.accept(it)
    }
}

fun ServletOutputStream.writeXSSFWorkbook(workbook: XSSFWorkbook) {
    workbook.use {
        it.write(this)
    }
}

fun XSSFWorkbook.writeKt(stream: ServletOutputStream) {
    this.use {
        it.write(stream)
    }
}

fun HttpServletResponse.image(byteArray: ByteArray) {
    this.contentType = "image/png"
    this.outputStream.use {
        it.write(byteArray)
        it.flush()
    }
}
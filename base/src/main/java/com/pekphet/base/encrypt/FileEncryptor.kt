// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.encrypt

import android.util.Base64
import com.pekphet.base.components.ZInfoRecorder
import com.pekphet.base.components.safe
import com.pekphet.base.components.toHexStringL
import com.pekphet.base.components.toJson
import com.pekphet.base.encrypt.FileEncryptor.decryptTo
import com.pekphet.base.encrypt.FileEncryptor.encryptTo
import com.pekphet.base.net.ex.generateNewFile
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.spec.SecretKeySpec

object FileEncryptor {

    private const val _ENCRYPT_ALGORITHM = "AES"
    private const val _ENCRYPT_CIPHER_MODE = "AES/ECB/PKCS7Padding"

    private const val _ENCRYPT_SIZE = 16

    private lateinit var mEncCipher: Cipher
    private lateinit var mDecCipher: Cipher

    fun initCipher(accId: String) {
        if (accId.isEmpty()) return
        val keySpec = SecretKeySpec(getPwd(accId), _ENCRYPT_ALGORITHM)
        mEncCipher = Cipher.getInstance(_ENCRYPT_CIPHER_MODE)
        mDecCipher = Cipher.getInstance(_ENCRYPT_CIPHER_MODE)
        ZInfoRecorder.e("ENCRYPT", "BLOCK: ${mEncCipher.blockSize}")
        ZInfoRecorder.e("ENCRYPT", "BLOCK: ${mDecCipher.blockSize}")
        mEncCipher.init(Cipher.ENCRYPT_MODE, keySpec)
        mDecCipher.init(Cipher.DECRYPT_MODE, keySpec)
    }

    fun isInited() = FileEncryptor::mDecCipher.isInitialized

    fun getPwd(pwdStr: String): ByteArray {
        val md = MessageDigest.getInstance("SHA-256")
        val sha256ResultStr = md.digest(pwdStr.toByteArray()).toHexStringL()
        ZInfoRecorder.e("SHA256RESULT", sha256ResultStr)
        val sha256Result = sha256ResultStr.toByteArray()
        val bytes = ByteArray(_ENCRYPT_SIZE) { 0x00 }
        if (sha256Result.size <= _ENCRYPT_SIZE) {
            System.arraycopy(sha256Result, 0, bytes, 0, sha256Result.size)
        } else {
            System.arraycopy(sha256Result, 0, bytes, 0, _ENCRYPT_SIZE)
        }
        ZInfoRecorder.e("encrypt", "key:${sha256Result.toJson()}")
        ZInfoRecorder.e("encrypt", "key:${bytes.toJson()}")
        return bytes
    }

    fun String.UDSEncryptStr() = safe { mEncCipher.doFinal(toByteArray()).toBase64() }
    fun ByteArray.UDSEncrypt() = safe { mEncCipher.doFinal(this) }
    fun ByteArray.UDSDecrypt() = safe { mDecCipher.doFinal(this) }
    fun ByteArray.UDSDecryptStr() = safe { String(mDecCipher.doFinal(this)) }


    fun InputStream.decryptTo(out: OutputStream, bufferSize: Int = DEFAULT_BUFFER_SIZE): Long {
        val deIS = CipherInputStream(this, mDecCipher)
        var bytesCopied: Long = 0
        val buffer = ByteArray(bufferSize)
        var bytes = deIS.read(buffer)
        while (bytes >= 0) {
            out.write(buffer, 0, bytes)
            bytesCopied += bytes
            bytes = deIS.read(buffer)
        }
        safe { deIS.close() }
        return bytesCopied
    }

    fun InputStream.encryptTo(out: OutputStream, bufferSize: Int = DEFAULT_BUFFER_SIZE): Long {
        val enIS = CipherInputStream(this, mEncCipher)
        var bytesCopied: Long = 0
        val buffer = ByteArray(bufferSize)
        var bytes = enIS.read(buffer)
        while (bytes >= 0) {
            out.write(buffer, 0, bytes)
            bytesCopied += bytes
            bytes = enIS.read(buffer)
        }
        safe { enIS.close() }
        return bytesCopied
    }


}

fun ByteArray.toBase64(flag: Int = Base64.NO_WRAP) = Base64.encodeToString(this, flag)

fun File.copyDecrypt(dstFile: File, bufferSize: Int = 1024 * 128) {
    try {
        dstFile.generateNewFile()
        this.inputStream().use { input ->
            dstFile.outputStream().use { output ->
                input.decryptTo(output, bufferSize)
            }
        }
    } catch (ex: Exception) {
    ex.printStackTrace()
        dstFile.delete()
    }
}

fun File.copyEncrypt(dstFile: File, bufferSize: Int = DEFAULT_BUFFER_SIZE) {
    dstFile.generateNewFile()
    this.inputStream().use { input ->
        dstFile.outputStream().use { output ->
            input.encryptTo(output, bufferSize)
        }
    }
}

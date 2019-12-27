package de.nenick.playground.networkingcornet

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import java.lang.reflect.Type
import java.nio.ByteBuffer
import java.nio.charset.Charset
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CronetCoroutinesCallback<T>(val gson: Gson, val continuation: Continuation<T>) :
    UrlRequest.Callback() {

    override fun onRedirectReceived(request: UrlRequest, info: UrlResponseInfo, newLocationUrl: String) {
        // Determine whether you want to follow the redirect.
        val shouldFollow = true

        if (shouldFollow) {
            request.followRedirect()
        } else {
            request.cancel()
        }
    }

    override fun onResponseStarted(request: UrlRequest, info: UrlResponseInfo) {
        val buffer = ByteBuffer.allocateDirect(100 * 1024)
        val httpStatusCode = info.httpStatusCode
        if (httpStatusCode == 200) {
            // The request was fulfilled. Start reading the response.
            request.read(buffer)
        } else {
            // You should still check if the request contains some data.
            request.read(buffer)
        }
    }

    override fun onReadCompleted(request: UrlRequest, info: UrlResponseInfo, buffer: ByteBuffer) {
        // The response body is available, process byteBuffer.
        buffer.flip()
        buffer.let {
            val bytes = ByteArray(it.remaining())
            it.get(bytes)
            String(bytes, Charset.forName("UTF-8"))
        }.apply {
            val listType: Type = object : TypeToken<T>() {}.type

            val myItems: T = gson.fromJson(this, listType)
            continuation.resume(myItems)
        }

        // Continue reading the response body by reusing the same buffer
        // until the response has been completed.
        buffer.clear()
        request.read(buffer)
    }

    override fun onFailed(request: UrlRequest, info: UrlResponseInfo, error: CronetException) {
        continuation.resumeWithException(RuntimeException("request failed"))
    }

    override fun onSucceeded(request: UrlRequest, info: UrlResponseInfo) {
        // Request has completed successfully
    }
}
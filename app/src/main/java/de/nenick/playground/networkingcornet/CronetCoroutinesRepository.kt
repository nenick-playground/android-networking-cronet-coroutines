package de.nenick.playground.networkingcornet

import android.content.Context
import com.google.gson.Gson
import org.chromium.net.CronetEngine
import java.util.concurrent.Executors
import kotlin.coroutines.suspendCoroutine

class CronetCoroutinesRepository(context: Context) {

    private val gson = Gson()
    private val executor = Executors.newSingleThreadExecutor()
    private val cronetEngineBuilder = CronetEngine.Builder(context)
    private val cronetEngine = cronetEngineBuilder.build()

    suspend fun readTodos() = suspendCoroutine<List<TodoItem>> { continuation ->
        val callback = CronetCoroutinesCallback(gson, continuation)
        cronetEngine.newUrlRequestBuilder("https://jsonplaceholder.typicode.com/todos", callback, executor).build().start()
    }
}


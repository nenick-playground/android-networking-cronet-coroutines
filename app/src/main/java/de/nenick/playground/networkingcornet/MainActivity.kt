package de.nenick.playground.networkingcornet

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.suspendCoroutine

class MainActivity : AppCompatActivity(), LifecycleOwner {

    private val repository by lazy { CronetCoroutinesRepository(applicationContext) }
    private val items = MutableLiveData<List<TodoItem>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        items.observe(this, Observer {
            Toast.makeText(this, "Ready, found ${it.size} todos", Toast.LENGTH_LONG).show()
        })

        btn_request.setOnClickListener {
            Toast.makeText(this, "Wait until request is done ...", Toast.LENGTH_LONG).show()
            CoroutineScope(Dispatchers.IO).launch { items.postValue(repository.readTodos()) }
        }
    }
}
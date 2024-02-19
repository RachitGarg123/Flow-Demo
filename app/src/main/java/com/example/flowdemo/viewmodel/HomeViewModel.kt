package com.example.flowdemo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flowdemo.repo.HomeRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {

    private val repo = HomeRepo()

    fun getLatestInteger() {
        viewModelScope.launch {
            repo.latestInteger
                .filter { num ->  (num % 2) == 0 } //Intermediate operators used in flow to manipulate data
                .map { filteredNum -> filteredNum + 9 }
                .flowOn(Dispatchers.Default) // upstream will be executing in default thread pool instead of main thread
                .catch {
                    Log.e("Error", "error ----> ${it.message}")
                    emit(7777) // we can catch exceptions and also emit values
                }
                .collectLatest{ latestInteger -> // terminal operator to fetch data from flow
//                    delay(500L) // If Items are producing faster than consumed and we are using collectLatest then only latest value will be collected unlike collect which collects all values
                    Log.i("Integer","latest integer ---> $latestInteger")
                }
        }
    }

    fun zipAndCombine() {
        viewModelScope.launch {
            val numbers = (1..3).asFlow().onEach {
                delay(300)
            }
            val numberOfTexts = flowOf("one", "two", "three").onEach {
                delay(400)
            }
            val startTime = System.currentTimeMillis()

        }
    }
}


/*
* Flow collection can stop because of:
    * The collecting coroutine is cancelled
    * The producer finishes emitting items */
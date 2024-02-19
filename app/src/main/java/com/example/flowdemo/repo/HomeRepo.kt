package com.example.flowdemo.repo

import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow

class HomeRepo {

    /*flow Builder {executed within a coroutine}
     It's a cold flow i.e executed only when there is someone to collect
     It executes lazily by default*/

    val latestInteger: Flow<Int> = flow {
        for(i in 0..<8) {
            if(i == 5) {
                throw CancellationException("Cancellation Exception Thrown")
            }
            Log.i("Integer", "i---> $i")
            emit(i)
            delay(200L)
        }
    }


    val latestString: Flow<String> = flow {
        for(i in 10..<18) {
            if(i == 15) {
                throw CancellationException("Cancellation Exception Thrown Again")
            }
            Log.i("Integer", "i---> $i")
            emit("$i")
            delay(200L)
        }
    }


    //alternative of above code
//    val latestInteger = listOf(1, 2, 3, 4, 5, 6, 7).asFlow()
}
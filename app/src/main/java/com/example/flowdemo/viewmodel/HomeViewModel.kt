package com.example.flowdemo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flowdemo.repo.HomeRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {

    private val repo = HomeRepo()

    fun getLatestInteger() {
        viewModelScope.launch {
            val integerFlow = repo.latestInteger
                .filter { num ->  (num % 2) == 0 } //Intermediate operators used in flow to manipulate data
                .map { filteredNum -> filteredNum + 9 }
//                .withIndex() // map values with indexes
                .flowOn(Dispatchers.Default) // upstream will be executing in default thread pool instead of main thread
                .catch {
                    Log.e("Error", "error ----> ${it.message}")
                    emit(7777) // we can catch exceptions and also emit values
//                    emit(IndexedValue(777, 7777)) // when using withIndex Operator
                }
//                .collectLatest{ latestInteger -> // terminal operator to fetch data from flow
////                    delay(500L) // If Items are producing faster than consumed and we are using collectLatest then only latest value will be collected unlike collect which collects all values
//                    Log.i("Integer","latest integer ---> $latestInteger")
//                }
            repo.latestString
                .stateIn( // state in is used to convert a cold flow to a StateFlow
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000), // whileSubscribed is used to collect data as long as producer is active and we can allot some time to make it active for few more seconds {in this case its 5 sec, default value is 0} so if user has pressed back by mistake he can come back quickly and there wouldn't be any changes
//                started = SharingStarted.Eagerly, // starts immediately and last forever until the scope is active
//                started = SharingStarted.Lazily, // starts when there is one active subscriber and last forever until the scope is active
                initialValue = "3")
                .shareIn( // state in is used to convert a cold flow to a StateFlow
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    replay = 1)// how many old values we need we can mention it in replay parameter
                .filter { num ->  (num.toInt() % 2) == 0 } //Intermediate operators used in flow to manipulate data
                .map { filteredNum -> filteredNum + 9 }
//                .withIndex() // map values with indexes
                .flowOn(Dispatchers.Default) // upstream will be executing in default thread pool instead of main thread
                .catch {
                    Log.e("Error", "error ----> ${it.message}")
                    emit("7777") // we can catch exceptions and also emit values
//                    emit(IndexedValue(777, 7777)) // when using withIndex Operator
                }
                .buffer(onBufferOverflow = BufferOverflow.DROP_LATEST) // DROP_LATEST drops the latest value and retain the old value whereas DROP_OLDEST does the opposite
//                .take(2) // only 2 elements will be collected
//                .takeWhile {// we can give a condition inside takeWhile so that it only collect items that satisfies the condition and as soon as the condition fails it will not collect any other elements
//                    it.length > 2
//                }

//                .combine(integerFlow) { valueOne, valueTwo -> // In case of combine operator it will not wait for the other flow to make pair with it instead it will let the values come and make pair ith the value currently flowing and als it makes flow with every value from other flow
//                    "valueOne ---> $valueOne valueTwo -----> $valueTwo"
//                }
//                .zip(integerFlow) { valueOne, valueTwo -> // In case of zip operator values from both flows are zipped {collected from the output of zip including change in data type} and will wait for the other flow to emit a value to form a pair and minimum size will be taken i.e if one flow emits 1 value and other emits 10 values then only 1 pair will be formed
//                    "valueOne ---> $valueOne valueTwo -----> $valueTwo"
//                }
                .collect { latestInteger -> // terminal operator to fetch data from flow
                    delay(2000L) // If Items are producing faster than consumed and we are using collectLatest then only latest value will be collected unlike collect which collects all values
                    Log.i("Integer","latest integer ---> $latestInteger")
                }
//            val finalResult = merge(flowOne, flowTwo)
//            finalResult.collectLatest {
//                Log.i("MergedValues", "after merging ----> $it") // output ----> merged values coming from both flows in any unpredictable order
//            }
        }
    }

/*    one example of merge zip and combine operators

    flow1 -> {1, 2, 3, 4, 5}
    flow2 -> {6, 7, 8}

    merge -> {1, 6, 2, 3, 7, 8, 4, 5} -> merged both flows in any order

    let's say we add delay of 1000 ms in flowOne and 400 ms in flowTwo then
    flow.zip -> {{1, 6}, {2, 7}, {3, 8}} // we can manipulate values according to our will

    flow.combine -> {{1, 8}, {2, 8}, {3, 8}, {4, 8}, {5, 8}} // since first flow is having delay of 1 sec then current value from flow2 will be 8 then such pair is formed in combine*/
}


/*
* Flow collection can stop because of:
    * The collecting coroutine is cancelled
    * The producer finishes emitting items */
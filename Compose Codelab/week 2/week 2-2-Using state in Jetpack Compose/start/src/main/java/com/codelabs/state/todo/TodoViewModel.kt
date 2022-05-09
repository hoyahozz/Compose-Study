/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codelabs.state.todo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// ViewModel 을 이용해 State Hoisting
class TodoViewModel : ViewModel() {

    // private state
    private var currentEditPosition by mutableStateOf(-1)

    // Livedata 를 제거하고 바로 mutableStateListOf 로 리팩터링을 진행한다.
    // Livedata 사용의 오버헤드가 줄어든다.
    // private var _todoItems = MutableLiveData(listOf<TodoItem>())
    // val todoItems: LiveData<List<TodoItem>> = _todoItems

    // state : todoItems
    var todoItems = mutableStateListOf<TodoItem>()
        private set // 비공개 setter 로 전환, viewModel 내부에서만 작성 가능

    // state
    val currentEditItem : TodoItem?
        get() = todoItems.getOrNull(currentEditPosition)

    // event : addItem
    fun addItem(item: TodoItem) {
        todoItems.add(item)
    //  _todoItems.value = _todoItems.value!! + listOf(item)
    }

    // event : removeItem
    fun removeItem(item: TodoItem) {
        todoItems.remove(item)
        // _todoItems.value = _todoItems.value!!.toMutableList().also {
        //  it.remove(item)
        // }
        onEditDone() // 항목 제거시 편집기를 활성화하지 않는 고런 느낌..
    }

    // event: onEditItemSelected
    fun onEditItemSelected(item: TodoItem) {
        currentEditPosition = todoItems.indexOf(item)
    }

    // event: onEditDone
    fun onEditDone() {
        currentEditPosition = -1
    }

    // event: onEditItemChange
    fun onEditItemChange(item: TodoItem) {
        val currentItem = requireNotNull(currentEditItem)
        require(currentItem.id == item.id) {
            "You can only change an item with the same id as currentEditItem"
        }

        todoItems[currentEditPosition] = item
    }
}

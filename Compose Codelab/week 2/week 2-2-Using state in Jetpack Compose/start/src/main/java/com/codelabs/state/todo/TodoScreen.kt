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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codelabs.state.util.generateRandomTodoItem
import kotlin.random.Random

/*
    State Hoisting 시 중요한 세 가지 규칙
    1. State 를 끌어올릴 때는 필요한 만큼만 끌어올리기, 즉 가장 낮은 공통의 컴포넌트로 끌어올려야 한다.
    2. State 는 적어도 변경될 수 있는 수준으로 끌어올려야 한다.
    3. 동일한 이벤트에 대한 응답으로 두 State 가 변경되는 경우 두 State 모두 끌어올려야 한다.
 */


/**
 * Stateless component that is responsible for the entire todo screen.
 * 스테이트리스한 컴포저블, 상태 직접 변경 불가능! (이로운거)
 * @param items (state) list of [TodoItem] to display
 * @param onAddItem (event) request an item be added
 * @param onRemoveItem (event) request an item be removed
 */
@Composable
fun TodoScreen(
    items: List<TodoItem>,
    currentlyEditing: TodoItem?, // 현재 수정중인 것
    onAddItem: (TodoItem) -> Unit,
    onRemoveItem: (TodoItem) -> Unit,
    onStartEdit: (TodoItem) -> Unit,
    onEditItemChange: (TodoItem) -> Unit,
    onEditDone: () -> Unit
) {
    Column {
        val enableTopSection = currentlyEditing == null // 현재 편집 중인지 확인하기 위한 변수
        TodoItemInputBackground(elevate = true, modifier = Modifier.fillMaxWidth()) {
            // 편집중이 아니면 입력창 표시
            if (enableTopSection) {
                TodoItemEntryInput(onItemComplete = onAddItem)
            }
            // 편집중이면 편집중임을 알림
            else {
                Text(
                    "수정 중!",
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(16.dp)
                        .fillMaxWidth()
                )
            }
        }

        // 리사이클러뷰와 달리 재활용이 아예 일어나지 않고 표시된 항목만 구성, 화면을 벗어나는 즉시 삭제한다.
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(top = 8.dp)
        ) {
            items(items = items) { todo ->
                if (currentlyEditing?.id == todo.id) { // 수정 중일 때
                    TodoItemInlineEditor(
                        item = currentlyEditing,
                        onEditItemChange = onEditItemChange,
                        onEditDone = onEditDone,
                        onRemoveItem = { onRemoveItem(todo) }
                    )
                } else { // 수정 중이지 않을 때
                    TodoRow(
                        todo,
                        { onStartEdit(it) },
                        modifier = Modifier.fillParentMaxWidth()
                    )
                }
            }
        }

        // For quick testing, a random item generator button
        Button(
            onClick = { onAddItem(generateRandomTodoItem()) },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Text("Add random item")
        }
    }
}

/**
 * Stateless composable that displays a full-width [TodoItem].
 *
 * @param todo item to show
 * @param onItemClicked (event) notify caller that the row was clicked
 * @param modifier modifier for this element
 */
@Composable
fun TodoRow(
    todo: TodoItem,
    onItemClicked: (TodoItem) -> Unit,
    modifier: Modifier = Modifier,
    iconAlpha: Float = remember(todo.id) { randomTint() }
) {
    Row(
        modifier = modifier
            .clickable { onItemClicked(todo) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(todo.task)
        // 아이콘 명도를 랜덤으로 지정
        // 리컴포저블이 발생해도 remember 를 통해 이전 값을 반환
        // todo.id 값을 키 값으로 전달, todo.id 에 값이 전달되지 않는 이상 값은 바뀌지 않음.
        // val iconAlpha: Float = remember(todo.id) { randomTint() }
        Icon(
            imageVector = todo.icon.imageVector,
            // LocalContentColor :: 아이콘 및 서체와 같은 컨텐츠의 기본 색상 제공
            tint = LocalContentColor.current.copy(alpha = iconAlpha),
            contentDescription = stringResource(id = todo.icon.contentDescription)
        )
    }
}

@Composable
fun TodoInputTextField(text: String, onTextChange: (String) -> Unit, modifier: Modifier) {
    // val (text, setText) = remember { mutableStateOf("") }
    TodoInputText(text, onTextChange, modifier)
}


// 완료 시 이벤트 트리거
@Composable
fun TodoItemEntryInput(onItemComplete: (TodoItem) -> Unit) {
    // 상태 끌어올리기 (State Hoisting)
    // 중요 :: Stateless 컴포저블에는 UI 관련 코드가 모두 있고,
    // Stateful 컴포저블에는 UI 관련 코드가 없다.
    // 이를 통해 UI 코드를 재사용 가능하게 설정할 수 있다.
    val (text, setText) = remember { mutableStateOf("") }
    val (icon, setIcon) = remember { mutableStateOf(TodoIcon.Default) }
    val iconsVisible = text.isNotBlank()
    val submit = {
        onItemComplete(TodoItem(text, icon))
        setIcon(TodoIcon.Default)
        setText("")
    }
    TodoItemInput(
        text = text,
        onTextChange = setText,
        onIconChange = setIcon,
        submit = submit,
        iconsVisible = iconsVisible,
        icon = icon,
        buttonSlot = {
            TodoEditButton(onClick = submit, text = "Add", enabled = text.isNotBlank())
        }
    )
}

@Composable
fun TodoItemInput(
    text: String,
    onTextChange: (String) -> Unit,
    onIconChange: (TodoIcon) -> Unit,
    submit: () -> Unit,
    iconsVisible: Boolean,
    icon: TodoIcon,
    buttonSlot: @Composable () -> Unit
) {
    Column {
        Row(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {
            TodoInputText(
                text = text,
                onTextChange = onTextChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                onImeAction = submit // 키보드 작업
            )
//            TodoEditButton(
//                onClick = submit,
//                text = "Add",
//                modifier = Modifier.align(Alignment.CenterVertically),
//                enabled = text.isNotBlank() // 텍스트가 있을 때만 활성화
//            )

            // New code: Replace the call to TodoEditButton with the content of the slot
            // TodoEditButton 에 대한 호출을 슬롯 형태로 변경

            Spacer(modifier = Modifier.width(8.dp))
            Box(Modifier.align(Alignment.CenterVertically)) { buttonSlot() }
        }
        if (iconsVisible) { // 텍스트가 비어있지 않으면 아이콘 목록 출
            AnimatedIconRow(icon = icon, onIconChange = onIconChange, Modifier.padding(top = 8.dp))
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TodoItemInlineEditor(
    item: TodoItem,
    onEditItemChange: (TodoItem) -> Unit,
    onEditDone: () -> Unit,
    onRemoveItem: () -> Unit
) = TodoItemInput(
    text = item.task,
    onTextChange = { onEditItemChange(item.copy(task = it)) },
    onIconChange = { onEditItemChange(item.copy(icon = it)) },
    icon = item.icon,
    submit = onEditDone,
    iconsVisible = true,
    buttonSlot = {
        Row {
            val shrinkButtons = Modifier.widthIn(20.dp)
            TextButton(onClick = onEditDone, modifier = shrinkButtons) {
                Text(
                    text = "\uD83D\uDCBE", // floppy disk
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(30.dp)
                )
            }
            TextButton(onClick = onRemoveItem, modifier = shrinkButtons) {
                Text(
                    text = "❌",
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(30.dp)
                )
            }
        }
    }
)


private fun randomTint(): Float {
    return Random.nextFloat().coerceIn(0.3f, 0.9f)
}

@Preview
@Composable
fun PreviewTodoScreen() {
    val items = listOf(
        TodoItem("Learn compose", TodoIcon.Event),
        TodoItem("Take the codelab"),
        TodoItem("Apply state", TodoIcon.Done),
        TodoItem("Build dynamic UIs", TodoIcon.Square)
    )

    Surface {
        TodoScreen(items, null, {}, {}, {}, {}, {})
    }
}

@Preview
@Composable
fun PreviewTodoRow() {
    val todo = remember { generateRandomTodoItem() }
    Surface {
        TodoRow(todo = todo, onItemClicked = {}, modifier = Modifier.fillMaxWidth())
    }
}

package co.kr.yapp.android.basicscodelab

import androidx.activity.ComponentActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.kr.yapp.android.basicscodelab.ui.theme.BasicsCodelabTheme

class LayoutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BasicsCodelabTheme {
                Surface {
//                    MyColumnTest()
                    StaggeredGridExample()
                }
            }
        }
    }

    // Modifier 확장 함수를 생성, 컴포넌트를 측정하고 배치하는 것을 수동으로 제어한다.
    private fun Modifier.firstBaselineToTop(
        firstBaselineToTop: Dp
    ) = this.then( // this.then -> Modifier 인터페이스에 현재 함수 생성하기..?
        layout { measurable, constraints ->
            // 측정이 완료되면 배치 가능한 값으로 탈바꿈한다!
            val placeable = measurable.measure(constraints)

            check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
            val firstBaseline = placeable[FirstBaseline]

            val placeableY = firstBaselineToTop.roundToPx() - firstBaseline
            val height = placeable.height + placeableY

            // x, y 로 실제 배치, 최종 크기 결정
            layout(placeable.width, height) {
                placeable.placeRelative(0, placeableY)
            }
        }
    )

    @Composable
    fun AppBarLayout() {
        Scaffold(topBar = {
            TopAppBar(title = { Text(text = "AppBarLayout") })
        }) {
            Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                Text(text = "Hello World", modifier = Modifier.padding(top = 36.dp))
                Text(text = "Hello World", modifier = Modifier.firstBaselineToTop(36.dp))
            }
        }
    }

    @Composable
    fun MyOwnRealRow(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        Layout(
            content = content,
            modifier = modifier,
            measurePolicy = { measurables: List<Measurable>,
                              constraints: Constraints ->

                var containerHeight = 0
                var containerWidth = 0

                val placeables = measurables.map { measurable ->

                    val placeable = measurable.measure(constraints)
                    containerWidth += placeable.width
                    if (containerHeight <= placeable.height)
                        containerHeight = placeable.height
                    placeable
                }

                // Track the y co-ord we have placed children up to
                var xPosition = 0

                // Set the size of the layout as big as it can
                // 최종 크기
                layout(containerWidth, containerHeight) {
                    // Place children in the parent layout
                    placeables.forEach { placeable ->
                        // Position item on the screen
                        // 화면에 아이템의 위치 지정
                        placeable.placeRelative(x = xPosition, y = 0)

                        // Record the y co-ord placed up to
                        // 아이템의 y 값을 차곡차곡 더해준다.
                        xPosition += placeable.width
                    }
                }
            }
        )
    }

    @Composable
    fun MyOwnRealColumn(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit // Slot!
    ) {
        Layout(
            content = content,
            modifier = modifier,
            measurePolicy = { measurables: List<Measurable>, // 레이아웃에 들어온 자식 아이템들
                              constraints: Constraints ->

                var containerHeight = 0
                var containerWidth = 0

                // 측정 가능한 아이템들을 Placeable 로 변환
                val placeables = measurables.map { measurable ->
                    // Measure each child (각 자식들을 측정)
                    val placeable = measurable.measure(constraints)
                    containerHeight += placeable.height // 높이는 각각 더 해주기
                    if (containerWidth <= placeable.width) // 가장 가로가 긴 아이템
                        containerWidth = placeable.width

                    placeable
                }

                // Track the y co-ord we have placed children up to
                var yPosition = 0

                // Set the size of the layout as big as it can
                // 최종 크기
                layout(containerWidth, containerHeight) {
                    // Place children in the parent layout
                    placeables.forEach { placeable ->
                        // Position item on the screen
                        // 화면에 아이템의 위치 지정
                        placeable.placeRelative(x = 0, y = yPosition)

                        // Record the y co-ord placed up to
                        // 아이템의 y 값을 차곡차곡 더해준다.
                        yPosition += placeable.height
                    }
                }
            })
    }

    @Composable
    fun MyOwnColumn(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        Layout(
            modifier = modifier,
            content = content
        ) { measurables, constraints ->
            // Don't constrain child views further, measure them with given constraints
            // List of measured children
            val placeables = measurables.map { measurable ->
                Log.d("TAG", measurable.toString())
                // Measure each child
                measurable.measure(constraints)
            }

            // Track the y co-ord we have placed children up to
            var yPosition = 0

            // Set the size of the layout as big as it can
            layout(constraints.maxWidth, constraints.maxHeight) {
                // Place children in the parent layout
                placeables.forEach { placeable ->
                    // Position item on the screen
                    placeable.placeRelative(x = 0, y = yPosition)

                    // Record the y co-ord placed up to
                    yPosition += placeable.height
                }
            }
        }
    }

    val topics = listOf(
        "Arts & Crafts", "Beauty", "Books", "Business", "Comics", "Culinary",
        "Design", "Fashion", "Film", "History", "Maths", "Music", "People", "Philosophy",
        "Religion", "Social sciences", "Technology", "TV", "Writing"
    )

    @Composable
    fun StaggeredGridExample(modifier: Modifier = Modifier) {
        Row(
            modifier = modifier
                .background(color = Color.LightGray, shape = RectangleShape)
                .padding(16.dp)
                .size(200.dp)
                .background(Color.Yellow)
                .horizontalScroll(rememberScrollState())
        ) {
            StaggeredGrid {
                for (topic in topics) {
                    Chip(modifier = Modifier.padding(8.dp).background(Color.Blue), text = topic)
                }
            }
        }
    }

    @Composable
    fun Chip(modifier: Modifier = Modifier, text: String) {
        Card(
            modifier = modifier,
            border = BorderStroke(color = Color.Black, width = Dp.Hairline),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp, 16.dp)
                        .background(color = MaterialTheme.colors.secondary)
                )
                Spacer(Modifier.width(4.dp))
                Text(text = text)
            }
        }
    }

    @Preview
    @Composable
    fun ChipPreview() {
        BasicsCodelabTheme() {
            StaggeredGridExample()
        }
    }

    @Composable
    fun MyColumnTest() {
        Column {
            MyOwnRealRow(Modifier.background(Color.Yellow)) {
                Text(text = "안녕하세요 컴포즈")
                Text(text = "레이아웃 너무 어려워")
                Text(text = "정신 나갈 것 같애")
            }

            Row(Modifier.background(Color.Blue)) {
                Text(text = "안녕하세요 컴포즈")
                Text(text = "레이아웃 너무 어려워")
                Text(text = "정신 나갈 것 같애")
            }
        }

    }

    @Preview
    @Composable
    fun DefaultLayout() {
        BasicsCodelabTheme {
            MyColumnTest()
        }
    }
}
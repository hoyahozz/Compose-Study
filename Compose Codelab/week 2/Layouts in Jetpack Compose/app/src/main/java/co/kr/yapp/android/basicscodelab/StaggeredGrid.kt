package co.kr.yapp.android.basicscodelab

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout

@Composable
fun StaggeredGrid(
    modifier: Modifier = Modifier,
    rows: Int = 3,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->

        // Keep track of the width of each row
        // 각 행의 가로 배열
        val rowWidths = IntArray(rows) { 0 }

        // Keep track of the max height of each row
        // 각 행의 높이 배열
        val rowHeights = IntArray(rows) { 0 }

        // Don't constrain child views further, measure them with given constraints
        // List of measured children
        // measureable 로 placeables 만들기
        val placeables = measurables.mapIndexed { index, measurable ->

            // Measure each child
            val placeable = measurable.measure(constraints)

            // Track the width and max height of each row
            val row = index % rows
            // 각 배열에 각각 아이템의 너비와 높이를 집어 넣는다.
            rowWidths[row] += placeable.width
            // 아이템 중 가장 높은 높이를 가진 요소를 집어 넣는다.
            rowHeights[row] = Math.max(rowHeights[row], placeable.height)

            placeable
        }

        // Grid's width is the widest row
        // 가로 배열 중 가장 높은 배열을 가져온다.
        val containerWidth = rowWidths.maxOrNull() // (containerWidth)
            // 값이 범위 안에 있으면 해당 값을, 값이 범위 안에 없으면 경계값을 리턴한다.
                // 여기서는 컨테이너의 가장 낮은 너비 - 높은 너비를 범위 지정하여 경계값에 없으면 가장 높은 너비를 리턴하는 방식
                // null 이면 가장 낮은 너비로 지정
            ?.coerceIn(constraints.minWidth.rangeTo(constraints.maxWidth))
            ?: constraints.minWidth

        // Grid's height is the sum of the tallest element of each row
        // coerced to the height constraints
        // 그리드의 높이는 각 행들의 가장 높은 요소들의 높이 합계
        val height = rowHeights.sumOf { it }
            .coerceIn(constraints.minHeight.rangeTo(constraints.maxHeight))

        // Y of each row, based on the height accumulation of previous rows
        // 각 행의 Y 위치를 담은 배열을 생성한다.
        val rowY = IntArray(rows) { 0 }
        for (i in 1 until rows) {
            rowY[i] = rowY[i - 1] + rowHeights[i - 1]
        }

        // Set the size of the parent layout
        layout(containerWidth, height) {
            // x cord we have placed up to, per row
            val rowX = IntArray(rows) { 0 }

            placeables.forEachIndexed { index, placeable ->
                val row = index % rows
                placeable.placeRelative(
                    x = rowX[row],
                    y = rowY[row]
                )
                rowX[row] += placeable.width
            }
        }
        // measure and position children given constraints logic here
    }
}
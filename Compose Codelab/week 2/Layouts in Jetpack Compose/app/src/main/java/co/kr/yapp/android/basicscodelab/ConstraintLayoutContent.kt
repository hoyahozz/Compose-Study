package co.kr.yapp.android.basicscodelab

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.*
import co.kr.yapp.android.basicscodelab.ui.theme.BasicsCodelabTheme

@Composable
fun ConstraintLayoutContent() {
    ConstraintLayout {

        // Composable References, id 지정과 같음.
        val (button1, button2, text) = createRefs()

        Button(
            onClick = { /* do Something */ },
            modifier = Modifier.constrainAs(button1) {
                // 기존의 ConstraintLayout 과 같이 링크 방식
                top.linkTo(parent.top, margin = 16.dp)
                start.linkTo(parent.start, margin = 15.dp)
            }
        ) {
            Text(text = "Button1")
        }

        Text(text = "Text", Modifier.constrainAs(text) {
            top.linkTo(button1.bottom, margin = 16.dp)
            // Text의 start 및 end 를 모두 parent의 가장자리로 설정
//            centerHorizontallyTo(parent)
            centerAround(button1.end)
        })

        // button1 과 text 의 끝을 기준으로 잡음
        // 현재 예시에서는 text 가 더 오른쪽에 있으니 text 기준
        val barrier = createEndBarrier(button1, text)
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier.constrainAs(button2) {
                top.linkTo(parent.top, margin = 16.dp)
                start.linkTo(barrier)
            }
        ) {
            Text(text = "Button 2")
        }
    }
}

@Composable
fun LargeConstraintLayout() {
    ConstraintLayout {
        val text = createRef()

        // 시작 가이드라인 지정
        val guideline = createGuidelineFromStart(fraction = 0.5f)
        Text(text = "이건 엄청나게 긴 텍스트 입니다라다라다라다라다라다라",
            modifier = Modifier.constrainAs(text) {
                linkTo(start = guideline, end = parent.end)
                // 텍스트 줄바꿈
//                width = Dimension.preferredWrapContent
                width = Dimension.preferredWrapContent.atLeast(150.dp)
            }
        )
    }
}

@Composable
fun DecoupledConstraintLayout() {
    BoxWithConstraints {

        // constraints -> 세트 모음
        val constraints = if (maxWidth < maxHeight) {
            decoupledConstraints(margin = 16.dp) // 세로 화면일 때
        } else {
            decoupledConstraints(margin = 32.dp) // 가로 화면일 때
        }

        ConstraintLayout(constraints) {
            Button(
                onClick = {},
                modifier = Modifier.layoutId("Button")
            ) {
                Text("Button")
            }
            Text(text = "Text", Modifier.layoutId("Text"))
        }
    }
}

private fun decoupledConstraints(margin : Dp) : ConstraintSet {
    return ConstraintSet {
        val button = createRefFor("Button")
        val text = createRefFor("Text")

        constrain(button) {
            top.linkTo(parent.top, margin = margin)
        }
        constrain(text) {
            top.linkTo(button.bottom, margin)
        }

    }
}

@Preview
@Composable
fun ConstraintLayoutContentPreview() {
    BasicsCodelabTheme {
        Surface {
            ConstraintLayoutContent()
        }
    }
}

@Preview
@Composable
fun LargeConstraintLayoutPreview() {
    BasicsCodelabTheme {
        Surface {
            LargeConstraintLayout()
        }
    }
}

@Preview
@Composable
fun DecoupledConstraintLayoutPreview() {
    BasicsCodelabTheme {
        Surface {
            DecoupledConstraintLayout()
        }
    }
}

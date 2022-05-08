package com.dongyang.android.basicscodelab

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dongyang.android.basicscodelab.ui.theme.BasicsCodelabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // setContent 내에 사용되는 앱 테마는 프로젝트 이름에 맞게 저장됨
            // 현재 프로젝트 명 : BasicCodelab
            BasicsCodelabTheme {

                /* 중첩 레벨이 많아질수록 유지보수에 어려움을 겪을 수 있음
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
                */
                // BasicCodelabTheme 가 MaterialTheme를 매핑하고 있으므로
                // MyApp 에서도 매터리얼 테마를 사용할 수 있다.
                MyApp()
            }
        }
    }
}

// 컴포즈가 가능한 함수는 @Composable 이라는 어노테이션이 붙은 일반 함수
// 함수가 내부에서 다른 @Composable 함수를 호출할 수 있음

// Composable 재사용
@Composable
private fun MyApp() {

    var shouldShowOnBoarding by rememberSaveable { mutableStateOf(true) }
    // true -> Continue 버튼을 누를 수 있는 화면 출력
    // false -> 리스트 목록 화면 출력
    // rememberSaveable : 화면 회전, 다크모드 변경과 같은 구성 변경에도 상태를 저장함

    if (shouldShowOnBoarding) {
        // 상태가 아닌 함수를 전달하는 방식으로 컴포저블의 재사용성을 높이고 다른 컴포저블이 상태를 변경하지 않도록 보호
        OnboardingScreen(onContinueClicked = { shouldShowOnBoarding = false })
    } else {
        Greetings()
    }
}

@Composable
fun Greeting(name: String) {
    Card(
        backgroundColor = MaterialTheme.colors.primary,
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { Log.d("MainActivity", "Surface 클릭 $name") }
    ) {
        CardContent(name = name)
    }
}

@Composable
private fun CardContent(name: String) {
    // 컴포저블에서 상태 관리를 위한 방법
    // remember 를 이용해 값을 가지고 있는 것
    // 데이터 변경 시 Compose 는 새 데이터로 함수를 재실행하기 때문에 상태를 지정하지 않으면 계속해서 false로 지정될 것
    val expanded = remember { mutableStateOf(false) } // 항목의 상태 지정
    var expanded02 by remember { mutableStateOf(false) }
    val (expanded03, setExpanded03) = remember { mutableStateOf(false) }


//    val extraPadding = if (expanded.value) 48.dp else 0.dp
    // 애니메이션 추가
    val extraPadding by animateDpAsState(
        targetValue = if (expanded.value) 48.dp else 0.dp,
        // 애니메이션 맞춤 설정
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    // Greeting에 배경 색상을 설정하려면 Surface 로 래핑한다.
    // 변경사항이 생기면 상단에 Build & Refresh 버튼이 표시되고, 클릭하면 프로젝트를 빌드하여
    // 미리보기에서 새로운 변경사항 확인 가능
//    Surface(
//        color = MaterialTheme.colors.primary,
//        modifier = Modifier
//            .padding(vertical = 4.dp, horizontal = 8.dp)
//            .clickable { Log.d("MainActivity", "Surface 클릭 $name") }
//    ) { }
    Row(
        modifier = Modifier
            .padding(24.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        // Modifier.weight -> 가중치 지정
        Column(
            modifier =
            Modifier
                .weight(1f)
//                    .padding(bottom = extraPadding.coerceAtLeast(0.dp)) // 패딩이 음수가 되지 않게 설정(음수 되면 앱 다운)
        ) {
            Text("우와 컴포즈!!")
            // 매터리얼 디자인 적용
            // 수정이 필요한 경우 copy 를 이용
            Text(
                text = name, style = MaterialTheme.typography.h4.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
            if (expanded.value) {
                Text(text = "컴포즈는재밌다하지만어렵다재밌지만어렵지만재밌고어렵다하지만신기하다재밌다")
            }
        }
        IconButton(
            onClick = {
                expanded.value = !expanded.value
            })
        {
            Icon(
                imageVector = if (expanded.value) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (expanded.value) "원래 화면으로" else "자세히 보기"
            )
        }
//            OutlinedButton(
//                onClick = {
//                    Log.d("MainActivity", "버튼 클릭 $name")
//                    expanded.value = !expanded.value
////                    expanded02 = !expanded02
////                    setExpanded03(!expanded03)
//                }
//            ) {
//                Text(if (expanded.value) "원래 화면으로" else "자세히 보기")
////                Text(if (expanded02) "원래 화면으로" else "자세히 보기")
////                Text(if (expanded03) "원래 화면으로" else "자세히 보기")
//            }
    }

        // Text(text = "Hello $name!", Modifier.padding(24.dp))
        // 여기서 잠깐!
        // 텍스트가 자동으로 흰색으로 변경되는 것을 확인할 수 있음.
        // androix.compose.material.Surface는 해당 색상에서 적절한 텍스트를 알아서 처리해줌.
        // 배경이 primary로 설정되면 텍스트도 테마에 정의된 onPrimary 색상을 사용해야 하는 것을 자동 인식함.

        // 대부분의 Compose UI 요소는 modifier 매개변수를 선택적으로 허용한다.
        // 예시에서는 padding modifier 를 추가한 모습을 확인할 수 있다.
}



@Composable
private fun Greetings(names: List<String> = List(100) { "$it" }) {
    // LazyColumn -> RecyclerView 와 동일
    // items : 컴포저블을 반복해서 나타내고자 할 때 사용
    // 리사이클러뷰와 같이 뷰를 재활용하며 사용하기 때문에 비용이 적게 든다.
    LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
        items(items = names) { name ->
            Greeting(name)
        }
    }

//    Column(Modifier.padding(vertical = 4.dp)) {
//        for (name in names) {
//            Greeting(name)
//        }
//    }
}

// 새로운 컴포저블 및 미리보기 추가
// 프로젝트 빌드시 여러 개의 미리보기를 확인할 수 있음
@Composable
fun OnboardingScreen(onContinueClicked: () -> Unit) {
    // = 대신 by 키워드를 이용함으로서 매번 .value를 입력하지 않아도 됨
    var shouldShowOnboarding by remember { mutableStateOf(true) }

    Surface {
        // 화면 중앙에 컨텐츠를 표시하도록 Column 구성
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to the Basics Codelab!")
            Button(
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = onContinueClicked
            ) {
                Text("Continue")
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    name = "DefaultPreview"
)
@Composable
fun DefPreview() {
    BasicsCodelabTheme {
        Greetings()
    }
}

// 미리보기를 사용하려면 @Preview 어노테이션을 붙임.
// 어두운 색상 미리보기는 uiMode 에서 조정
@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Composable
fun DefaultPreview() {
    BasicsCodelabTheme {
//        Greeting("Android~!")
        MyApp()
    }
}

// 고정 높이 추가
@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnboardingPreview() {
    BasicsCodelabTheme {
        OnboardingScreen(onContinueClicked = { var shouldShowOnBoarding = false })
    }
}


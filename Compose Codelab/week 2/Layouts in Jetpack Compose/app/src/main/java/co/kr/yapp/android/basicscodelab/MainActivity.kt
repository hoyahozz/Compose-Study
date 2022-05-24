package co.kr.yapp.android.basicscodelab

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import co.kr.yapp.android.basicscodelab.ui.theme.BasicsCodelabTheme
import coil.compose.rememberImagePainter
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BasicsCodelabTheme {
                Surface {
                    LayoutCodeLab(Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
fun LayoutCodeLab(modifier : Modifier = Modifier) {

    val scrollState = rememberLazyListState()
    // 스크롤 하다가 다른 스크롤이 들어오면 지연시키고 그 스크롤을 실행해야 하니깐..! 코루틴스코프
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopLayoutCodeLab()
        },
        bottomBar = {
            BottomLayoutCodeLab()
        },
        floatingActionButton = {
            IconButton(onClick = {
                coroutineScope.launch {
                    // 0 is the first item index
                    scrollState.animateScrollToItem(0)
                }
            }) {
                Icon(imageVector = Icons.Filled.North, contentDescription = "위로")
            }
        }
    ) { padding ->

        SimpleList(modifier, scrollState = scrollState)
    }
}


@Composable
fun SimpleList(modifier: Modifier = Modifier, scrollState: LazyListState) {
    // 컬럼 내에서 스크롤이 가능하도록 설정한다.
    // 컬럼은 화면에 표시하지 않는 항목도 모두 렌더링하므로 크기가 커질수록 성능이 하락한다.
    // scroll modifier 가 필요하지 않은 LazyColumn 사용을 권장한다.
//    val scrollState = rememberScrollState()
//
//    Column(modifier.verticalScroll(scrollState)) {
//        repeat(100) {
//            Text(
//                modifier = modifier.fillMaxWidth(),
//                textAlign = TextAlign.Center,
//                text = "Item #$it"
//            )
//        }
//    }

    val listSize = 150
    val coroutineScope = rememberCoroutineScope()

    Column() {
        Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(onClick = {
                coroutineScope.launch {
                    // 0 is the first item index
                    scrollState.animateScrollToItem(0)
                }
            }) {
                Text("Scroll to the top")
            }

            Button(onClick = {
                coroutineScope.launch {
                    // listSize - 1 is the last index of the list
                    scrollState.animateScrollToItem(listSize - 1)
                }
            }) {
                Text("Scroll to the end")
            }
        }


        LazyColumn(state = scrollState) {
            items(count = listSize) { content ->
                ImageListItem(modifier, index = content)
//            Text(
//                modifier = Modifier.fillMaxWidth(),
//                textAlign = TextAlign.Center,
//                text = "Item #$content"
//            )
            }
        }
    }

}

@Composable
fun ImageListItem(modifier: Modifier = Modifier ,index : Int) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        Image(
            painter = rememberImagePainter( // coil, 이미지를 원격으로 가져온다.
                data = "https://developer.android.com/images/brand/Android_Robot.png"
            ),
            contentDescription = "안드로이드 로고",
            modifier = Modifier.size(50.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = "Item #$index", style = MaterialTheme.typography.subtitle1)
    }
}


@Composable
fun TopLayoutCodeLab() {
    val context = LocalContext.current
    TopAppBar(
        title = { Text(text = "LayoutCodelab") },
        actions = {
            IconButton(onClick = {
                val intent = Intent(context, LayoutActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(Icons.Filled.Favorite, contentDescription = null)
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "내 정보")
            }
        },
        navigationIcon = {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = "새로고침")
        }
    )
}

@Composable
fun BottomLayoutCodeLab() {
    BottomAppBar(
        content = {
            IconButton(modifier = Modifier.weight(0.3f), onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "검색")
            }
            IconButton(modifier = Modifier.weight(0.3f), onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Filled.Article, contentDescription = "아티클")
            }
            IconButton(modifier = Modifier.weight(0.3f), onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Filled.Today, contentDescription = "달력")
            }
        })
}

@Composable
fun StaggeredGridExample(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = "안녕하세요!")
        Text(text = "코드랩에 참여해주셔서 감사해요!")
    }
}


// Modifier 를 매개변수로 설정하면, 외부에서도 Modifier 를 설정할 수 있다는 장점이 있다. (확장성 증대)
@Composable
fun PhotographerCard(modifier: Modifier = Modifier) {
    // clickable 이 padding 보다 먼저 적용되어야 전체 영역 클릭이 가능하다.
    // 명시적 순서 배치로 인해 동작이 명확해지고 예측할 수 있게 된다.
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(topStart = 4.dp, bottomEnd = 4.dp)) // 둥글게 설정
            .background(MaterialTheme.colors.surface)
            .clickable(onClick = { Log.d("Main", "PhotographerCard: Clicked") })
            .padding(16.dp)
    ) {
        Surface(
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
        ) {
            // 이미지 설정은 여기에서
        }
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text("hoyahozz", fontWeight = FontWeight.Bold)
            // CompositionLocalProvider -> 컴포지션 트리를 통해 암시적으로 데이터 전달
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text("3일 전 포스팅", style = MaterialTheme.typography.body2)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BasicsCodelabTheme {
        LayoutCodeLab()
    }
}
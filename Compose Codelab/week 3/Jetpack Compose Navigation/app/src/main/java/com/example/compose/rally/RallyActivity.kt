/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.rally

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.compose.rally.data.UserData
import com.example.compose.rally.ui.accounts.AccountsBody
import com.example.compose.rally.ui.accounts.SingleAccountBody
import com.example.compose.rally.ui.bills.BillsBody
import com.example.compose.rally.ui.components.RallyTabRow
import com.example.compose.rally.ui.overview.OverviewBody
import com.example.compose.rally.ui.theme.RallyTheme

/**
 * This Activity recreates part of the Rally Material Study from
 * https://material.io/design/material-studies/rally.html
 */
class RallyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RallyApp()
        }
    }
}

@Composable
fun RallyApp() {
    RallyTheme {
        val allScreens = RallyScreen.values().toList() // values.toList() -> enum 클래스를 가져옴
        // rememberSavable -> 화면 회전시 보존
        // 현재 스크린
        // var currentScreen by rememberSaveable { mutableStateOf(RallyScreen.Overview) }

        val navController = rememberNavController()
        // 현재 뒤로가기 스택의 마지막을 가져옴, 즉 현재 화면.
        val backstackEntry = navController.currentBackStackEntryAsState()

        // Helper Method, 현재 네비게이션 스택의 Route 이름으로 RallyScreen 의 현재 enum 가져오기
        val currentScreen = RallyScreen.fromRoute(
            backstackEntry.value?.destination?.route
        )

        Scaffold(
            topBar = {
                RallyTabRow(
                    allScreens = allScreens,
                    onTabSelected = { screen ->
                        navController.navigate(route = screen.name)
                        // currentScreen = screen
                    },
                    currentScreen = currentScreen
                )
            }
        ) { innerPadding ->
            RallyNavHost(navController = navController, modifier = Modifier.padding(innerPadding))

//            Box(Modifier.padding(innerPadding)) {
//                // 스크린 값을 가져옴
//                currentScreen.content(
//                    onScreenChange = { screen ->
//                        currentScreen = RallyScreen.valueOf(screen)
//                    }
//                )
//            }
        }
    }
}

@Composable
fun RallyNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController, // 뒤로가기, 백스택 처리, 화면 이동은 모두 navController 를 통해 이뤄진다.
        startDestination = RallyScreen.Overview.name, // 시작점 (Overview.name = Overview)
        modifier = modifier,
        builder =
        {
            // "Accounts"
            val accountsName = RallyScreen.Accounts.name

            // 자세히 보기 화면
            composable(
                route = "$accountsName/{name}", // (Accounts/name)
                arguments = listOf(
                    navArgument("name") {
                        // Make argument type safe
                        // 여기서 매개변수 지정
                        type = NavType.StringType
                    }
                ),
                // Test Code : adb shell am start -d "rally://accounts/Checking" -a android.intent.action.VIEW
                deepLinks = listOf(navDeepLink {
                    uriPattern = "rally://$accountsName/{name}"
                })
            ) { entry -> // Look up "name" in NavBackStackEntry's arguments
                val accountName = entry.arguments?.getString("name") // name 이라는 키로 넣어주기
                // Find first name match in UserData
                val account = UserData.getAccount(accountName)
                // Pass account to SingleAccountBody
                SingleAccountBody(account = account) // 매개변수 넣어주기
            }

            // 화면 이동, route -> 화면 이동을 위한 키 값
            // "Overview"
            composable(RallyScreen.Overview.name) {
                // Text(RallyScreen.Overview.name) // OverView 이면 이 Text 를 반환하자
                // See All 버튼을 눌렀을 때 Accounts 의 이름으로 이동
                OverviewBody(
                    onClickSeeAllAccounts = { navController.navigate(RallyScreen.Accounts.name) },
                    onClickSeeAllBills = { navController.navigate(RallyScreen.Bills.name) },
                    onAccountClick = { selectedAccount ->
                        navigateToSingleAccount(
                            navController,
                            selectedAccount
                        )
                    }
                )
            }
            // "Accounts"
            composable(RallyScreen.Accounts.name) {
                // Text(RallyScreen.Accounts.name)
                AccountsBody(accounts = UserData.accounts,
                    onAccountClick = { selectedAccount ->
                        navigateToSingleAccount(
                            navController,
                            selectedAccount
                        )
                    })
            }
            // "Bills"
            composable(RallyScreen.Bills.name) {
                // Text(RallyScreen.Bills.name)
                BillsBody(bills = UserData.bills)
            }
        })
}

private fun navigateToSingleAccount(
    navController: NavHostController,
    accountName: String
) {
    // Accounts/{path}
    navController.navigate("${RallyScreen.Accounts.name}/$accountName")
}

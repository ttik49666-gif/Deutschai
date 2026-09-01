package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PlacementTestScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.SpeakScreen
import com.example.ui.theme.AIElectricCyan
import com.example.ui.theme.DeutschAITheme
import com.example.ui.theme.GermanBlack
import com.example.ui.theme.GermanGold
import com.example.ui.theme.GermanGoldLight
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DeutschAITheme {
                DeutschAIApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DeutschAIApp(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val showOnboarding by viewModel.showOnboarding.collectAsState()
    val showPlacement by viewModel.showPlacementTest.collectAsState()

    if (showOnboarding) {
        OnboardingScreen(viewModel = viewModel)
    } else if (showPlacement) {
        PlacementTestScreen(viewModel = viewModel)
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                DeutschAIBottomNav(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            },
            containerColor = Slate950
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    MainTab.HOME -> HomeScreen(viewModel = viewModel)
                    MainTab.LEARN -> LearnScreen(viewModel = viewModel)
                    MainTab.SPEAK -> SpeakScreen(viewModel = viewModel)
                    MainTab.PROGRESS -> ProgressScreen(viewModel = viewModel)
                    MainTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun DeutschAIBottomNav(
    currentTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Slate950)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(Slate900)
                .border(1.dp, Slate800, RoundedCornerShape(26.dp))
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Home Tab
            BottomNavItem(
                iconSelected = Icons.Filled.Home,
                iconUnselected = Icons.Outlined.Home,
                label = "Home",
                isSelected = currentTab == MainTab.HOME,
                onClick = { onTabSelected(MainTab.HOME) },
                testTag = "nav_home"
            )

            // 2. Learn Tab
            BottomNavItem(
                iconSelected = Icons.Filled.MenuBook,
                iconUnselected = Icons.Outlined.MenuBook,
                label = "Learn",
                isSelected = currentTab == MainTab.LEARN,
                onClick = { onTabSelected(MainTab.LEARN) },
                testTag = "nav_learn"
            )

            // 3. Center Speak Floating Button (Hero)
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(GermanGold, GermanGoldLight, AIElectricCyan)
                        )
                    )
                    .clickable { onTabSelected(MainTab.SPEAK) }
                    .testTag("nav_speak_hero"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Speak Mode",
                    tint = GermanBlack,
                    modifier = Modifier.size(28.dp)
                )
            }

            // 4. Progress Tab
            BottomNavItem(
                iconSelected = Icons.Filled.AutoGraph,
                iconUnselected = Icons.Outlined.AutoGraph,
                label = "Progress",
                isSelected = currentTab == MainTab.PROGRESS,
                onClick = { onTabSelected(MainTab.PROGRESS) },
                testTag = "nav_progress"
            )

            // 5. Profile Tab
            BottomNavItem(
                iconSelected = Icons.Filled.Person,
                iconUnselected = Icons.Outlined.Person,
                label = "Profile",
                isSelected = currentTab == MainTab.PROFILE,
                onClick = { onTabSelected(MainTab.PROFILE) },
                testTag = "nav_profile"
            )
        }
    }
}

@Composable
fun BottomNavItem(
    iconSelected: ImageVector,
    iconUnselected: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isSelected) iconSelected else iconUnselected,
                contentDescription = label,
                tint = if (isSelected) GermanGold else Slate400,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = if (isSelected) GermanGold else Slate400,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

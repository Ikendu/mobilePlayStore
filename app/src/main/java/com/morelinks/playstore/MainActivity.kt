package com.morelinks.playstore

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.viewinterop.AndroidView
import com.morelinks.playstore.ui.theme.PlayStoreTheme

// Installed app data class with icon
data class InstalledApp(
    val name: String,
    val packageName: String,
    val icon: Drawable
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlayStoreTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "landing") {
                    composable("landing") { PlayStoreLanding(navController) }
                    composable("secondpage") { SecondPage(navController) }
                    composable("thirdpage/{packageName}") { backStackEntry ->
                        val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
                        ThirdPage(navController, packageName)
                    }
                }
            }
        }
    }
}

@Composable
fun PlayStoreLanding(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.playstore_bg),
            contentDescription = "Play Store Landing",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Search icon (invisible but clickable)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 40.dp, end = 20.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.Transparent, // fully invisible
                modifier = Modifier
                    .size(70.dp)
                    .clickable { navController.navigate("secondpage") }
            )
        }
    }
}

@Composable
fun SecondPage(navController: NavHostController) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    val allApps = remember { getInstalledApps(context.packageManager) }

    // ✅ Only match the app "FirstMobile"
    val filteredApp = allApps.firstOrNull {
        it.name.contains("FirstMobile", ignoreCase = true) &&
                it.name.contains(query, ignoreCase = true)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background static image
        Image(
            painter = painterResource(id = R.drawable.secondimage),
            contentDescription = "Search Page",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Search bar row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(top = 27.dp, start = 10.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left search icon (invisible but clickable)
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Transparent,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Input styled like Play Store
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    textStyle = TextStyle(fontSize = 24.sp, color = Color.Black),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(0.65f) // 65% width
                        .background(
                            Color(0xFFF1F3F4), // Play Store-like gray background
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }

            // ✅ Show ONLY the first matching result
            if (query.isNotEmpty() && filteredApp != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("thirdpage/${filteredApp.packageName}")
                        }
                        .background(Color.White.copy(alpha = 0.8f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // App icon
                    AndroidView(
                        factory = { ctx ->
                            android.widget.ImageView(ctx).apply {
                                setImageDrawable(filteredApp.icon)
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .padding(end = 12.dp)
                    )

                    // App name
                    Text(filteredApp.name, fontSize = 20.sp, color = Color.Black)
                }
            }
        }

        // Back icon (invisible but clickable)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 30.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back to Landing",
                tint = Color.Transparent, // invisible
                modifier = Modifier
                    .size(70.dp)
                    .clickable { navController.navigate("landing") }
            )
        }
    }
}

@Composable
fun ThirdPage(navController: NavHostController, packageName: String) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        // Background placeholder image
        Image(
            painter = painterResource(id = R.drawable.thirdimage), // Add your placeholder in res/drawable
            contentDescription = "App Detail Page",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Transparent clickable area (190.dp from top, 40% width)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 190.dp, end = 20.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)  // 40% width
                    .height(50.dp)       // give it some tap area height
                    .background(Color.Transparent) // invisible
                    .clickable {
                        val launchIntent =
                            context.packageManager.getLaunchIntentForPackage(packageName)
                        if (launchIntent != null) {
                            context.startActivity(launchIntent)
                        }
                    }
            )
        }
    }
}



fun getInstalledApps(pm: PackageManager): List<InstalledApp> {
    val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
    return packages
        .map { app ->
            InstalledApp(
                name = pm.getApplicationLabel(app).toString(),
                packageName = app.packageName,
                icon = pm.getApplicationIcon(app)
            )
        }
        .sortedBy { it.name.lowercase() }
}

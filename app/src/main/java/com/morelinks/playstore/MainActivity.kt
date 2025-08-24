package com.morelinks.playstore

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import com.morelinks.playstore.ui.theme.PlayStoreTheme

data class InstalledApp(val name: String, val packageName: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlayStoreTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "landing") {
                    composable("landing") { PlayStoreLanding(navController) }
                    composable("secondpage") { SecondPage(navController) }
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

        // Search icon bottom right
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 30.dp, end = 20.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.Transparent,
                modifier = Modifier
                    .size(100.dp)
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
    val filteredApp = allApps.firstOrNull {
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

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Transparent Search Bar at top with double height
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .height(64.dp) // doubled height
                    .padding(top = 30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Transparent,
                    modifier = Modifier.size(35.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    textStyle = TextStyle(fontSize = 28.sp, color = Color.DarkGray),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .background(
                            if (query.isEmpty()) Color.Transparent else Color(0xFFecefec)
                        )
//                        .padding(10.dp)
                        .height(35.dp),

                    decorationBox = { innerTextField ->
                        if (query.isEmpty()) {
                            Text("", color = Color.Gray, fontSize = 18.sp)
                        }
                        innerTextField()
                    }
                )

            }

            // Show result if found
            if (query.isNotEmpty() && filteredApp != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val launchIntent =
                                context.packageManager.getLaunchIntentForPackage(filteredApp.packageName)
                            if (launchIntent != null) {
                                context.startActivity(launchIntent)
                            }
                        }
                        .background(Color.White.copy(alpha = 0.9f))
                        .padding(16.dp)
                ) {
                    Text(filteredApp.name, fontSize = 18.sp, color = Color.Black)
                }
            }
        }

        // Back icon bottom center
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back to Landing",
                tint = Color.Transparent,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { navController.navigate("landing") }

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
                packageName = app.packageName
            )
        }
        .sortedBy { it.name.lowercase() }
}

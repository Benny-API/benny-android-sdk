package com.bennyapi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bennyapi.ui.theme.BennyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    BennyApplicationTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            Column(
                verticalArrangement = Center,
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
            ) {
                LaunchButton(text = "Launch EBT Balance Link")
            }
        }
    }
}

@Composable
fun LaunchButton(
    text: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Button(
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .padding(16.dp)
            .height(52.dp)
            .fillMaxWidth(),
        onClick = { context.startActivity(Intent(context, EbtBalanceLinkActivity::class.java)) },
    ) {
        Text(text)
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}

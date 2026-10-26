package seifemadhamdy.buzzi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import seifemadhamdy.buzzi.core.navigation.AppNavHost
import seifemadhamdy.buzzi.ui.theme.BuzziTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      BuzziTheme {
        AppNavHost()
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(
    text = "Hello $name!",
    modifier = modifier,
  )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  BuzziTheme {
    Greeting("Android")
  }
}

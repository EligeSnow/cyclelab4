package com.example.cyclelab4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.cyclelab4.ui.theme.Cyclelab4Theme
import kotlin.math.abs

data class SeriesResult(
    val sum: Double,
    val lastTerm: Double,
    val iterations: Int
)

/**
 * Вычисление суммы ряда 1/1! + 1/3! + 1/5! + 1/7! + ...
 * Вычисление прекращается, когда очередное слагаемое < eps.
 */
fun calculateSeriesSum(eps: Double): SeriesResult {
    require(eps > 0) { "Epsilon должно быть больше 0" }

    var sum = 0.0
    var term = 1.0 // Первое слагаемое: 1/1! = 1
    var k = 0
    var count = 0
    var lastTerm = 0.0

    while (abs(term) >= eps) {
        sum += term
        lastTerm = term
        count++
        k++
        // Следующее слагаемое t_k = t_{k-1} / ((2k) * (2k + 1))
        term /= (2.0 * k * (2.0 * k + 1.0))
    }

    return SeriesResult(
        sum = sum,
        lastTerm = lastTerm,
        iterations = count
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Cyclelab4Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Cyclelab4Theme {
        Greeting("Android")
    }
}

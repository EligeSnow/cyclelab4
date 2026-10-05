package com.example.cyclelab4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var epsInput by remember { mutableStateOf("0.0001") }
    var resultState by remember { mutableStateOf<SeriesResult?>(null) }

    val epsValue = epsInput.toDoubleOrNull()
    val isError = epsValue == null || epsValue <= 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Лабораторная работа №4",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Вариант 7: Суммирование ряда",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = epsInput,
            onValueChange = { epsInput = it },
            label = { Text("Точность ε (эпсилон)") },
            placeholder = { Text("Например, 0.0001") },
            isError = isError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        if (isError) {
            Text(
                text = "Введите положительное число (например, 0.0001)",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                epsValue?.let {
                    resultState = calculateSeriesSum(it)
                }
            },
            enabled = !isError,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Вычислить")
        }

        Spacer(modifier = Modifier.height(20.dp))

        resultState?.let { result ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Результаты контрольного расчета:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Сумма ряда (S): ${result.sum}")
                    Text(text = "Последнее слагаемое: ${result.lastTerm}")
                    Text(text = "Количество итераций (N): ${result.iterations}")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Cyclelab4Theme {
        MainScreen()
    }
}

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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

// ============================================================================
// 1. МОДЕЛЬ ДАННЫХ
// ============================================================================

/**
 * Класс данных для сохранения результатов расчета суммы ряда:
 * @param sum итоговая сумма ряда (S)
 * @param lastTerm последнее слагаемое, которое успели прибавить
 * @param iterations количество повторений цикла (N)
 */
data class SeriesResult(
    val sum: Double,
    val lastTerm: Double,
    val iterations: Int
)

// ============================================================================
// 2. ВЫЧИСЛИТЕЛЬНЫЙ АЛГОРИТМ (ВАРИАНТ 7)
// ============================================================================

/**
 * Функция вычисления суммы бесконечного ряда:
 * S = 1/1! + 1/3! + 1/5! + 1/7! + 1/9! + ...
 * где n! = 1 * 2 * 3 * ... * n.
 *
 * Алгоритм прекращает вычисление, как только добавка к сумме (слагаемое)
 * по модулю станет меньше малого заранее определенного числа эпсилон (eps).
 */
fun calculateSeriesSum(eps: Double): SeriesResult {
    require(eps > 0) { "Точность ε должна быть больше 0" }

    var sum = 0.0          // Накопитель суммы ряда
    var term = 1.0         // Первое слагаемое: 1/1! = 1
    var k = 0              // Счетчик порядка слагаемых
    var count = 0          // Счетчик итераций цикла
    var lastTerm = 0.0     // Переменная для сохранения последнего слагаемого

    // Выполнение цикла пока слагаемое по модулю >= заданному эпсилон
    while (abs(term) >= eps) {
        sum += term        // Прибавляем текущее слагаемое к общей сумме
        lastTerm = term    // Запоминаем последнее слагаемое для вывода
        count++            // Увеличиваем количество итераций на 1
        k++

        // Рекуррентная формула расчета следующего слагаемого:
        // Переход от (2k-1)! к (2k+1)! достигается делением на (2k * (2k+1)).
        // Например: от 1! (1) к 3! (6) -> делим на 2 * 3 = 6
        //           от 3! (6) к 5! (120) -> делим на 4 * 5 = 20
        term /= (2.0 * k * (2.0 * k + 1.0))
    }

    return SeriesResult(
        sum = sum,
        lastTerm = lastTerm,
        iterations = count
    )
}

// ============================================================================
// 3. ГЛАВНАЯ АКТИВНОСТЬ И ПОЛЬЗОВАТЕЛЬСКИЙ ИНТЕРФЕЙС
// ============================================================================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Включение полноэкранного режима отображения
        setContent {
            Cyclelab4Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

/**
 * Главный экран приложения с формой ввода, кнопкой и выводом результатов.
 */
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    // Состояние поля ввода эпсилон (по умолчанию 0.0001)
    var epsInput by remember { mutableStateOf("0.0001") }

    // Состояние результатов расчета (null - пока пользователь не нажал кнопку "Вычислить")
    var resultState by remember { mutableStateOf<SeriesResult?>(null) }

    // Валидация введенного значения
    val epsValue = epsInput.toDoubleOrNull()
    val isError = epsValue == null || epsValue <= 0

    // Состояние вертикальной прокрутки для удобства на маленьких экранах
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // --- Заголовок лабораторной работы ---
        Text(
            text = "Лабораторная работа №4",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Вариант 7: Вычисление суммы ряда",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- Карточка с формулой и условием задачи ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Условие задачи (Формула):",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "S = 1/1! + 1/3! + 1/5! + 1/7! + ...",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "где n! = 1 · 2 · 3 · ... · n",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Останов: когда |слагаемое| < ε",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Поле ввода точности ε (эпсилон) ---
        OutlinedTextField(
            value = epsInput,
            onValueChange = { epsInput = it },
            label = { Text("Точность ε (эпсилон)") },
            placeholder = { Text("Например: 0.0001") },
            isError = isError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Подсказка об ошибке в случае некорректного ввода
        if (isError) {
            Text(
                text = "Введите корректное положительное число (например, 0.0001)",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Кнопка запуска вычислений ---
        Button(
            onClick = {
                epsValue?.let {
                    // Вычисляем сумму ряда и сохраняем результат в состояние
                    resultState = calculateSeriesSum(it)
                }
            },
            enabled = !isError, // Кнопка активна только при валидном вводе
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Вычислить")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- Блок вывода результатов (отображается после расчета) ---
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
                    Text(text = "1. Суммы ряда (S): ${result.sum}")
                    Text(text = "2. Последнее слагаемое: ${result.lastTerm}")
                    Text(text = "3. Количество повторений цикла: ${result.iterations}")
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

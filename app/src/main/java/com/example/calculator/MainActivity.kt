package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorApp()
        }
    }
}

@Composable
fun CalculatorApp() {
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("0") }

    val buttons = listOf(
        listOf("C", "(", ")", "/"),
        listOf("7", "8", "9", "*"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf("0", ".", "DEL", "=")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = if (input.isEmpty()) result else input,
            color = Color.White,
            fontSize = 48.sp,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { label ->
                    Button(
                        onClick = {
                            when (label) {
                                "C" -> { input = ""; result = "0" }
                                "DEL" -> { if (input.isNotEmpty()) input = input.drop(1) }
                                "=" -> {
                                    try {
                                        result = eval(input).toString()
                                        input = ""
                                    } catch (e: Exception) {
                                        result = "Error"
                                    }
                                }
                                else -> { input += label }
                            }
                        },
                        modifier = Modifier
                            .size(70.dp)
                            .padding(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (label) {
                                "C", "DEL" -> Color(0xFFFF3B30)
                                "/", "*", "-", "+" -> Color(0xFFFF9500)
                                "=" -> Color(0xFF34C759)
                                else -> Color(0xFF2C2C2E)
                            }
                        )
                    ) {
                        Text(text = label, fontSize = 24.sp, color = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

fun eval(str: String): Double {
    return object : Any() {
        var pos = -1
        var ch = 0
        fun nextChar() {
            ch = if (++pos < str.length) str[pos].toInt() else -1
        }
        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            return x
        }
        fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                when {
                    eat('+'.toInt()) -> x += parseTerm()
                    eat('-'.toInt()) -> x -= parseTerm()
                    else -> return x
                }
            }
        }
        fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                when {
                    eat('*'.toInt()) -> x *= parseFactor()
                    eat('/'.toInt()) -> x /= parseFactor()
                    else -> return x
                }
            }
        }
        fun parseFactor(): Double {
            if (eat('+'.toInt())) return parseFactor()
            if (eat('-'.toInt())) return -parseFactor()
            var x: Double
            val startPos = pos
            if (eat('('.toInt())) {
                x = parseExpression()
                eat(')'.toInt())
            } else if ((ch >= '0'.toInt() && ch <= '9'.toInt()) || ch == '.'.toInt()) {
                while ((ch >= '0'.toInt() && ch <= '9'.toInt()) || ch == '.'.toInt()) nextChar()
                x = str.substring(startPos, pos).toDouble()
            } else {
                throw RuntimeException("Unexpected")
            }
            return x
        }
        fun eat(charToEat: Int): Boolean {
            while (ch == ' '.toInt()) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }
    }.parse()
}


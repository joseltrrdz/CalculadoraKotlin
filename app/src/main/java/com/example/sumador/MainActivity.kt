package com.example.sumador

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sumador.ui.theme.SumadorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SumadorTheme {
                PantallaPrincipal()
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PantallaPrincipal() {
    var entrada by remember { mutableStateOf("") }
    var parentesisAbierto by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val resultado = calcularResultado(entrada) ?: "0"
        Resultado(numero = resultado)


        Row(
            modifier = Modifier.fillMaxWidth() then Modifier.padding(start = 50.dp)
        ){

            BasicTextField(
                value = entrada,
                onValueChange = {
                    entrada = it
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                textStyle = TextStyle(fontSize = 28.sp),
                modifier = Modifier.padding(26.dp)
            )
            OperationButton("⌫") { entrada = borrarUltimoCaracter(entrada) }


        }


        Row {
            OperationButton("C") {
                entrada = ""
                parentesisAbierto = 0
            }
            OperationButton("()") {
                if (parentesisAbierto > 0) {
                    entrada += ")"
                    parentesisAbierto--
                } else {
                    entrada += "("
                    parentesisAbierto++
                }
            }
            OperationButton("%") {
                entrada += "%"
            }
            OperationButton("÷") { entrada += "/" }



        }
        Row{
            NumeroButton("7") { entrada += "7" }
            NumeroButton("8") { entrada += "8" }
            NumeroButton("9") { entrada += "9" }
            OperationButton("x") { entrada += "*" }



        }
        Row {
            NumeroButton("4") { entrada += "4" }
            NumeroButton("5") { entrada += "5" }
            NumeroButton("6") { entrada += "6" }
            OperationButton("+") { entrada += "+" }


        }

        Row {
            NumeroButton("1") { entrada += "1" }
            NumeroButton("2") { entrada += "2" }
            NumeroButton("3") { entrada += "3" }
            OperationButton("-") { entrada += "-" }
        }

        Row {
            NumeroButton(".") { entrada += "." }
            NumeroButton("0") { entrada += "0" }
            Box(
                modifier = Modifier.width(188.dp) // Ancho doble
            ){
                OperationButtonResul("=", onClick = {
                    val result = calcularResultado(entrada)
                    if (result != null) {
                        entrada = result.toString()
                    } else {
                        entrada = "Error"
                    }
                }, width = 100.dp)
            }

        }
    }
}

@Composable
fun OperationButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(94.dp),

        shape = CircleShape

    ) {
        Text(text = label,
                style = TextStyle(fontSize = 40.sp)
        )
    }
}
@Composable
fun OperationButtonResul(label: String, onClick: () -> Unit, width: Dp) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(188.dp, 94.dp),  // Establece el ancho y el alto según tus preferencias
        shape = CircleShape
    ) {
        Text(text = label,
            style = TextStyle(fontSize = 40.sp))
    }
}

@Composable
fun NumeroButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(94.dp),
        shape = CircleShape
    ) {
        Text(text = label,
            style = TextStyle(fontSize = 40.sp))
    }
}

@Composable
fun Resultado(numero: Comparable<*>) {
    Text(
        text = numero.toString(),
        style = TextStyle(fontSize = 60.sp),
        modifier = Modifier.padding(16.dp)
    )
}

fun calcularResultado(entrada: String): Double? {
    if (entrada.isNotEmpty()) {
        try {
            // Reemplaza '%' por '/100' para calcular el porcentaje
            val expresion = entrada.replace("%", "/100")
            val result = evalExpresion(expresion)
            return result
        } catch (e: Exception) {
            return null
        }
    }
    return null
}

fun evalExpresion(expresion: String): Double {
    return object : Any() {
        var pos = -1
        var ch = ' '

        fun nextChar() {
            ch = if (++pos < expresion.length) expresion[pos] else (-1).toChar()
        }

        fun eat(charToEat: Char): Boolean {
            while (ch == ' ') nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < expresion.length) throw RuntimeException("Carácter inesperado: $ch")
            return x
        }

        fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                if (eat('+')) x += parseTerm()
                else if (eat('-')) x -= parseTerm()
                else if (eat('*')) x *= parseTerm()
                else if (eat('/')) x /= parseTerm()
                else return x
            }
        }

        fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                if (eat('*')) x *= parseFactor()
                else if (eat('/')) x /= parseFactor()
                else return x
            }
        }

        fun parseFactor(): Double {
            if (eat('+')) return parseFactor()
            if (eat('-')) return -parseFactor()
            var x: Double
            val startPos = pos
            if (eat('(')) {
                x = parseExpression()
                eat(')')
            } else if (ch in '0'..'9' || ch == '.') {
                while (ch in '0'..'9' || ch == '.') nextChar()
                x = expresion.substring(startPos, pos).toDouble()
            } else {
                throw RuntimeException("Carácter inesperado: $ch")
            }
            if (eat('^')) x *= parseFactor()
            return x
        }
    }.parse()
}

fun borrarUltimoCaracter(entrada: String): String {
    return if (entrada.isNotEmpty()) {
        entrada.substring(0, entrada.length - 1)
    } else {
        ""
    }
}
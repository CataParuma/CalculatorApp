package com.example.calculatorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlin.math.sin

class MainActivity : AppCompatActivity() {
    var tablero : TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tablero = findViewById(R.id.tablero)
    }

    fun calculo(view : View){
        var boton = view as Button
        var textoBoton = boton.text.toString()
        var conc = tablero?.text.toString()+textoBoton
        var mostrar = sinCero(conc)
        if(textoBoton == "=") {

            var respuesta = 0.0

            try {
                respuesta = eval(tablero?.text.toString())
                tablero?.text = respuesta.toString()

            } catch (e : Exception) {
                tablero?.text = e.toString()
            }
        } else if(textoBoton == "C") {
            tablero?.text = "0"
        } else {
            tablero?.text = mostrar
        }
    }

    fun sinCero(str : String) : String {
        var i = 0
        while (i < str.length && str[i] == '0') i++
        var f = StringBuffer(str)
        f.replace(0, i, "")
        return f.toString()
    }

    fun eval(str: String) : Double {

        return object : Any() {
            var pos = -1
            var ch = 0
            fun nextChar() {
                ch = if (++pos < str.length) str[pos].toInt() else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.toInt()) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.toInt())) x += parseTerm()
                    else if (eat('-'.toInt())) x -= parseTerm()
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.toInt())) x *= parseFactor()
                    else if (eat('/'.toInt())) x /= parseFactor()
                    else return x
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
                } else if (ch >= '0'.toInt() && ch <= '9'.toInt() || ch == '.'.toInt()) {
                    while (ch >= '0'.toInt() && ch <= '9'.toInt() || ch == '.'.toInt()) nextChar()
                    x = str.substring(startPos, pos).toDouble()
                } else if (ch >= 'a'.toInt() && ch <= 'z'.toInt()) { // functions
                    while (ch >= 'a'.toInt() && ch <= 'z'.toInt()) nextChar()
                    val func = str.substring(startPos, pos)
                    x = parseFactor()
                    x = if (func == "sqrt") Math.sqrt(x) else if (func == "sin") sin(Math.toRadians(x)) else if (func == "cos") Math.cos(Math.toRadians(x)) else if (func == "tan") Math.tan(Math.toRadians(x)) else throw RuntimeException("Unknown function: $func")
                } else {
                    throw RuntimeException("Unexpected: " + ch.toChar())
                }
                if (eat('^'.toInt())) x = Math.pow(x, parseFactor())
                return x
            }
        }.parse()
    }
}
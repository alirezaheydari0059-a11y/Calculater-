package com.example.calculator

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.text.DecimalFormat

class MainActivity : Activity() {
    private lateinit var tvExpression: TextView
    private lateinit var tvResult: TextView
    private var expression = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvExpression = findViewById(R.id.tvExpression)
        tvResult = findViewById(R.id.tvResult)

        val buttons = listOf(
            R.id.btn0 to "0", R.id.btn1 to "1", R.id.btn2 to "2",
            R.id.btn3 to "3", R.id.btn4 to "4", R.id.btn5 to "5",
            R.id.btn6 to "6", R.id.btn7 to "7", R.id.btn8 to "8",
            R.id.btn9 to "9", R.id.btnPlus to "+", R.id.btnMinus to "-",
            R.id.btnMultiply to "×", R.id.btnDivide to "÷", R.id.btnDot to "."
        )

        buttons.forEach { (id, value) ->
            findViewById<Button>(id).setOnClickListener {
                expression += value
                tvExpression.text = expression
            }
        }

        findViewById<Button>(R.id.btnClear).setOnClickListener {
            expression = ""
            tvExpression.text = ""
            tvResult.text = "0"
        }

        findViewById<Button>(R.id.btnBackspace).setOnClickListener {
            if (expression.isNotEmpty()) {
                expression = expression.dropLast(1)
                tvExpression.text = expression
            }
        }

        findViewById<Button>(R.id.btnEquals).setOnClickListener {
            try {
                val clean = expression.replace("×", "*").replace("÷", "/")
                val res = eval(clean)
                tvResult.text = DecimalFormat("#.######").format(res)
            } catch (e: Exception) {
                tvResult.text = "خطا"
            }
        }
    }

    private fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0
            fun nextChar() { ch = if (++pos < str.length) str[pos].code else -1 }
            fun eat(c: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == c) { nextChar(); return true }
                return false
            }
            fun parse(): Double {
                nextChar()
                val x = parseExp()
                return x
            }
            fun parseExp(): Double {
                var x = parseTerm()
                while (true) {
                    when {
                        eat('+'.code) -> x += parseTerm()
                        eat('-'.code) -> x -= parseTerm()
                        else -> return x
                    }
                }
            }
            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    when {
                        eat('*'.code) -> x *= parseFactor()
                        eat('/'.code) -> x /= parseFactor()
                        else -> return x
                    }
                }
            }
            fun parseFactor(): Double {
                if (eat('+'.code)) return parseFactor()
                if (eat('-'.code)) return -parseFactor()
                var x: Double
                val startPos = pos
                if (eat('('.code)) {
                    x = parseExp()
                    eat(')'.code)
                } else if (ch in '0'.code..'9'.code || ch == '.'.code) {
                    while (ch in '0'.code..'9'.code || ch == '.'.code) nextChar()
                    x = str.substring(startPos, pos).toDouble()
                } else {
                    x = 0.0
                }
                return x
            }
        }.parse()
    }
}

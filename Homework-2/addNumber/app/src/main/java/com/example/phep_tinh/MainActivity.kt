package com.example.phep_tinh

import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var currentOp = ""   // STATE: phép toán đang chọn

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val edtA = findViewById<EditText>(R.id.edtA)
        val edtB = findViewById<EditText>(R.id.edtB)
        val txtResult = findViewById<TextView>(R.id.txtResult)

        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val btnSub = findViewById<Button>(R.id.btnSub)
        val btnMul = findViewById<Button>(R.id.btnMul)
        val btnDiv = findViewById<Button>(R.id.btnDiv)

        // Reset màu button
        fun resetButtonColor() {
            btnAdd.setBackgroundColor(Color.LTGRAY)
            btnSub.setBackgroundColor(Color.LTGRAY)
            btnMul.setBackgroundColor(Color.LTGRAY)
            btnDiv.setBackgroundColor(Color.LTGRAY)
        }

        // Tính toán
        fun calculate() {
            val a = edtA.text.toString().toDoubleOrNull()
            val b = edtB.text.toString().toDoubleOrNull()

            if (a == null || b == null || currentOp.isEmpty()) {
                txtResult.text = "Kết quả:"
                return
            }

            if (currentOp == "÷" && b == 0.0) {
                txtResult.text = "Không chia cho 0"
                return
            }

            val result = when (currentOp) {
                "+" -> a + b
                "-" -> a - b
                "×" -> a * b
                "÷" -> a / b
                else -> 0.0
            }

            txtResult.text = "Kết quả: $result"
        }

        // Button events
        btnAdd.setOnClickListener {
            currentOp = "+"
            resetButtonColor()
            btnAdd.setBackgroundColor(Color.RED)
            calculate()
        }

        btnSub.setOnClickListener {
            currentOp = "-"
            resetButtonColor()
            btnSub.setBackgroundColor(Color.YELLOW)
            calculate()
        }

        btnMul.setOnClickListener {
            currentOp = "×"
            resetButtonColor()
            btnMul.setBackgroundColor(Color.BLUE)
            calculate()
        }

        btnDiv.setOnClickListener {
            currentOp = "÷"
            resetButtonColor()
            btnDiv.setBackgroundColor(Color.DKGRAY)
            calculate()
        }
    }
}

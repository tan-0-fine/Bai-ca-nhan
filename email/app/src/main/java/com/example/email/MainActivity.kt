package com.example.email

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Áp dụng padding hệ thống (đúng mục đích)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ===== Ánh xạ View =====
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val txtStatus = findViewById<TextView>(R.id.txtStatus)
        val btnCreate = findViewById<Button>(R.id.btnCreate)

        // ===== Xử lý nút kiểm tra =====
        // ===== Xử lý nút kiểm tra =====
        btnCreate.setOnClickListener {
            val email = edtEmail.text.toString().trim()

            txtStatus.visibility = TextView.VISIBLE

            if (!email.contains("@")) {
                edtEmail.setBackgroundResource(R.drawable.bg_input)
                txtStatus.text = "Email không đúng định dạng"
            } else {
                edtEmail.setBackgroundResource(R.drawable.bg_button)
                txtStatus.text = "Bạn đã nhập email hợp lệ"
            }
        }


    }
}

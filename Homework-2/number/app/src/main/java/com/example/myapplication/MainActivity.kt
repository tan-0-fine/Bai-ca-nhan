package com.example.myapplication

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val edtNumber = findViewById<EditText>(R.id.edtNumber)
        val btnCreate = findViewById<Button>(R.id.btnCreate)
        val container = findViewById<LinearLayout>(R.id.container)
        val txtError = findViewById<TextView>(R.id.txtError)

        btnCreate.setOnClickListener {

            // Xóa dữ liệu cũ
            container.removeAllViews()
            txtError.visibility = TextView.GONE

            val input = edtNumber.text.toString()

            // Kiểm tra nhập rỗng
            if (input.isEmpty()) {
                txtError.visibility = TextView.VISIBLE
                return@setOnClickListener
            }

            val number = input.toIntOrNull()

            // Kiểm tra không phải số hoặc <= 0
            if (number == null || number <= 0) {
                txtError.visibility = TextView.VISIBLE
                return@setOnClickListener
            }

            // Tạo danh sách
            for (i in 1..number) {
                val tv = TextView(this)
                tv.text = i.toString()
                tv.textSize = 16f
                tv.setTextColor(Color.WHITE)
                tv.gravity = Gravity.CENTER
                tv.setPadding(0, 24, 0, 24)
                tv.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.bg_item_red
                )

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 12, 0, 12)
                tv.layoutParams = params

                container.addView(tv)
            }
        }
    }
}

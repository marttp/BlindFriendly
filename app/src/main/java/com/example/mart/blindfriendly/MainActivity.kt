package com.example.mart.blindfriendly

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        readTextButton.setOnClickListener{
            val intent = Intent(this, ReadTextActivity::class.java)
            startActivity(intent)
        }

        readImageButton.setOnClickListener{
            val intent = Intent(this, ReadImageActivity::class.java)
            startActivity(intent)
        }
    }


}

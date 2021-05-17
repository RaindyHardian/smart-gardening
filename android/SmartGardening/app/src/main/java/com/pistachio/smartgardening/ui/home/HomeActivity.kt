package com.pistachio.smartgardening.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pistachio.smartgardening.databinding.ActivityMainBinding
import com.pistachio.smartgardening.ui.camera.CameraActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardViewCamera?.setOnClickListener {
            val i = Intent(this, CameraActivity::class.java)
            startActivity(i)
        }
    }
}
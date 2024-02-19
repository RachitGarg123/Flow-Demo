package com.example.flowdemo.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.flowdemo.R
import com.example.flowdemo.viewmodel.HomeViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        homeViewModel = HomeViewModel()
        homeViewModel.getLatestInteger()
    }
}
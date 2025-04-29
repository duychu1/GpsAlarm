package com.ruicomp.onboardmd

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.ruicomp.onboardmd.databinding.ActivityOnboardBinding

class OnboardActivity : AppCompatActivity(), OnboardContentFragment.OnEventClickListener {

    private lateinit var binding: ActivityOnboardBinding
    private lateinit var onboardingPagerAdapter: OnboardingPagerAdapter

    private val totalPages = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOnboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemBar()

        setupViewPager()
        setupButton()
    }

    private fun setupViewPager() {
        onboardingPagerAdapter = OnboardingPagerAdapter(this, totalPages)
        binding.viewPager.adapter = onboardingPagerAdapter

//        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateButtonText(position)

//                if (position % 2 == 1) {
//                    binding.llindicator.visibility = View.GONE
//                } else {
//                    binding.llindicator.visibility = View.VISIBLE
//                }

            }
        })

        updateButtonText(0)
    }

    private fun setupButton() {
//        binding.buttonNext.setOnClickListener {
//            val currentItem = binding.viewPager.currentItem
//            if (currentItem < totalPages - 1) {
//                binding.viewPager.currentItem = currentItem + 1
//            } else {
//                Toast.makeText(this, "Goto Home", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    private fun updateButtonText(position: Int) {
//        if (position == totalPages - 1) {
//            binding.buttonNext.setText(R.string.get_started)
//        } else {
//            binding.buttonNext.setText(R.string.next)
//        }
    }

    override fun onButtonClicked(nPage: Int) {
        val currentItem = binding.viewPager.currentItem
        if (currentItem < totalPages - 1) {
            binding.viewPager.currentItem = currentItem + 1
        } else {
            gotoOther()
        }
    }

    private fun gotoOther() {
        Toast.makeText(this, "Goto Home", Toast.LENGTH_SHORT).show()
    }

    private fun hideSystemBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                // Hide the status bar
                it.hide(WindowInsets.Type.statusBars())
                // Optional: Hide the navigation bar
                it.hide(WindowInsets.Type.navigationBars())

                // Optional: Enable immersive mode - makes system bars translucent and auto-hides them
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, OnboardActivity::class.java))
        }
    }
}

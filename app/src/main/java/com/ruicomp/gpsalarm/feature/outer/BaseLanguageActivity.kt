package com.ruicomp.gpsalarm.feature.outer

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ruicomp.gpsalarm.model.Language
import com.ruicomp.gpsalarm.data.provider.LanguageProvider
import com.ruicomp.gpsalarm.databinding.ActivityLanguageBinding

abstract class BaseLanguageActivity : BaseActivityNonBinding() {

    protected lateinit var binding: ActivityLanguageBinding
    protected lateinit var adapter: LanguageAdapter
    protected var selectedLanguage: Language? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupLanguagesList()
    }

    private fun setupLanguagesList() {
        // Clear any pre-selected language in the shared list
        LanguageProvider.languages.forEach { it.isSelected = false }

        // Subclasses can override this to set initial selection if needed
        setInitialLanguageSelection()

        // Setup RecyclerView
        adapter = LanguageAdapter(LanguageProvider.languages) { language ->
            onLanguageSelected(language)
        }

        binding.rvLanguages.layoutManager = LinearLayoutManager(this)
        binding.rvLanguages.adapter = adapter
    }

    // Abstract methods to be implemented by subclasses
    protected abstract fun setupToolbar()
    protected abstract fun setInitialLanguageSelection()
    protected abstract fun onLanguageSelected(language: Language)
}
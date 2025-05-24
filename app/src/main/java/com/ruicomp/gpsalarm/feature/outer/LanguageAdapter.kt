package com.ruicomp.gpsalarm.feature.outer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ruicomp.gpsalarm.R
import com.ruicomp.gpsalarm.databinding.ItemLanguageBinding
import com.ruicomp.gpsalarm.model.Language

class LanguageAdapter(
    private val languages: List<Language>,
    private val onLanguageClick: (Language) -> Unit
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    class LanguageViewHolder(val binding: ItemLanguageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding = ItemLanguageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LanguageViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val language = languages[position]
        with(holder.binding) {
            ivFlag.setImageResource(language.flagResId)
            tvLanguageName.text = language.name
            rbSelected.isChecked = language.isSelected

            // Set radio button color based on selection
            if (language.isSelected) {
                rbSelected.buttonTintList = ContextCompat.getColorStateList(root.context, R.color.blue_selected)
            } else {
                rbSelected.buttonTintList = ContextCompat.getColorStateList(root.context, R.color.grey_unselected)
            }

            // Set click listener for the entire item
            root.setOnClickListener {
                // Update selection state
                languages.forEach { it.isSelected = false }
                language.isSelected = true
                notifyDataSetChanged()
                onLanguageClick(language)
            }
        }
    }

    override fun getItemCount() = languages.size
}
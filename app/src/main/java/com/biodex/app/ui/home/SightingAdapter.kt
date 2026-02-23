package com.biodex.app.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.biodex.app.databinding.ItemSightingBinding
import com.biodex.app.domain.model.Sighting

class SightingAdapter : ListAdapter<Sighting, SightingAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<Sighting>() {
        override fun areItemsTheSame(oldItem: Sighting, newItem: Sighting) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Sighting, newItem: Sighting) = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemSightingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    class VH(private val binding: ItemSightingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Sighting) {
            binding.tvSpecies.text = item.speciesName
            binding.tvNotes.text = item.notes ?: "—"
        }
    }
}
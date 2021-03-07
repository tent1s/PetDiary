package com.tent1s.android.petdiary.ui.start_activity.pets_list

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tent1s.android.petdiary.databinding.StartPetsListItemBinding
import com.tent1s.android.petdiary.datebase.PetsList
import java.io.File


class PetsListAdapter(val longClickListener: (PetsList) -> Boolean, val clickListener: (PetsList) -> Unit, private val context: Context) : ListAdapter<PetsList,
        PetsListAdapter.ViewHolder>(PetsListDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(item)
        holder.binding.listItem.setOnLongClickListener { longClickListener(item) }
        holder.binding.listItem.setOnClickListener { clickListener(item) }

        val file = File(context.getExternalFilesDir(null), "${item.name}.jpg")

        if (file.exists()){
            Glide
                .with(context)
                .load(file)
                .into(holder.binding.petImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: StartPetsListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PetsList) {

            binding.petName.text = item.name

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = StartPetsListItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}


class PetsListDiffCallback : DiffUtil.ItemCallback<PetsList>() {
    override fun areItemsTheSame(oldItem: PetsList, newItem: PetsList): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: PetsList, newItem: PetsList): Boolean {
        return oldItem == newItem
    }

}

package com.tent1s.android.petdiary.ui.start_activity.pets_list

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tent1s.android.petdiary.databinding.StartPetsListItemBinding
import com.tent1s.android.petdiary.datebase.PetDiaryDatabase
import com.tent1s.android.petdiary.datebase.PetsList
import java.io.File


class PetsListAdapter(val longClickListener: (PetsList) -> Boolean,
                      val clickListener: (PetsList) -> Unit,
                      private val context: Context) : ListAdapter<PetsList,
        PetsListAdapter.ViewHolder>(PetsListDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder,
                                  position: Int) {
        val item = getItem(position)
        with(holder) {
            bind(item, context)
            binding.listItem.setOnLongClickListener { longClickListener(item) }
            binding.listItem.setOnClickListener { clickListener(item) }
        }

    }

    class ViewHolder private constructor(val binding: StartPetsListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PetsList, context: Context) {


            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "${item.name}.jpg")
            if (file.exists()){
                Glide
                        .with(context)
                        .load(file)
                        .centerCrop()
                        .into(binding.petImage)
            }


            binding.petNameTextView.text = item.name

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = StartPetsListItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding, )
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

package com.submission.mystoryapp.ui.story

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.submission.mystoryapp.data.local.entity.StoryEntity
import com.submission.mystoryapp.databinding.StoryListItemBinding

class ListStoryAdapter : PagingDataAdapter<StoryEntity, ListStoryAdapter.ViewHolder>(DIFF_CALLBACK) {
    var onItemClick: ((StoryEntity) -> Unit)? = null

    class ViewHolder(private val binding: StoryListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryEntity) {
            Log.d("ListStoryAdapter", "Story: $story")
            binding.apply {
                tvItemName.text = story.name
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .into(ivItemPhoto)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        StoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
            holder.itemView.setOnClickListener {
                onItemClick?.invoke(story)
            }
        } else {
            Log.d("ListStoryAdapter", "No data")
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }
            override fun getChangePayload(oldItem: StoryEntity, newItem: StoryEntity): Any? {
                return if (areContentsTheSame(oldItem, newItem)) null else newItem
            }
        }
    }
}
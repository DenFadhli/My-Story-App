package com.submission.mystoryapp

import com.submission.mystoryapp.data.local.entity.StoryEntity
import com.submission.mystoryapp.data.network.StoryUploadResponse

object DataDummy {
    fun generateDummyStoryResponse(): List<StoryEntity> {
        val items = arrayListOf<StoryEntity>()
        for (i in 0 until 100){
            val story = StoryEntity(
                "qwerty-1a2s3d4f5g",
                "wasd",
                "testing",
                "https://story-api.dicoding.dev/images/stories/photos.jpg",
                "2022-02-02T22:22:22.269Z",
                8.9375815,
                -18.6172046
            )
            items.add(story)
        }
        return items
    }

    fun generateDummyAddStoryResponse(): StoryUploadResponse {
        return StoryUploadResponse(
            false,
            "success"
        )
    }
}
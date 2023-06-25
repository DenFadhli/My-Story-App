package com.submission.mystoryapp.ui.story

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.submission.mystoryapp.data.local.entity.StoryEntity
import com.submission.mystoryapp.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    @Suppress("DEPRECATION")
    private fun setupView() {
        showLoading(true)
        val story = arguments?.getParcelable<StoryEntity>("story")
        binding?.apply {
            tvDetailUsername.text = story?.name
            tvDetailDesc.text = story?.description
            Glide.with(this@DetailFragment)
                .load(story?.photoUrl)
                .into(ivDetailImg)
        }
        Handler(Looper.getMainLooper()).postDelayed({
            showLoading(false)
        }, 1500)
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            tvDetailUsername.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
            tvDetailDesc.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
            ivDetailImg.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
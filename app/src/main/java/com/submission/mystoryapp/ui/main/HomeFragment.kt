package com.submission.mystoryapp.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.submission.mystoryapp.R
import com.submission.mystoryapp.databinding.FragmentHomeBinding
import com.submission.mystoryapp.di.ViewModelFactory
import com.submission.mystoryapp.ui.story.DetailFragment
import com.submission.mystoryapp.ui.story.ListStoryAdapter
import com.submission.mystoryapp.ui.story.PostFragment
import com.submission.mystoryapp.ui.viewmodel.MainViewModel
import com.submission.mystoryapp.utils.ListLoadingAdapter

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding

    private lateinit var listStoryAdapter: ListStoryAdapter
    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupModel()
        setupView()
        setStories()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }

        listStoryAdapter.onItemClick = { story ->
            val bundle = Bundle().apply {
                putParcelable("story", story)
            }
            val detailFragment = DetailFragment()
            detailFragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_container, detailFragment)
                .addToBackStack(null)
                .commit()
        }

        binding?.fabUpload!!.setOnClickListener {
            val createStoryFragment = PostFragment.newInstance()
            val fragmentManager = parentFragmentManager
            fragmentManager.beginTransaction().apply {
                replace(R.id.frame_container, createStoryFragment, PostFragment::class.java.simpleName)
                addToBackStack(null)
                commit()
            }
        }

        binding?.swipeRefreshLayout?.setOnRefreshListener {
            setStories()
        }
    }

    private fun setupModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext())
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    private fun setupView() {
        listStoryAdapter = ListStoryAdapter()
        binding!!.rvUserStory.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = listStoryAdapter
        }
        binding!!.rvUserStory.adapter = listStoryAdapter.withLoadStateFooter(
            footer = ListLoadingAdapter {
                listStoryAdapter.retry()
            }
        )
        listStoryAdapter.addLoadStateListener { load ->
            when (load.refresh) {
                is LoadState.Loading -> {
                    showLoading(true)
                    showError(false)
                }
                is LoadState.NotLoading -> {
                    showLoading(false)
                    showError(false)
                }
                is LoadState.Error -> {
                    showError(true)
                }
            }
        }
    }

    private fun setStories() {
        mainViewModel.getStories().observe(viewLifecycleOwner) { pagingData ->
            listStoryAdapter.submitData(lifecycle, pagingData)
            binding?.progressBar?.visibility = View.GONE
            binding?.swipeRefreshLayout!!.isRefreshing = false
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding?.rvUserStory?.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding?.fabUpload?.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showError(isError: Boolean) {
        binding?.errorMessage?.visibility = if (isError) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        listStoryAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding!!.rvUserStory.scrollToPosition(0)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
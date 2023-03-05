package com.tapbi.spark.emojimashup.ui.main.favorite

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.tapbi.spark.emojimashup.R
import com.tapbi.spark.emojimashup.databinding.FragmentFavoriteBinding
import com.tapbi.spark.emojimashup.ui.adapter.FavoriteAdapter
import com.tapbi.spark.emojimashup.ui.base.BaseBindingFragment
import com.tapbi.spark.emojimashup.utils.show

class FavoriteFragment: BaseBindingFragment<FragmentFavoriteBinding,FavoriteViewModel>() {
    private lateinit var favoriteAdapter: FavoriteAdapter

    override fun getViewModel(): Class<FavoriteViewModel> {
        return FavoriteViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_favorite

    override fun initView() {
        binding.headerMain.tvTitle.text = getString(R.string.favorite_list)
        favoriteAdapter = FavoriteAdapter()
        binding.rvFavorite.adapter = favoriteAdapter
        binding.rvFavorite.setPadding(0,0,0, getNavigationBarSize())
    }

    override fun initData() {
    }

    override fun observerData() {
        mainViewModel.getFavoriteStickersLiveData.observe(requireActivity()){
            favoriteAdapter.submitList(it)
            binding.tvNoData.show(it.isNullOrEmpty())
        }
    }

    override fun eventClick() {
        binding.headerMain.ivHome.setOnClickListener {
            findNavController().popBackStack()
        }

        favoriteAdapter.setOnItemClickListener {
            findNavController().navigate(FavoriteFragmentDirections.actionFavoriteFragmentToRemixEmojiFragment(
                fromFavoriteFragment = true,
                stickerId = it
            ))
        }
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        
    }

    override fun onPermissionGranted() {
        
    }
}
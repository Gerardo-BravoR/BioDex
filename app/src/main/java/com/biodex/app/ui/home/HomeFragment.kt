package com.biodex.app.ui.home

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.biodex.app.core.BaseFragment
import com.biodex.app.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCreateSighting.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeToCreateSighting()
            findNavController().navigate(action)
        }
    }
}
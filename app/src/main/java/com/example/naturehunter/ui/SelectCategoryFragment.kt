package com.example.naturehunter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.naturehunter.NatureApplication
import com.example.naturehunter.adapter.CategoryListAdapter
import com.example.naturehunter.databinding.FragmentSelectCategoryBinding
import com.example.naturehunter.viewmodel.CategoryViewModel
import com.example.naturehunter.viewmodel.CategoryViewModelFactory

class SelectCategoryFragment : Fragment() {

    private var _binding: FragmentSelectCategoryBinding? = null
    private val binding get() = _binding!!

    private val categoryViewModel: CategoryViewModel by activityViewModels {
        CategoryViewModelFactory((activity?.application as NatureApplication).database.itemDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryListAdapter = CategoryListAdapter(findNavController())

        categoryViewModel.items.observe(viewLifecycleOwner) {
            categoryViewModel.update()
            categoryListAdapter.submitTypeItems(categoryViewModel.typeItems)
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            categoryRecyclerView.adapter = categoryListAdapter
            categoryViewModel = this@SelectCategoryFragment.categoryViewModel
        }

        smoothenNestedScrolling()
        disableRecyclerViewAnimation()
    }

    private fun disableRecyclerViewAnimation() {
        binding.categoryRecyclerView.itemAnimator = null
    }

    private fun smoothenNestedScrolling() {
        ViewCompat.setNestedScrollingEnabled(binding.categoryRecyclerView, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

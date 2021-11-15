package com.example.naturehunter.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.naturehunter.BuildConfig
import com.example.naturehunter.NatureApplication
import com.example.naturehunter.R
import com.example.naturehunter.adapter.ItemListAdapter
import com.example.naturehunter.constant.Type
import com.example.naturehunter.databinding.FragmentItemListBinding
import com.example.naturehunter.viewmodel.ItemViewModel
import com.example.naturehunter.viewmodel.ItemViewModelFactory
import java.io.File

class ItemListFragment : Fragment() {

    private val navigationArguments: ItemListFragmentArgs by navArgs()

    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!

    private val itemViewModel: ItemViewModel by activityViewModels {
        ItemViewModelFactory(
            (activity?.application as NatureApplication).database.itemDao(),
            navigationArguments.type
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setActionBarTitle(navigationArguments.type)
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            itemViewModel = this@ItemListFragment.itemViewModel
            itemListFragment = this@ItemListFragment
            itemListRecyclerView.adapter = ItemListAdapter(findNavController())
        }
    }

    fun takePicture() {
        lifecycleScope.launchWhenStarted {
            createFileUri().let {
                itemViewModel.pictureUri = it.toString()
                takeImageResult.launch(it)
            }
        }
    }

    private fun createFileUri(): Uri {
        val file = File.createTempFile("image_file", ".png", requireActivity().cacheDir)
            .apply {
                createNewFile()
                deleteOnExit()
            }
        return FileProvider.getUriForFile(
            requireContext(),
            "${BuildConfig.APPLICATION_ID}.provider",
            file
        )
    }

    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                startAddItemFragment(navigationArguments.type)
            }
        }

    private fun startAddItemFragment(itemType: Type) {
        findNavController().navigate(
            ItemListFragmentDirections.actionItemListFragmentToAddItemFragment(
                type = itemType,
                uri = itemViewModel.pictureUri
            )
        )
    }

    private fun setActionBarTitle(type: Type) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            if (type == Type.FUNGUS) {
                resources.getString(R.string.hunted_fungi)
            } else {
                resources.getString(
                    R.string.hunted,
                    type.name.lowercase().replaceFirstChar { it.uppercase() }
                )
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
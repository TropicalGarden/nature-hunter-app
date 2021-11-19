package ee.game.naturehunter.ui

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ee.game.naturehunter.BuildConfig
import ee.game.naturehunter.NatureApplication
import ee.game.naturehunter.R
import ee.game.naturehunter.adapter.ItemListAdapter
import ee.game.naturehunter.constant.Type
import ee.game.naturehunter.databinding.FragmentItemListBinding
import ee.game.naturehunter.viewmodel.ItemViewModel
import ee.game.naturehunter.viewmodel.ItemViewModelFactory
import java.io.File

class ItemListFragment : Fragment() {

    private val navigationArguments: ItemListFragmentArgs by navArgs()

    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!

    private val itemViewModel: ItemViewModel by activityViewModels {
        ItemViewModelFactory(
            (activity?.application as NatureApplication).database.itemDao(),
            requireActivity().application
        )
    }

    private val requestCamera =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
            if (permissionGranted) {
                takePicture()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.camera_permission),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                startAddItemFragment(navigationArguments.type)
            }
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
        itemViewModel.updateItems(navigationArguments.type)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            itemViewModel = this@ItemListFragment.itemViewModel
            itemListRecyclerView.adapter = ItemListAdapter(findNavController())
            huntFab.setOnClickListener { requestCamera.launch(Manifest.permission.CAMERA) }
        }
    }

    private fun takePicture() {
        lifecycleScope.launchWhenStarted {
            createFileUri().let {
                itemViewModel.pictureUri = it.toString()
                takeImageResult.launch(it)
            }
        }
    }

    private fun createFileUri(): Uri {
        val file = File.createTempFile("image_file", ".png", requireActivity().filesDir)
        return FileProvider.getUriForFile(
            requireContext(),
            "${BuildConfig.APPLICATION_ID}.provider",
            file
        )
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
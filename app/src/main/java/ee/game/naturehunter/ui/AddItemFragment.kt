package ee.game.naturehunter.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ee.game.naturehunter.NatureApplication
import ee.game.naturehunter.R
import ee.game.naturehunter.constant.Type
import ee.game.naturehunter.data.model.Item
import ee.game.naturehunter.databinding.FragmentAddItemBinding
import ee.game.naturehunter.viewmodel.ItemViewModel
import ee.game.naturehunter.viewmodel.ItemViewModelFactory

class AddItemFragment : Fragment() {

    private val navigationArguments: AddItemFragmentArgs by navArgs()

    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!

    private val itemViewModel: ItemViewModel by activityViewModels {
        ItemViewModelFactory(
            (activity?.application as NatureApplication).database.itemDao(),
            requireActivity().application
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setActionBarTitle(navigationArguments.type)
        _binding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArguments.id
        if (isAddFragment(id)) {
            binding.saveItem.setOnClickListener { addItem() }
            toggleDeleteButtonVisibility(false)
        } else {
            itemViewModel.retrieveItem(id).observe(viewLifecycleOwner) {
                bind(it)
            }
            toggleDeleteButtonVisibility(true)
        }
        binding.itemUri = navigationArguments.uri.toUri()
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun isAddFragment(id: Int): Boolean = id < 0

    private fun toggleDeleteButtonVisibility(showButton: Boolean) {
        binding.deleteItem.visibility = if (showButton) View.VISIBLE else View.GONE
    }

    private fun addItem() {
        if (isEntryValid()) {
            itemViewModel.addItem(
                binding.itemName.text.toString(),
                binding.itemSpecies.text.toString(),
                navigationArguments.type,
                navigationArguments.uri
            )
            toggleError(false)
            startSelectCategoryFragment()
        } else {
            toggleError(true)
        }
    }

    private fun updateItem() {
        if (isEntryValid()) {
            itemViewModel.updateItem(
                navigationArguments.id,
                binding.itemName.text.toString(),
                binding.itemSpecies.text.toString(),
                navigationArguments.type,
                navigationArguments.uri
            )
            toggleError(false)
            startItemListFragment()
        } else {
            toggleError(true)
        }
    }

    private fun isEntryValid(): Boolean {
        return itemViewModel.isEntryValid(binding.itemName.text.toString())
    }

    private fun bind(item: Item) {
        binding.apply {
            itemName.setText(item.itemName, TextView.BufferType.SPANNABLE)
            itemSpecies.setText(item.itemSpecies, TextView.BufferType.SPANNABLE)
            saveItem.setOnClickListener { updateItem() }
            deleteItem.setOnClickListener { showConfirmationDialog(item) }
        }
    }

    private fun showConfirmationDialog(item: Item) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteItem(item)
            }
            .show()
    }

    private fun deleteItem(item: Item) {
        val id = item.id
        val fileUri = item.itemUri.toUri()

        itemViewModel.deletePictureFile(fileUri)
        itemViewModel.deleteItem(id)
        startItemListFragment()
    }

    private fun toggleError(showError: Boolean) {
        if (showError) {
            binding.itemNameLabel.isErrorEnabled = true
            binding.itemNameLabel.error = getString(R.string.error)
        } else {
            binding.itemNameLabel.isErrorEnabled = false
        }
    }

    private fun startSelectCategoryFragment() {
        findNavController().navigate(
            AddItemFragmentDirections.actionAddItemFragmentToSelectCategoryFragment()
        )
    }

    private fun startItemListFragment() {
        findNavController().navigate(
            AddItemFragmentDirections.actionAddItemFragmentToItemListFragment(
                navigationArguments.type
            )
        )
    }

    private fun setActionBarTitle(type: Type) {
        val id = navigationArguments.id
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            resources.getString(
                if (isAddFragment(id)) R.string.add else R.string.edit,
                type.name.lowercase().replaceFirstChar { it.uppercase() })
    }

    private fun closeSoftKeyboard() {
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        toggleDeleteButtonVisibility(false)
        closeSoftKeyboard()
        _binding = null
    }

}
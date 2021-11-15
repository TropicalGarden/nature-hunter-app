package com.example.naturehunter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.naturehunter.databinding.FragmentItemViewBinding

class ItemViewFragment : Fragment() {

    private var _binding: FragmentItemViewBinding? = null
    private val binding get() = _binding!!

    private val navigationArguments: ItemViewFragmentArgs by navArgs()

    private var isBarShown = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        toggleBars()
        disableActionBarAnimation()
        _binding = FragmentItemViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pictureUri = navigationArguments.uri
        binding.itemViewFragment = this
        binding.viewItemImageView.setImageURI(pictureUri.toUri())
    }

    fun startAddItemFragment() {
        findNavController().navigate(
            ItemViewFragmentDirections.actionItemViewFragmentToAddItemFragment(
                navigationArguments.id,
                navigationArguments.type,
                navigationArguments.uri
            )
        )
    }

    fun toggleBars() {
        if (isBarShown) hideBars() else showBars()
    }

    private fun showBars() {
        val window = requireActivity().window
        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).show(WindowInsetsCompat.Type.statusBars())
        toggleActionBar(!isBarShown)
        isBarShown = true
    }

    private fun hideBars() {
        val window = requireActivity().window
        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).hide(WindowInsetsCompat.Type.statusBars())
        toggleActionBar(!isBarShown)
        isBarShown = false
    }

    private fun toggleActionBar(showActionBar: Boolean) {
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        if (showActionBar) {
            supportActionBar?.show()
        } else {
            supportActionBar?.hide()
        }
    }

    private fun disableActionBarAnimation() {
        (requireActivity() as AppCompatActivity).supportActionBar?.setShowHideAnimationEnabled(false)
    }

    override fun onStop() {
        super.onStop()
        toggleActionBar(true)
        if (!isBarShown) {
            showBars()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
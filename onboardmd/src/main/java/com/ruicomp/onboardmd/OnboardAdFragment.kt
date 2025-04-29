package com.ruicomp.onboardmd

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ruicomp.onboardmd.databinding.FragmentOnboardAdBinding

class OnboardAdFragment : Fragment() {

    private var _binding: FragmentOnboardAdBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardAdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFullScreenNativeAd()
    }

    private fun loadFullScreenNativeAd() {
        Log.d("OnboardAdFragment", "Ad loading placeholder - Implement ad SDK logic here.")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): OnboardAdFragment {
            return OnboardAdFragment()
        }
    }
}

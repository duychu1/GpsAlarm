package com.ruicomp.onboardmd

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.ruicomp.onboardmd.databinding.FragmentOnboardContentBinding

class OnboardContentFragment : Fragment() {

    private var _binding: FragmentOnboardContentBinding? = null
    private val binding get() = _binding!!

    // 1. Define the interface
    interface OnEventClickListener {
        fun onButtonClicked(nPage: Int) // Define method(s) the Activity must implement
        // fun onOtherEvent()
    }

    // 2. Hold a reference to the listener
    private var listener: OnEventClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 3. Check if the hosting Activity implements the interface
        if (context is OnEventClickListener) {
            listener = context
        } else {
            Log.w("OnboardContentFragment", "Hosting Activity does not implement OnEventClickListener")
            // Optional: Throw an exception if the Activity doesn't implement it
             throw RuntimeException("$context must implement OnEventClickListener")
            // Or just log a warning
        }
    }

    override fun onDetach() {
        super.onDetach()
        // 5. Release the listener reference to prevent memory leaks
        listener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = requireArguments().getInt(ARG_ID_INT)
        val imageRes = requireArguments().getInt(ARG_IMAGE_RES)
        val titleRes = requireArguments().getInt(ARG_TITLE_RES)
        val descriptionRes = requireArguments().getInt(ARG_DESC_RES)

        binding.imageViewOnboard.setImageResource(imageRes)
        binding.textViewTitle.setText(titleRes)
        binding.textViewDescription.setText(descriptionRes)

        binding.llindicator.getChildAt(id).isSelected = true
        if (id == 2) {
            binding.buttonNext.setText(R.string.get_started)
        } else {
            binding.buttonNext.setText(R.string.next)
        }

        binding.buttonNext.setOnClickListener {
            listener?.onButtonClicked(id*2)
        }

//        adjustLayoutConstraints()
    }

//    private fun adjustLayoutConstraints() {
//        val constraintLayout = binding.root
//        val constraintSet = ConstraintSet()
//        constraintSet.clone(constraintLayout)
//
//        if (binding.adContainer.visibility == View.GONE) {
//            // Remove bottom constraint of llOnboardContent with llindicator
//            constraintSet.clear(R.id.llOnboardContent, ConstraintSet.BOTTOM)
//            // Set height of llOnboardContent to 60% of the parent
//            constraintSet.constrainPercentHeight(R.id.llOnboardContent, 0.6f)
//        } else {
//            // Restore bottom constraint of llOnboardContent with llindicator
//            constraintSet.connect(
//                R.id.llOnboardContent,
//                ConstraintSet.BOTTOM,
//                R.id.llindicator,
//                ConstraintSet.TOP
//            )
//            // Remove height percentage constraint
//            constraintSet.constrainDefaultHeight(
//                R.id.llOnboardContent,
//                ConstraintSet.MATCH_CONSTRAINT
//            )
//        }
//
//        constraintSet.applyTo(constraintLayout)
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_ID_INT = "id_int"
        private const val ARG_IMAGE_RES = "image_res"
        private const val ARG_TITLE_RES = "title_res"
        private const val ARG_DESC_RES = "desc_res"

        fun newInstance(
            id: Int,
            @DrawableRes imageRes: Int,
            @StringRes titleRes: Int,
            @StringRes descriptionRes: Int,
        ): OnboardContentFragment {
            val fragment = OnboardContentFragment()
            val args = Bundle().apply {
                putInt(ARG_IMAGE_RES, imageRes)
                putInt(ARG_TITLE_RES, titleRes)
                putInt(ARG_DESC_RES, descriptionRes)
                putInt(ARG_ID_INT, id)
            }
            fragment.arguments = args
            return fragment
        }
    }
}

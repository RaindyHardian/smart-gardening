package com.pistachio.smartgardening.ui.detail.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pistachio.smartgardening.data.entity.PlantEntity
import com.pistachio.smartgardening.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {
    companion object{
        const val EXTRA_BUNDLE = "extra_bundle"
    }
    private lateinit var binding: FragmentDetailBinding

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View{
        binding = FragmentDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val plant : PlantEntity = arguments?.getParcelable<PlantEntity>(EXTRA_BUNDLE) as PlantEntity
        binding.txtBestPlacing.text = plant.bestPlacing
        binding.txtDescription.text = plant.description
        binding.txtHabitat.text = plant.habitat
        binding.txtMarketPrice.text = plant.marketPrice
        binding.txtWatering.text = plant.watering
        binding.txtFertilizer.text = plant.fertilizer
        binding.txtTips.text = plant.tips
    }
}
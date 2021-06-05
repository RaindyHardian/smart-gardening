package com.pistachio.smartgardening.ui.detail.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.pistachio.smartgardening.R
import com.pistachio.smartgardening.data.entity.DiseaseEntity
import com.pistachio.smartgardening.data.entity.PlantEntity
import com.pistachio.smartgardening.databinding.DiseaseDetailContentBinding
import com.pistachio.smartgardening.databinding.FragmentDiseaseBinding
import com.pistachio.smartgardening.ui.adapter.DiseaseAdapter
import kotlinx.android.synthetic.main.fragment_detail.*


class DiseaseFragment : Fragment() {
    companion object{
        const val EXTRA_BUNDLE = "extra_bundle"
    }
    private lateinit var binding: FragmentDiseaseBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDiseaseBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val plant : PlantEntity = arguments?.getParcelable<PlantEntity>(EXTRA_BUNDLE) as PlantEntity
        val diseaseAdapter = DiseaseAdapter()
        diseaseAdapter.setList(plant.disease)
        diseaseAdapter.notifyDataSetChanged()

        with(binding.rvDisease) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = diseaseAdapter
        }

        diseaseAdapter.setOnItemClickCallback(object : DiseaseAdapter.OnItemClickCallback{
            override fun onItemClicked(data: DiseaseEntity) {
                val dialogBuilder = AlertDialog.Builder(context)

                val inflater: LayoutInflater = LayoutInflater.from(context)
                val detailContentBinding = DiseaseDetailContentBinding.inflate(inflater)
                with(detailContentBinding){
                    txtDiseaseName.text = data.name
                    txtDiseaseLatinName.text = data.latinName
                    txtDescription.text = data.description
                    txtTypeOfDisease.text = data.type
                    txtHabitat.text = data.habitat
                    txtCause.text = data.cause
                    txtBestTreatment.text = data.resolve

                    if(data.cause == null){
                        tvCause.visibility = View.GONE
                        txtCause.visibility = View.GONE
                    }
                    if(data.habitat == null){
                        tvHabitat.visibility = View.GONE
                        txtHabitat.visibility = View.GONE
                    }
                }
                dialogBuilder.setView(detailContentBinding.root)
                dialogBuilder.create().show()
            }
        })
    }
}
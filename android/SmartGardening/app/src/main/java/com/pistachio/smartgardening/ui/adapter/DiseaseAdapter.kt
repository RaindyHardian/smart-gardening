package com.pistachio.smartgardening.ui.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pistachio.smartgardening.data.entity.DiseaseEntity
import com.pistachio.smartgardening.databinding.ItemListDiseaseBinding
import java.util.*


class DiseaseAdapter : RecyclerView.Adapter<DiseaseAdapter.DiseaseViewHolder>() {
    private var listDiseases = ArrayList<DiseaseEntity>()
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setList(disease: List<DiseaseEntity>?) {
        if (disease == null) return
        this.listDiseases.clear()
        this.listDiseases.addAll(disease)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiseaseViewHolder {
        val itemsDiseaseBinding = ItemListDiseaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiseaseViewHolder(itemsDiseaseBinding)
    }

    override fun onBindViewHolder(holder: DiseaseViewHolder, position: Int) {
        val plant = listDiseases[position]
        holder.bind(plant)
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listDiseases[holder.bindingAdapterPosition]) }
    }

    override fun getItemCount(): Int = listDiseases.size


    class DiseaseViewHolder(private val binding: ItemListDiseaseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(diseaseEntity: DiseaseEntity) {
            with(binding) {
                txtDiseaseName.text = diseaseEntity.name
                txtDiseaseLatinName.text = diseaseEntity.latinName
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: DiseaseEntity)
    }
}
package com.pistachio.smartgardening.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pistachio.smartgardening.data.PlantEntity
import com.pistachio.smartgardening.databinding.ItemListPlantBinding
import com.pistachio.smartgardening.ui.detail.DetailActivity
import java.io.File
import java.util.ArrayList

class PlantAdapter : RecyclerView.Adapter<PlantAdapter.PlantViewHolder>() {
    private var listPlants = ArrayList<PlantEntity>()

    fun setPlants(plants: List<PlantEntity>?) {
        if (plants == null) return
        this.listPlants.clear()
        this.listPlants.addAll(plants)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val itemsAcademyBinding = ItemListPlantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlantViewHolder(itemsAcademyBinding)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = listPlants[position]
        holder.bind(plant)
    }

    override fun getItemCount(): Int = listPlants.size


    class PlantViewHolder(private val binding: ItemListPlantBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(plantEntity: PlantEntity) {
            with(binding) {
                txtPlantName.text = plantEntity.name
                val imgFile = File(plantEntity.imagePath)
                if (imgFile.exists()) {
                    Glide.with(itemView.context).load(imgFile).into(imgItemPhoto)
                }

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.EXTRA_PLANT, plantEntity)
                    intent.putExtra(DetailActivity.EXTRA_STATUS, 201)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }
}
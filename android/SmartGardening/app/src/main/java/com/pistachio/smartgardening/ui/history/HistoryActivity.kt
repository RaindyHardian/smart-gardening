package com.pistachio.smartgardening.ui.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pistachio.smartgardening.R
import com.pistachio.smartgardening.databinding.ActivityHistoryBinding
import com.pistachio.smartgardening.ui.adapter.PlantAdapter
import com.pistachio.smartgardening.utils.ViewModelFactory

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var viewModel: HistoryViewModel
    private var listEmpty = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "History"

        val factory = ViewModelFactory.getInstance(this.application)
        viewModel = ViewModelProvider(this, factory)[HistoryViewModel::class.java]

        val plantAdapter = PlantAdapter()
        viewModel.getAllPlants().observe(this, { plants ->
            if (plants != null) {
                plantAdapter.setPlants(plants)
                plantAdapter.notifyDataSetChanged()
            }
            if(plants.isEmpty()){
                listEmpty = true
                binding.txtNoHistory.visibility = View.VISIBLE
            }else{
                listEmpty = false
                binding.txtNoHistory.visibility = View.INVISIBLE
            }
        })

        with(binding.rvPlants) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = plantAdapter
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_history, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.btn_clear_history ){
            if(!listEmpty){
                val builder = AlertDialog.Builder(this)
                with(builder){
                    setTitle(R.string.clear_history)
                    setMessage(R.string.clear_history_message)
                    setIcon(android.R.drawable.ic_menu_delete)
                    setNegativeButton("Cancel", null)
                    setPositiveButton("Yes"){dialogInterface, which ->
                        viewModel.clearAll()
                    }
                    show()
                }
            }else{
                val builder = AlertDialog.Builder(this)
                with(builder) {
                    setTitle(R.string.no_history)
                    setMessage("Unable to delete")
                    setIcon(android.R.drawable.ic_dialog_alert)
                    setNegativeButton("OK", null)
                    show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
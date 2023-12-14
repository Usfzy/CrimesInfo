package com.usfzy.criminalintent.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.usfzy.criminalintent.databinding.ListItemCrimeBinding
import com.usfzy.criminalintent.model.Crime

class CrimeHolder(private val binding: ListItemCrimeBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(crime: Crime) {
        binding.apply {
            crimeTitle.text = crime.title
            crimeDate.text = crime.date.toString()

            root.setOnClickListener {
                Toast.makeText(binding.root.context, "${crime.title} Clicked!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}

class CrimeListAdapter(private val crimes: List<Crime>) : RecyclerView.Adapter<CrimeHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemCrimeBinding.inflate(inflater, parent, false)

        return CrimeHolder(binding)
    }

    override fun getItemCount(): Int {
        return crimes.size
    }

    override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
        holder.bind(crimes[position])
    }

}
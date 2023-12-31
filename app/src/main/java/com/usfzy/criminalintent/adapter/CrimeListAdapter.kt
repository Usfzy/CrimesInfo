package com.usfzy.criminalintent.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.usfzy.criminalintent.databinding.ListItemCrimeBinding
import com.usfzy.criminalintent.model.Crime
import java.util.UUID

class CrimeHolder(private val binding: ListItemCrimeBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        crime: Crime,
        onCrimeClicked: (crimeId: UUID) -> Unit,
        onDeleteCrimeClicked: (crime: Crime) -> Unit
    ) {
        binding.apply {
            crimeTitle.text = crime.title
            crimeDate.text = crime.date.toString()

            root.setOnClickListener {
                onCrimeClicked(crime.id)
            }

            deleteCrime.setOnClickListener {
                onDeleteCrimeClicked(crime)
            }

            crimePhoto.visibility = if (crime.isSolved) View.VISIBLE else View.GONE
        }
    }
}

class CrimeListAdapter(
    private val crimes: List<Crime>,
    private val onCrimeClicked: (crimeId: UUID) -> Unit,
    private val onDeleteCrimeClicked: (crime: Crime) -> Unit,
) :
    RecyclerView.Adapter<CrimeHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemCrimeBinding.inflate(inflater, parent, false)

        return CrimeHolder(binding)
    }

    override fun getItemCount(): Int {
        return crimes.size
    }

    override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
        holder.bind(crimes[position], onCrimeClicked, onDeleteCrimeClicked)
    }

}
package de.tobiasmoss.apier.ui.favorites_view

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import de.tobiasmoss.apier.R
import de.tobiasmoss.apier.backend.FavoritesRepository
import de.tobiasmoss.apier.data.TextEntry

class MyFavedItemRecyclerViewAdapter(
    initValues: List<TextEntry>,
    val favoritesRepository: FavoritesRepository
): RecyclerView.Adapter<MyFavedItemRecyclerViewAdapter.ViewHolder>() {

    private val values: MutableList<TextEntry> = initValues.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.id.toString()
        holder.contentView.text = item.content
        holder.checkBox.isChecked = item.faved
        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            item.faved = isChecked
            if(isChecked){
                favoritesRepository.saveFavorite(
                    TextEntry(
                        item.id,
                        item.content,
                        item.details,
                        item.faved
                    )
                )
            }
            else{
                favoritesRepository.removeFavorite(item.id)
            }
        }
    }

    override fun getItemCount(): Int = values.size

    fun setEntryList(newData : List<TextEntry>){
        values.clear()
        values.addAll(newData)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idView: TextView = view.findViewById(R.id.item_number)
        val contentView: TextView = view.findViewById(R.id.content)
        val checkBox: CheckBox = view.findViewById(R.id.checkBox)

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }
}
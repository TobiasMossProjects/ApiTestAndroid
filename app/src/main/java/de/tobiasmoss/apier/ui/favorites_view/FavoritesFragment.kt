package de.tobiasmoss.apier.ui.favorites_view

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import de.tobiasmoss.apier.R
import de.tobiasmoss.apier.backend.FavoritesRepository
import javax.inject.Inject

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var columnCount = 1
    private lateinit var myViewModel: FavoritesViewModel
    private lateinit var myAdapter: MyFavedItemRecyclerViewAdapter
    @Inject lateinit var favoritesRepository: FavoritesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

        // Set the viewmodel
        myViewModel = ViewModelProvider(this).get(FavoritesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                myAdapter = MyFavedItemRecyclerViewAdapter(myViewModel.favorites.value!!, favoritesRepository)
                adapter = myAdapter
            }
            myViewModel.favorites.observe(viewLifecycleOwner, { newList ->
                myAdapter.setEntryList(newList)
            })
        }
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            FavoritesFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
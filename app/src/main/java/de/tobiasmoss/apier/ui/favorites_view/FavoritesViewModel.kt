package de.tobiasmoss.apier.ui.favorites_view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tobiasmoss.apier.backend.FavoritesRepository
import de.tobiasmoss.apier.data.TextEntry
import javax.inject.Inject
import kotlin.concurrent.thread

@HiltViewModel
class FavoritesViewModel @Inject constructor(private val favoritesRepository: FavoritesRepository): ViewModel() {

    private val _favorites: MutableLiveData<List<TextEntry>> = MutableLiveData(favoritesRepository.getCachedFavorites().toList())
    val favorites: LiveData<List<TextEntry>>
        get() = _favorites

    init {
        Log.i("ApiViewModel", "ApiViewModel instantiated")
        fetchBackendData()
    }

    // Backend Things

    private fun fetchBackendData(){
        thread {
            _favorites.postValue(favoritesRepository.getFavorites())
        }
    }
}
package de.tobiasmoss.apier.backend

import android.util.Log
import androidx.lifecycle.LiveData
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import de.tobiasmoss.apier.data.TextEntry
import java.security.KeyStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepository @Inject constructor() {

    private val endpoint = "textentry"

    private val favorites = mutableListOf<TextEntry>()
    @Inject lateinit var backendController: BackendController

    fun saveFavorite(entry: TextEntry){
        val result = backendController.create(endpoint, entry.content, entry.details)
        Log.i("Backend", "Persist Favorite over Backend")
        var tempEntry = entry
        tempEntry.id = result
        favorites.add(tempEntry)
    }

    fun removeFavorite(foreignDetail: String){
        favorites.removeAll{it.details == foreignDetail}
        val result = backendController.queryByDetail(endpoint, foreignDetail)
        if (result == null) {
            return
        }
        backendController.delete(endpoint, result.id)
        Log.i("Backend", "Remove Favorite from Backend")
    }

    fun removeFavorite(id: Int){
        favorites.removeAll{it.id == id}
        backendController.delete(endpoint, id)
        //TODO Backend API Call
        Log.i("Backend", "Remove Favorite from Backend")
    }

    fun isFaved(foreignDetail: String) : Boolean{
        val localFaved = favorites.any{it.details == foreignDetail}

        if (!localFaved) {
            val result = backendController.queryByDetail(endpoint, foreignDetail)
            return result != null
        }else{
            return true
        }
    }

    fun getFavorites(): List<TextEntry> {
        val result = backendController.queryAll(endpoint)
        favorites.clear()
        favorites.addAll(result)
        return favorites
    }

    fun getCachedFavorites(): List<TextEntry> {
        return favorites
    }
}

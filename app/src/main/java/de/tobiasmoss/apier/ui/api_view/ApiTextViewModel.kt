package de.tobiasmoss.apier.ui.api_view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tobiasmoss.apier.backend.FavoritesRepository
import de.tobiasmoss.apier.data.TextEntry
import okhttp3.*
import okio.IOException
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ApiTextViewModel @Inject constructor(private val favoritesRepository: FavoritesRepository) : ViewModel() {

    private val _textEntries = MutableLiveData(listOf<TextEntry>())
    val textBluEntries: LiveData<List<TextEntry>>
        get() = _textEntries

    private val client = OkHttpClient()
    private val defaultApiURL: String = "https://icanhazdadjoke.com/search"

    init {
        Log.i("ApiViewModel", "ApiViewModel instantiated")
        fetchApiData(defaultApiURL, "", 1)
    }

    // External API Things

    private fun fetchApiData(url: String, searchTerm: String, page: Int){
        val request = Request.Builder()
            .url("$url?term=$searchTerm")
            .addHeader("Accept", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = apiFail(call, e)
            override fun onResponse(call: Call, response: Response) = apiCallback(call, response)
        })
    }

    private fun apiCallback(call: Call, response: Response){
        val responseData = response.body?.string()
        println(responseData)
        if(!response.isSuccessful){
            Log.i("ApiViewModel", "Unexpected Return Code")
            return
        }
        if(responseData == null){
            Log.i("ApiViewModel", "Unexpected Null Response Body")
            return
        }
        //TODO: Make JSON Parsing API independent

        val apiResponseJson: JSONObject = JSONObject(responseData)
        val jokes = apiResponseJson.getJSONArray("results")
        val tempEntryList = mutableListOf<TextEntry>()
        for(i in 0 until jokes.length()) {
            tempEntryList.add(
                TextEntry(
                    i,
                    jokes.getJSONObject(i).getString("joke"),
                    jokes.getJSONObject(i).getString("id"),
                    favoritesRepository.isFaved(jokes.getJSONObject(i).getString("id"))
                )
            )
        }
        _textEntries.postValue(tempEntryList)
        Log.i("ApiViewModel", "Done Parsing API Response")
    }

    private fun apiFail(call: Call, e: IOException){
        Log.i("ApiViewModel", "API Request Failed: \n $e")
        _textEntries.postValue(listOf(
            TextEntry(
                0,
                "API Fetch Failed",
                "Details coming soon",
                false
            )
        ))
    }

}
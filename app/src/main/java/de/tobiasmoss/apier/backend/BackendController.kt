package de.tobiasmoss.apier.backend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.tobiasmoss.apier.data.TextEntry
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject


class BackendController @Inject constructor(){

    val internalAPI = "http://10.0.2.2:8080"
    private val client = OkHttpClient()

    fun create(endpoint: String, content: String, details: String) : Int {
        val mediaTypeJson = "application/json; charset=utf-8".toMediaType()
        val jsonString = "{\"content\":\"${content.replace("\"","\\\"")}\",\"details\":\"$details\"}"

        Log.i("BackendController", "Json Request: $jsonString")

        val request = Request.Builder()
            .url("$internalAPI/$endpoint/create")
            .addHeader("Accept", "application/json")
            .post(jsonString.toRequestBody(mediaTypeJson))
            .build()

        Log.i("BackendController", "Create Request: $request ")

        val response = client.newCall(request).execute()
        val responseData = response.body?.string()

        Log.i("BackendController", "Create Response: $response")

        if(!response.isSuccessful){
            Log.i("BackendController", "Unexpected Return Code")
            return -1
        }
        if(responseData == null){
            Log.i("BackendController", "Unexpected Null Response Body")
            return -1
        }
        return JSONObject(responseData).getInt("id")
    }

    fun delete(endpoint: String, id: Int){
        val request = Request.Builder()
            .url("$internalAPI/$endpoint/$id")
            .addHeader("Accept", "application/json")
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i("BackendController", "API Request Failed: \n $e")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                if(!response.isSuccessful){
                    Log.i("BackendController", "Unexpected Return Code")
                    return
                }
                if(responseData == null){
                    Log.i("BackendController", "Unexpected Null Response Body")
                    return
                }
            }
        })
    }

    fun queryAll(endpoint: String): List<TextEntry>{
        val request = Request.Builder()
            .url("$internalAPI/$endpoint/all")
            .addHeader("Accept", "application/json")
            .get()
            .build()

        val result = MutableLiveData(emptyList<TextEntry>())

        val response = client.newCall(request).execute()
        val responseData = response.body?.string()

        if(!response.isSuccessful){
            Log.i("BackendController", "Unexpected Return Code")
            return emptyList()
        }
        if(responseData == null){
            Log.i("BackendController", "Unexpected Null Response Body")
            return emptyList()
        }

        val apiResponseJson: JSONObject = JSONObject(responseData)
        val entries = apiResponseJson.getJSONArray("content")
        val tempEntryList = mutableListOf<TextEntry>()
        for(i in 0 until entries.length()) {
            tempEntryList.add(
                TextEntry(
                    entries.getJSONObject(i).getInt("id"),
                    entries.getJSONObject(i).getString("content"),
                    entries.getJSONObject(i).getString("details"),
                    true
                )
            )
        }
        Log.i("BackendController", "Done Parsing API Response")
        return tempEntryList
    }

    fun queryByDetail(endpoint: String, detail: String): TextEntry?{
        val request = Request.Builder()
            .url("$internalAPI/$endpoint/detail?detail=$detail")
            .addHeader("Accept", "application/json")
            .get()
            .build()

        val result = MutableLiveData<TextEntry>()

        val response = client.newCall(request).execute()
        val responseData = response.body?.string()
        if(!response.isSuccessful){
            Log.i("BackendController", "Unexpected Return Code")
            return null
        }
        if(responseData == null){
            Log.i("BackendController", "Unexpected Null Response Body")
            return null
        }

        val entries: JSONArray = JSONArray(responseData)

        if(entries.length() == 0){
            return null
        }
        else{
            return TextEntry(
                entries.getJSONObject(0).getInt("id"),
                entries.getJSONObject(0).getString("content"),
                entries.getJSONObject(0).getString("details"),
                true
            )
        }
    }
}
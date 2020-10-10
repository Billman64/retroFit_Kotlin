package com.github.billman64.retrofit10102020

/**
 *  @DESC: Retrofit Kotlin demo.
 *  Based on tutorial at: https://www.youtube.com/watch?v=rAk1j2CmPJs&t=189s
**/

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.github.billman64.retrofit10102020.api.ApiRequests
import com.github.billman64.retrofit10102020.api.CatJson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://cat-fact.herokuapp.com"

class MainActivity : AppCompatActivity() {
    private var TAG = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getCurrentData()

        layout_generate_new_fact.setOnClickListener(){
            getCurrentData()
        }
    }

    private fun getCurrentData(){
        // UI
        tv_textView.visibility = View.INVISIBLE
        tv_timeStamp.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE

        // Retrofit builder setup
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRequests::class.java)

        // Coroutine - run API call on a worker thread
        GlobalScope.launch(Dispatchers.IO){

            // Get response from the API
            val response: Response<CatJson> = api.getCatFacts().awaitResponse()

            // Process response
            if(response.isSuccessful){
                val data: CatJson = response.body()!!
                Log.d(TAG, data.text)

                withContext(Dispatchers.Main){
                    // UI
                    tv_textView.visibility = View.VISIBLE
                    tv_timeStamp.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE

                    // output data
                    tv_textView.text = data.text
                    tv_timeStamp.text = data.createdAt


                }
            }   // TODO: handling for no response, such as no Internet access


        }
    }

}

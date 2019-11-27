package com.arnav.retrofitcoroutines

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arnav.retrofitcoroutines.UsersResponse.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val r = Retrofit.Builder()
            .baseUrl("https://reqres.in/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = r.create(ReqResAPI::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val res1 = async { api.getUsers(1) }
                Log.d("RetCor", "first page done ${System.currentTimeMillis()}")
                val res2 = async { api.getUsers(2) }
                Log.d("RetCor", "second page done ${System.currentTimeMillis()}")

                val res = awaitAll(res1, res2)
                Log.d("RetCor", "both pages done ${System.currentTimeMillis()}")

                val users = ArrayList<User>()
                res[0].body()?.users?.let { users.addAll(it) }
                res[1].body()?.users?.let { users.addAll(it) }

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Request Success", Toast.LENGTH_SHORT).show()

                    printUsers(users)
                }




            } catch (e: Exception) {
                GlobalScope.launch(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Request Success", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun printUsers(users: List<User>) {
        lvPeople.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            android.R.id.text1,
            users.map { it.firstName }
        )
    }
}

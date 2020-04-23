package com.example.testi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.testi.ui.parking.ParkingFragment
import com.example.testi.ui.restaurant.RestaurantFragment
import com.example.testi.ui.settings.SettingsFragment
import kotlinx.android.synthetic.main.fragment_settings.*
import java.text.SimpleDateFormat
import java.util.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.internal.wait
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL

@Suppress("DEPRECATED_IDENTITY_EQUALS")

//Mainactivity contains definitons, onclicklisteners and asynctasks to fetch API data
class MainActivity : AppCompatActivity() {

    lateinit var token: String
    val fragmentManager = supportFragmentManager
    lateinit var btn: Button

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toParking = findViewById<Button>(R.id.navigation_parking)
        val toRestaurant = findViewById<Button>(R.id.navigation_restaurant)
        val toSettings = findViewById<Button>(R.id.navigation_settings)

        //Endpoint urls for their respective parking areas
        val parkingURLP5 = "https://mycampus-server.karage.fi/api/common/parking/status/P5"
        val parkingURLP10 = "https://mycampus-server.karage.fi/api/common/parking/status/P10"
        val parkingURLP10TOP = "https://mycampus-server.karage.fi/api/common/parking/status/P10TOP"

        val parkingURLS = setOf(
            parkingURLP5,
            parkingURLP10,
            parkingURLP10TOP
        )

        //Set of restaurant queue endpoints
        //https://mycampus-server.karage.fi/api/common/restaurant/Midpoint root url
        val restaurantURLS = setOf(
            "https://mycampus-server.karage.fi/api/common/restaurant/Midpoint/queue/1",
            "https://mycampus-server.karage.fi/api/common/restaurant/Midpoint/queue/2",
            "https://mycampus-server.karage.fi/api/common/restaurant/Midpoint/queue/3",
            "https://mycampus-server.karage.fi/api/common/restaurant/Midpoint/queue/4",
            "https://mycampus-server.karage.fi/api/common/restaurant/Midpoint/queue/5",
            "https://mycampus-server.karage.fi/api/common/restaurant/Midpoint/queue/6",
            "https://mycampus-server.karage.fi/api/common/restaurant/Midpoint/queue/7",
            "https://mycampus-server.karage.fi/api/common/restaurant/Midpoint/queue/8"
        )


        val midpoint =
            "https://mycampus-server.karage.fi/api/common/restaurant/Midpoint?select=fill_percent"


        fetchParkingData().execute(parkingURLS)


        val myPreference = MyPreferences(this)
        var loginCount = myPreference.getLoginCount()
        var jwt = myPreference.getJwt()
        if (jwt != null) {
            loginCount++
        }
        myPreference.setLoginCount(loginCount)
        //set login count to textview if desired
        print(loginCount)


        //Navigation on touch listener. Pressing the navigation buttons will fetch the data to the associated fragment


        toParking.setOnClickListener {
            val fragmentTransaction = fragmentManager.beginTransaction()
            val fragmentparking = ParkingFragment()
            fragmentTransaction.replace(R.id.hoster_frag, fragmentparking)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

            fetchParkingData().execute(parkingURLS)
        }
        toRestaurant.setOnClickListener {
            val fragmentTransaction = fragmentManager.beginTransaction()
            val fragmentRestaurant = RestaurantFragment()
            fragmentTransaction.replace(R.id.hoster_frag,  fragmentRestaurant)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

            fetchRestaurantData().execute(restaurantURLS)
            fetchRestaurantFillRate().execute(midpoint)
        }
        toSettings.setOnClickListener {
            val fragmentTransaction = fragmentManager.beginTransaction()
            val fragmentSettings = SettingsFragment()
            fragmentTransaction.replace(R.id.hoster_frag, fragmentSettings)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

            /*btn = findViewById<Button>(R.id.logoutbutton)

            btn.setOnClickListener {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                jwt = null
                myPreference.destroyJwt()
            }
            val user = myPreference.getUser().toString()!!
            loggedas.text = user!!*/
        }

        supportActionBar?.hide()
        actionBar?.hide()

        if (loginCount < 3) {
            Toast.makeText(
                applicationContext,
                "Double tap navigation bar section to get current data",
                Toast.LENGTH_LONG
            ).show()
        }


    }



    //Function to return restaurant queue time based on the API estimate value.
    fun queueTime(value: Int): String {

        val time1 = "0-30s"
        val time2 = "1min"
        val time3 = "1min30s"
        val time4 = "2min"
        val time5 = "+2min"

        var finaltime = ""

        when (value) {
            1 -> finaltime = time1
            2 -> finaltime = time2
            3 -> finaltime = time3
            4 -> finaltime = time4
            5 -> finaltime = time5
            else -> {
                print("You should never get here unless queue times returned by API are increased")
                Toast.makeText(
                    applicationContext,
                    "You should not get this message unless queue timevalue returned by API is increased",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        return finaltime
    }


    inner class fetchRestaurantData() : AsyncTask<Set<String>, Void, Set<String>>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        // Creates a set of JSON responses for the 8 different endpoint URLS and populates the queue data based on index of the set
        override fun doInBackground(vararg params: Set<String>): Set<String>? {
            Log.d("restaurantTask", "working...")

            token = MyPreferences.getInstance(applicationContext).getJwt().toString()
            val client = OkHttpClient()
            var responseSet = setOf<String>()

            //GET method
            //Params[0] is the setOf<String> that is given to doInBackground
            params[0].forEach {
                var currentURL = it
                val url = URL(it)
                val request = Request.Builder()
                    .addHeader("Authorization", "$token")
                    .url(url)
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body!!.string()

                responseSet += responseBody  //accumulate all endpoint responses to one entity

            }

            //Response
            println("Response Body: " + { responseSet })
            return responseSet  //response sent to onPostExecute
        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: Set<String>?) {

            super.onPostExecute(result)  //result value is responseSet

           try {
               Log.d("restaurant", "postexecuting")
               //For each index of the responseSet, populate fragment_restaurant.xml based on the current index when looping trough all 8
               for (index: Int in result!!.indices) {

                   val q = result.elementAt(index)
                   val jsonObj = JSONObject(q)
                   val timeq = jsonObj.getString("queue_time").toInt()
                   val pplq = jsonObj.getString("ppl_counter").toInt()

                   when (index) {
                       0 -> {

                           findViewById<TextView>(R.id.q1percent).setText(queueTime(timeq))
                           findViewById<ProgressBar>(R.id.q1progress).setProgress(
                               (timeq * 17),
                               true
                           )
                       }

                       1 -> {

                           findViewById<TextView>(R.id.q2percent).setText(queueTime(timeq))
                           findViewById<ProgressBar>(R.id.q2progress).setProgress(
                               (timeq * 17),
                               true
                           )
                       }
                       2 -> {

                           findViewById<TextView>(R.id.q3percent).setText(queueTime(timeq))
                           findViewById<ProgressBar>(R.id.q3progress).setProgress(
                               17,
                               true
                           )
                       }
                       3 -> {

                           findViewById<TextView>(R.id.q4percent).setText(queueTime(timeq))
                           findViewById<ProgressBar>(R.id.q4progress).setProgress(
                               (timeq * 17),
                               true
                           )
                       }
                       4 -> {

                           findViewById<TextView>(R.id.q5percent).setText(queueTime(timeq))
                           findViewById<ProgressBar>(R.id.q5progress).setProgress(
                               (timeq * 17),
                               true
                           )
                       }
                       5 -> {

                           findViewById<TextView>(R.id.q6percent).setText(queueTime(timeq))
                           findViewById<ProgressBar>(R.id.q6progress).setProgress(
                               (timeq * 17),
                               true
                           )
                       }
                       6 -> {

                           findViewById<TextView>(R.id.q7percent).setText(queueTime(timeq))
                           findViewById<ProgressBar>(R.id.q7progress).setProgress(
                               (timeq * 17),
                               true
                           )
                       }
                       7 -> {

                           findViewById<TextView>(R.id.q8percent).setText(queueTime(timeq))
                           findViewById<ProgressBar>(R.id.q8progress).setProgress(
                               (timeq * 17),
                               true
                           )
                       }
                   }
               }
           } catch (e: Exception) {
               print(e.toString())
               //Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_LONG ).show()
            }
        }
    }

    //much like previous asyncTask but simpler
    inner class fetchParkingData() : AsyncTask<Set<String>, Void, Set<String>>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        // GET method for fetching data from API
        // p5 capacity 650, p10 1025, p10top 205
        override fun doInBackground(vararg params: Set<String>): Set<String>? {
            Log.d("BGTASK", "alive?")
            token = MyPreferences.getInstance(applicationContext).getJwt().toString()
            val client = OkHttpClient()
            var responseSet = setOf<String>()

            //GET method
            //Params[0] is the setOf<String> that is given to doInBackground
            params[0].forEach {
                //var currentURL = it
                val url = URL(it)
                val request = Request.Builder()
                    .addHeader("Authorization", "$token")
                    .url(url)
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body!!.string()

                responseSet += responseBody  //accumulate all endpoint responses to one entity

            }

            //Response
            println("Response Body: " + { responseSet })
            return responseSet  //response sent to onPostExecute
        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: Set<String>?) {

            super.onPostExecute(result)

            try {
                Log.d("BGTASK", "postexecuting")
                for (index: Int in result!!.indices) {

                    var q = result?.elementAt(index)
                    var jsonObj = JSONObject(q)
                    val percent = jsonObj.getString("percent")
                    print(percent)

                    when (index) {
                        0 -> {

                            findViewById<ProgressBar>(R.id.p5progress).setProgress(percent.toInt(), true)
                            findViewById<TextView>(R.id.p5textprogress).text = "$percent%"
                        }

                        1 -> {

                            findViewById<ProgressBar>(R.id.p10progress).setProgress(percent.toInt(), true)
                            findViewById<TextView>(R.id.p10textprogress).text = "$percent%"
                        }
                        2 -> {

                            findViewById<ProgressBar>(R.id.p10insideprogress).setProgress(percent.toInt(), true)
                            findViewById<TextView>(R.id.p10insidetextprogress).text = "$percent%"
                        }
                    }
                }
            } catch (e: Exception) {
                print(e.toString())
                /*Toast.makeText(
                    applicationContext,
                    "Api not returning parking data",
                    Toast.LENGTH_LONG
                ).show()*/
            }
        }
    }

    inner class fetchRestaurantFillRate() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        // GET method for fetching data from API
        override fun doInBackground(vararg params: String): String? {
            var currentURL = params[0]
            val client = OkHttpClient()
            val url = URL("${params[0]}")
            token = MyPreferences.getInstance(applicationContext).getJwt().toString()

            val request = Request.Builder()
                .addHeader("Authorization", "$token")
                .url(url)
                .get()
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body!!.string()
            //Response
            println("Response Body: " + responseBody)
            return responseBody
        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: String?) {

            super.onPostExecute(result)
            //SUCCESS RESPONSE:
            //{count: 20, percent: 3, capacity: 650, parkingId: 1}

            try {
                val jsonObj = JSONObject(result)
                val percent = jsonObj.getString("fill_percent")
                print(percent)
                findViewById<ProgressBar>(R.id.restaurantUsageProgress).setProgress(
                    percent.toInt(),
                    true
                )
                findViewById<TextView>(R.id.restaurantUsageTextProgress).text =
                    "$percent % full"

            } catch (e: Exception) {
            }
        }
    }
}


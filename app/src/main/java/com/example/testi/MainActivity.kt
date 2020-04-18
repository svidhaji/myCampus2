package com.example.testi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URL

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class MainActivity : AppCompatActivity() {

    lateinit var token: String
    lateinit var currentURL: String
    private lateinit var tv: TextView
    private lateinit var p: ProgressBar

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setActionBar(toolbar)*/

        //Endpoint urls for their respective parking areas
        val parkingURLP5 = "https://mycampus-server.karage.fi/api/common/parking/status/P5"
        val parkingURLP10 = "https://mycampus-server.karage.fi/api/common/parking/status/P10"
        val parkingURLP10TOP = "https://mycampus-server.karage.fi/api/common/parking/status/P10TOP"

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


        fetchParkingData().execute(parkingURLP5)
        fetchParkingData().execute(parkingURLP10)
        fetchParkingData().execute(parkingURLP10TOP)


        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)

        val myPreference = MyPreferences(this)
        var loginCount = myPreference.getLoginCount()
        val jwt = myPreference.getJwt()
        if (jwt != null) {
            loginCount++
        }
        myPreference.setLoginCount(loginCount)
        //set login count to textview if desired
        print(loginCount)


        //Navigation on touch listener. Pressing the navigation buttons will fetch the data to the associated fragment
        /* navView.setOnNavigationItemSelectedListener {

            if (it.itemId === R.id.navigation_parking) {


            } else if (it.itemId === R.id.navigation_restaurant) {

                fetchRestaurantFillRate().execute(midpoint)
                fetchRestaurantData().execute(restaurantURLS)

            } else if (it.itemId === R.id.navigation_settings) {

                val textView: TextView? = findViewById(R.id.launchcount) as? TextView
                textView?.text = "launchcount: " + loginCount!!.toString()

            }
            return@setOnNavigationItemSelectedListener true
        }*/
        navView.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.navigation_parking -> {
                    //Do stuff
                    fetchParkingData().execute(parkingURLP5)
                    fetchParkingData().execute(parkingURLP10)
                    fetchParkingData().execute(parkingURLP10TOP)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_restaurant -> {

                    fetchRestaurantFillRate().execute(midpoint)
                    fetchRestaurantData().execute(restaurantURLS)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_settings -> {
                    //Do stuff
                    val textView: TextView? = findViewById(R.id.launchcount) as? TextView
                    textView?.text = "launchcount: " + loginCount!!.toString()

                    val btn = findViewById<Button>(R.id.logButton)

                    btn.setOnClickListener {
                        println("clicked logout button")
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }


        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_parking, R.id.navigation_restaurant, R.id.navigation_settings
            )
        )
        setupWithNavController(
            androidx.appcompat.widget.Toolbar(applicationContext),
            navController,
            appBarConfiguration
        )
        navView.setupWithNavController(navController)

    }


    // function to populate parking data in correct views of fragment_parking.xml
    @SuppressLint("SetTextI18n")
    fun setParkingProgress(percent: String) {
        val valuetopass: Int = percent.toInt()
        if (currentURL.equals("https://mycampus-server.karage.fi/api/common/parking/status/P5")) {
            findViewById<ProgressBar>(R.id.p5progress).setProgress(valuetopass, true)
            findViewById<TextView>(R.id.p5textprogress).text = "$valuetopass%"
        }
        if (currentURL.equals("https://mycampus-server.karage.fi/api/common/parking/status/P10")) {
            findViewById<ProgressBar>(R.id.p10progress).setProgress(valuetopass, true)
            findViewById<TextView>(R.id.p10textprogress).text = "$valuetopass%"
        }
        if (currentURL.equals("https://mycampus-server.karage.fi/api/common/parking/status/P10TOP")) {
            findViewById<ProgressBar>(R.id.p10insideprogress).setProgress(valuetopass, true)
            findViewById<TextView>(R.id.p10insidetextprogress).text = "$valuetopass%"
        } else {
            print("You should never get here")
            Log.d("setParking", "error with setParking")
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
            var i = 0

            //GET method
            //Params[0] is the setOf<String> that is given to doInBackground
            params[0].forEach {
                currentURL = it
                val url = URL(it)
                val request = Request.Builder()
                    .addHeader("Authorization", "$token")
                    .url(url)
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body!!.string()

                responseSet += responseBody  //accumulate all endpoint responses to one entity
                i++
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
                               5,
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
               Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_LONG ).show()
            }
        }
    }

    //much like previous asyncTask but simpler
    inner class fetchParkingData() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        // GET method for fetching data from API
        // p5 capacity 650, p10 1025, p10top 205
        override fun doInBackground(vararg params: String): String? {
            Log.d("BGTASK", "alive?")
            currentURL = params[0]
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

            try {
                Log.d("BGTASK", "postexecuting")
                val jsonObj = JSONObject(result)
                val percent = jsonObj.getString("percent")
                print(percent)
                //populate fragment_parking.xml with this function
                setParkingProgress(percent)
            } catch (e: Exception) {
            }
        }
    }

    inner class fetchRestaurantFillRate() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        // GET method for fetching data from API
        override fun doInBackground(vararg params: String): String? {
            currentURL = params[0]
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


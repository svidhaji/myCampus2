package com.example.testi.ui.restaurant

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.testi.R
import com.example.testi.MainActivity
import com.example.testi.MyPreferences

class RestaurantFragment : Fragment() {

    private lateinit var restaurantViewModel: RestaurantViewModel
    lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {
        restaurantViewModel =
            ViewModelProviders.of(this).get(RestaurantViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_restaurant, container, false)
        return root

    }

/*
    fun datafetch() {
        val m = MainActivity()
        val myPreference = MyPreferences(m)
        var jwt = myPreference.getJwt()
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

        m.fetchRestaurantFillRate().execute(midpoint)
        m.fetchRestaurantData().execute(restaurantURLS)

    }*/


}

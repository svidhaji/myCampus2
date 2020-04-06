package com.example.testi.ui.restaurant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.testi.R

class RestaurantFragment : Fragment() {

    private lateinit var restaurantViewModel: RestaurantViewModel

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
}

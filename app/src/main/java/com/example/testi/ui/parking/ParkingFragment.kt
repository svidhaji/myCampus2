
package com.example.testi.ui.parking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.testi.R


class ParkingFragment : Fragment() {

    private lateinit var parkingViewModel: ParkingViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {
        parkingViewModel =
            ViewModelProviders.of(this).get(ParkingViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_parking, container, false)
        return root

    }
}
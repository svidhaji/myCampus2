package com.example.testi.ui.parking

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.testi.R


class ParkingFragment : Fragment() {

    private lateinit var parkingViewModel: ParkingViewModel
    private var vstup: TextView? = null
    private var savedState: Bundle? = null

    /*override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            //Restore the fragment's state here
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val f = FragmentActivity()
        val s = f.supportFragmentManager
        s.putFragment(outState, "Parking", ParkingFragment())

    }*/

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


/*
    override fun onDestroyView() {
        super.onDestroyView()
        savedState = saveState() */
/* vstup defined here for sure *//*

        vstup = null
    }

    private fun saveState(): Bundle { */
/* called either from onDestroyView() or onSaveInstanceState() *//*

        val state = Bundle()
        state.putCharSequence(vstup.getText().toString())
        return state
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        */
/* If onDestroyView() is called first, we can use the previously savedState but we can't call saveState() anymore *//*
 */
/* If onSaveInstanceState() is called first, we don't have savedState, so we need to call saveState() *//*
 */
/* => (?:) operator inevitable! *//*
outState.putBundle(
            Application.,
            if (savedState != null) savedState else saveState()
        )
    }
}

*/

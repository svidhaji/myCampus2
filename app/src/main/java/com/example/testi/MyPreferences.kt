package com.example.testi

import android.content.Context

class MyPreferences (cntx : Context) {


    companion object {
        val PREFERENCE_NAME = "SharedPreference1"
        val PREFERENCE_LOGIN_COUNT = "LoginCount"
        val JWT = "JsonWebToken"
        val USER = ""
        private var mInstance: MyPreferences? = null
        @Synchronized
        fun getInstance(cntx: Context): MyPreferences {
            if (mInstance == null) {
                mInstance = MyPreferences(cntx)
            }
            return mInstance as MyPreferences
        }
    }


    val preference = cntx.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    val logged: Boolean
        get() {
            val sharedPreferences = preference
            return sharedPreferences.getString(JWT,null ) != null
        }

    fun getLoginCount () : Int {
        return  preference.getInt(PREFERENCE_LOGIN_COUNT, 0)
    }

    fun getJwt () : String? {
        return preference.getString(JWT, null).toString()
    }

    fun getUser () : String? {
        return preference.getString(JWT, null).toString()
    }

    fun destroyJwt () {
        val editor = preference.edit()
        editor.putString(JWT, null)
        editor.apply()
    }

    fun setLoginCount(count:Int) {
        val editor = preference.edit()
        editor.putInt(PREFERENCE_LOGIN_COUNT, count)
        editor.apply()
    }

    fun saveJwt(token:String) {
        val editor = preference.edit()
        editor.putString(JWT,token)
        editor.apply()
    }

    fun saveUser(email:String) {
        val editor = preference.edit()
        editor.putString(USER,email)
        editor.apply()
    }

}

package com.hdd.empowerpro.utils

import android.view.View

object ExtensionFunction {
    fun View.hide(){
        this.visibility= View.GONE
    }
    fun View.show(){
        this.visibility= View.VISIBLE
    }
    fun View.invisible(){
        this.visibility= View.INVISIBLE
    }
}
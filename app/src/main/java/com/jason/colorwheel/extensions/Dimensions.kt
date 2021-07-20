package com.jason.colorwheel.extensions

import android.content.res.Resources

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

val Int.sp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

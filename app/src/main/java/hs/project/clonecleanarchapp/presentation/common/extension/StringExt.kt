package hs.project.clonecleanarchapp.presentation.common.extension

import android.util.Patterns


fun String.isEmail() : Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}
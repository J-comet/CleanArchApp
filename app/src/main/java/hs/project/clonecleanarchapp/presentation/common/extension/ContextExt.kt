package hs.project.clonecleanarchapp.presentation.common.extension

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast

fun Context.showToast(msg : String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.showGenericAlertDialog(msg: String) {
    AlertDialog.Builder(this).apply {
        setMessage(msg)
        setPositiveButton("OK") { d, _ ->
            d.cancel()
        }
    }.show()
}
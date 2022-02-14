package com.candra.projectlimbah

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import java.text.SimpleDateFormat
import java.util.*

object Help {

    fun helpDialog(context: Context,inputanPertama: String,inputanKedua: String){
        val builder = AlertDialog.Builder(context,R.style.AlertDialogTheme)
        val view = LayoutInflater.from(context).inflate(
            R.layout.help_message_dialog,
            null
        )
        builder.setView(view)
        view.findViewById<MaterialTextView>(R.id.isiTeks).text = inputanPertama
        view.findViewById<MaterialTextView>(R.id.isiTeksPeringatan).text = inputanKedua

        val alertDialog = builder.create()

        view.findViewById<ImageButton>(R.id.closeBtn).setOnClickListener {
            alertDialog.dismiss()
        }

        if (alertDialog.window != null){
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }

        alertDialog.show()
    }

    fun makeToast(context: Context,message: String){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }


    @SuppressLint("SimpleDateFormat")
    fun setTimePickerDialog(view: TextInputEditText, context:Context){
        view.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY,hour)
                calendar.set(Calendar.MINUTE,minute)
                view.setText(SimpleDateFormat("HH:mm").format(calendar.time))
            }
            TimePickerDialog(context,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(
                Calendar.MINUTE),true).show()
        }
    }

}
package com.candra.projectlimbah

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import com.candra.projectlimbah.databinding.ActivityMainBinding
import com.google.android.material.textview.MaterialTextView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigateToActivity()
        setToolbar()
        showPermissions()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.help){
            Help.makeToast(this,"Anda mengklik fitur bantuan")
            showDialog()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setToolbar(){
        supportActionBar?.title = resources.getString(R.string.app_name)
        supportActionBar?.subtitle = "Perhitungan Debit Air"
    }

    private fun navigateToActivity(){

        binding.apply {
            cardViewUNote.setOnClickListener {
                startActivity(Intent(this@MainActivity, UnoteActivity::class.java))
            }

            cardViewVNote.setOnClickListener {
                startActivity(Intent(this@MainActivity,VnoteActivity::class.java))
            }
        }

    }

    private fun showDialog(){
        val builder = AlertDialog.Builder(this,R.style.AlertDialogTheme)
        val view = LayoutInflater.from(this).inflate(
            R.layout.help_message_dialog,
            null
        )
        builder.setView(view)
        view.findViewById<MaterialTextView>(R.id.isiTeks).setText(resources.getString(R.string.home_note))
        view.findViewById<MaterialTextView>(R.id.isiTeksPeringatan).visibility = View.GONE
        view.findViewById<MaterialTextView>(R.id.materialTextView2).visibility = View.GONE

        val alertDialog = builder.create()

        view.findViewById<ImageButton>(R.id.closeBtn).setOnClickListener {
            alertDialog.dismiss()
        }

        if (alertDialog.window != null){
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }

        alertDialog.show()

    }

    private fun showPermissions(){
        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0?.areAllPermissionsGranted() == true){
                        Help.makeToast(this@MainActivity,"Permission diizinkan")
                    }else{
                        showDialogPermissionGranted()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }

            }).onSameThread().check()

    }

    private fun showDialogPermissionGranted() {
        AlertDialog.Builder(this)
            .setMessage("Aplikasi ini membutuhkan fitur perizinan dari sistem anda. Silahkan cek setting anda " +
                    "untuk mengaktifkan fitur tersebut")
            .setTitle("Warning")
            .setIcon(R.mipmap.ic_launcher_foreground2)
            .setPositiveButton("Pergi Ke Setting"){_,_ ->

                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package",packageName,null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                    Log.d("MainActvity", "showDialogPermissionGranted: " + e.message.toString())
                }

            }

            .setNegativeButton("CANCEL"){dialog,_ ->
                dialog.dismiss()
                exitProcess(0)
            }.show()
    }

}
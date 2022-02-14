package com.candra.projectlimbah

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.widget.doOnTextChanged
import com.candra.projectlimbah.databinding.VNoteActivityBinding
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.HorizontalAlignment
import com.itextpdf.layout.property.TextAlignment
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

@Suppress("DEPRECATION")
class VnoteActivity : AppCompatActivity()
{
    private lateinit var binding: VNoteActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = VNoteActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showText(false)

        showTextCalculator(false)

        binding.apply {
            hasilQ1.setOnClickListener {
                validasiData()
            }

            binding.sharePDf.visibility = View.VISIBLE
            binding.sharePDf.isEnabled = false
        }


        setDataInputOclock()

        actionHasil()

        setToolbar()

        setComponents()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.help -> {
                Toast.makeText(this,"Anda mengklik fitur bantuan",Toast.LENGTH_SHORT).show()
                Help.helpDialog(this,resources.getString(R.string.fitur_help_v_note),"Disini tidak ada kesalahan")
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setToolbar(){
        supportActionBar?.title = resources.getString(R.string.app_name)
        supportActionBar?.subtitle = "VNotch"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    @SuppressLint("SetTextI18n")
    private fun validasiData(){

        try {

            val conversiB = binding.textBConversi.editText?.text.toString()
            val conversiD = binding.dConversiMeter.editText?.text.toString()
            val conversiH = binding.hM.editText?.text.toString()

            val b_cm = binding.bCm.editText?.text?.trim().toString()
            val d_cm = binding.dCm.editText?.text?.trim().toString()
            val h_cm = binding.hCm.editText?.text?.trim().toString()

            if (conversiB.isEmpty() && b_cm.isEmpty()){
                binding.bCm.error = "Inputan B kosong"
                binding.bCm.isErrorEnabled = true
                binding.bCm.setErrorTextColor(ColorStateList.valueOf(Color.RED))
                Help.makeToast(this@VnoteActivity,"Inputan B kosong")
            }else if (conversiD.isEmpty() && d_cm.isEmpty()){
                Help.makeToast(this@VnoteActivity,"Inputan D kosong")
                binding.dCm.error = "Inputan D kosong"
                binding.dCm.isErrorEnabled = true
                binding.dCm.setErrorTextColor(ColorStateList.valueOf(Color.RED))
            }else if (conversiH.isEmpty() && h_cm.isEmpty()){
                Help.makeToast(this@VnoteActivity,"Inputan h kosong")
                binding.hCm.error = "Inputan h kosong"
                binding.hCm.isErrorEnabled = true
                binding.hCm.setErrorTextColor(ColorStateList.valueOf(Color.RED))
            }else{
                binding.bCm.isErrorEnabled = false
                binding.dCm.isErrorEnabled = false
                binding.hCm.isErrorEnabled = false
                val d: Double = (8.4 + 12 / conversiD.toDouble().pow(0.5))
                val a: Double = (conversiH.toDouble()/conversiB.toDouble() - 0.09).pow(2)
                val k: Double = 81.2 + 0.24/conversiH.toDouble() + d * a
                val q: Double = k * conversiH.toDouble().pow(2.5)
                val q1: Double = q * 60
                val QString = q.toString()
                val Q1String = q1.toString()

                showTextCalculator(true)

                binding.textHasilQ1.text =  "Q1(menit) $QString"
                binding.textHasilQ2.text = "Q2(jam) $Q1String"

            }
        }catch (e: NumberFormatException){
            Help.helpDialog(this@VnoteActivity,"Ini adalah fitur peringatan","KESALAHAN: ${e.message.toString()}" )
        }

    }

    private fun setComponents(){
        binding.apply {
            editBCm.doOnTextChanged { text, start, before, count ->
                val textLowerCase = text?.trim().toString().lowercase()
                val foo = textLowerCase.substring(0)
                try{
                    when {
                        textLowerCase.isEmpty() -> {
                            Toast.makeText(this@VnoteActivity,"Inputan C masih kosong", Toast.LENGTH_SHORT).show()
                        }
                        foo == "." -> {
                            Help.helpDialog(this@VnoteActivity,"Ini adalah fitur peringatan",
                                "Jangan menggunakan . sebagai karakter awal")
                        }
                        else -> {
                            val resultBMeter: Double = textLowerCase.toDouble() / 100
                            textEditBConversiMeter.setText(resultBMeter.toString())
//                            textEditBConversiMeter.setText(DecimalFormat("##.#####").format(resultBMeter))
                        }
                    }
                }catch (e: NumberFormatException){
                    Help.helpDialog(this@VnoteActivity,"Peringatan untuk user","Periksa data input kembali")
                    Help.makeToast(this@VnoteActivity,"Kesalahan " + e.message.toString())
                }
            }

            editDCm.doOnTextChanged { text, start, before, count ->
                val textLowerCaseD = text?.trim().toString().lowercase()
                val foo = textLowerCaseD.substring(0)
                try {
                    when {
                        textLowerCaseD.isEmpty() -> {
                            Toast.makeText(this@VnoteActivity,"Inputan D masih kosong", Toast.LENGTH_SHORT).show()
                        }
                        foo == "." -> {
                            Help.helpDialog(this@VnoteActivity,"Ini adalah fitur peringatan",resources.getString(R.string.fitur_validasi))
                        }
                        else -> {
                            val resultDMeter: Double = textLowerCaseD.toDouble() / 100
                            editDMeter.setText(resultDMeter.toString())
//                            editDMeter.setText(DecimalFormat("##.#####").format(resultDMeter)).toString()
                        }
                    }
                }catch (e: NumberFormatException){
                    Help.helpDialog(this@VnoteActivity,"Peringatan untuk user","Periksa data input kembali")
                    Help.makeToast(this@VnoteActivity,"Kesalahan " + e.message.toString())
                }
            }

            editHCm.doOnTextChanged { text, start, before, count ->

                val textLowerCase1 = text?.trim().toString().lowercase()
                val foo = textLowerCase1.substring(0)
                try {
                    when {
                        textLowerCase1.isEmpty() -> {
                            Toast.makeText(this@VnoteActivity,"Inputan h masih kosong", Toast.LENGTH_SHORT).show()
                        }
                        foo == "." -> {
                            Help.helpDialog(this@VnoteActivity,"Ini adalah fitur peringatan",resources.getString(R.string.fitur_validasi))
                        }
                        else -> {
                            val resultMeter: Double = textLowerCase1.toDouble() / 100
                            editHM.setText(resultMeter.toString())
//                            editHM.setText(DecimalFormat("##.#####").format(resultMeter))
                        }
                    }
                }catch (e: NumberFormatException){
                    Help.helpDialog(this@VnoteActivity,"Peringatan untuk user","Periksa data input kembali")
                    Help.makeToast(this@VnoteActivity,"Kesalahan " + e.message.toString())
                }
            }

        }
    }

    private fun setDataInputOclock(){
        Help.setTimePickerDialog(binding.idTimeOne,this)
        Help.setTimePickerDialog(binding.idTimeTwo,this)
        Help.setTimePickerDialog(binding.idTimeThree,this)
        Help.setTimePickerDialog(binding.idTimeFour,this)
        Help.setTimePickerDialog(binding.idTimeFive,this)
        Help.setTimePickerDialog(binding.idTimeSix,this)
        Help.setTimePickerDialog(binding.idTimeSeven,this)
        Help.setTimePickerDialog(binding.idTimeEight,this)
    }

    private fun actionHasil(){

        binding.sharePDf.setOnClickListener {
            sharePdf()
        }


        binding.cetakPdfShiftPagi.setOnClickListener {

            val textInputJam1 = binding.userInputJam1.editText?.text.toString()
            val textInputQ1 = binding.userInputQ1.editText?.text.toString()
            val textInputJam2 = binding.userInputJam2.editText?.text.toString()
            val textInputQ2 = binding.userInputQ2.editText?.text.toString()
            val textInputJam3 = binding.userInputJam3.editText?.text.toString()
            val textInputQ3 = binding.userInputQ3.editText?.text.toString()
            val textInputJam4 = binding.userInputJam4.editText?.text.toString()
            val textInputQ4 = binding.userInputQ4.editText?.text.toString()
            val textInputJam5 = binding.userInputJam5.editText?.text.toString()
            val textInputQ5 = binding.userInputQ5.editText?.text.toString()
            val textInputJam6 = binding.userInputJam6.editText?.text.toString()
            val textInputQ6 = binding.userInputQ6.editText?.text.toString()
            val textInputJam7 = binding.userInputJam7.editText?.text.toString()
            val textInputQ7 = binding.userInputQ7.editText?.text.toString()
            val textInputJam8 = binding.userInputJam8.editText?.text.toString()
            val textInputQ8 = binding.userInputQ8.editText?.text.toString()
            val textDataRata = binding.totalRataQ.text.toString()
            val rataQ = binding.totalQ.text.toString()


            try {
                if (textInputJam1.isEmpty() && textInputQ1.isEmpty()) {
                    Help.makeToast(this@VnoteActivity, "Inputan Jam dan Q pertama kosong")
                } else if (textInputJam2.isEmpty() && textInputQ2.isEmpty()) {
                    Help.makeToast(this@VnoteActivity, "Inputan Jam dan Q kedua kosong")
                } else if (textInputJam3.isEmpty() && textInputQ3.isEmpty()) {
                    Help.makeToast(this@VnoteActivity, "Inputan Jam dan Q ketiga kosong")
                } else if (textInputJam4.isEmpty() && textInputQ4.isEmpty()) {
                    Help.makeToast(this@VnoteActivity, "Inputan Jam dan Q keempat kosong")
                } else if (textInputJam5.isEmpty() && textInputQ5.isEmpty()) {
                    Help.makeToast(this@VnoteActivity, "Inputan Jam dan Q kelima kosong")
                } else if (textInputJam6.isEmpty() && textInputQ6.isEmpty()) {
                    Help.makeToast(this@VnoteActivity, "Inputan Jam dan Q keenam kosong")
                } else if (textInputJam7.isEmpty() && textInputQ7.isEmpty()) {
                    Help.makeToast(this@VnoteActivity, "Inputan Jam dan Q ketujuh kosong")
                } else if (textInputJam8.isEmpty() && textInputQ8.isEmpty()) {
                    Help.makeToast(this@VnoteActivity, "Inputan Jam dan Q kedelapan kosong")
                }else{
                    cetakPdf(textInputQ1,textInputJam1,textInputQ2,textInputJam2,textInputQ3,textInputJam3,textInputQ4,textInputJam4,
                        textInputQ5,textInputJam5,textInputQ6,textInputJam6,textInputQ7,textInputJam7,textInputQ8,textInputJam8,rataQ,textDataRata)
                    clearData()
                }
            }catch (e: NumberFormatException){
                Help.helpDialog(this@VnoteActivity,"Harap lihat inputan anda.. Jangan ada inputan kosong"
                    ,"KESALAHAN: Terjadi Error Pada System " + e.message.toString())
            }
        }

        binding.buttonHasilShiftPagi.setOnClickListener {

            val textInputJam1 = binding.userInputJam1.editText?.text.toString()
            val textInputQ1 = binding.userInputQ1.editText?.text.toString()
            val textInputJam2 = binding.userInputJam2.editText?.text.toString()
            val textInputQ2 = binding.userInputQ2.editText?.text.toString()
            val textInputJam3 = binding.userInputJam3.editText?.text.toString()
            val textInputQ3 = binding.userInputQ3.editText?.text.toString()
            val textInputJam4 = binding.userInputJam4.editText?.text.toString()
            val textInputQ4 = binding.userInputQ4.editText?.text.toString()
            val textInputJam5 = binding.userInputJam5.editText?.text.toString()
            val textInputQ5 = binding.userInputQ5.editText?.text.toString()
            val textInputJam6 = binding.userInputJam6.editText?.text.toString()
            val textInputQ6 = binding.userInputQ6.editText?.text.toString()
            val textInputJam7 = binding.userInputJam7.editText?.text.toString()
            val textInputQ7 = binding.userInputQ7.editText?.text.toString()
            val textInputJam8 = binding.userInputJam8.editText?.text.toString()
            val textInputQ8 = binding.userInputQ8.editText?.text.toString()

            try{
                if (textInputJam1.isEmpty() && textInputQ1.isEmpty()){
                    Help.makeToast(this@VnoteActivity,"Inputan Jam dan Q pertama kosong")
                }else if (textInputJam2.isEmpty() && textInputQ2.isEmpty()){
                    Help.makeToast(this@VnoteActivity,"Inputan Jam dan Q kedua kosong")
                }else if (textInputJam3.isEmpty() && textInputQ3.isEmpty()){
                    Help.makeToast(this@VnoteActivity,"Inputan Jam dan Q ketiga kosong")
                }else if (textInputJam4.isEmpty() && textInputQ4.isEmpty()){
                    Help.makeToast(this@VnoteActivity,"Inputan Jam dan Q keempat kosong")
                }else if (textInputJam5.isEmpty() && textInputQ5.isEmpty()){
                    Help.makeToast(this@VnoteActivity,"Inputan Jam dan Q kelima kosong")
                }else if(textInputJam6.isEmpty() && textInputQ6.isEmpty()){
                    Help.makeToast(this@VnoteActivity,"Inputan Jam dan Q keenam kosong")
                }else if(textInputJam7.isEmpty() && textInputQ7.isEmpty()){
                    Help.makeToast(this@VnoteActivity,"Inputan Jam dan Q ketujuh kosong")
                }else if (textInputJam8.isEmpty() && textInputQ8.isEmpty()){
                    Help.makeToast(this@VnoteActivity,"Inputan Jam dan Q kedelapan kosong")
                }else{

                    val text1: Double = textInputQ1.toDouble()
                    val text2: Double = textInputQ2.toDouble()
                    val text3: Double = textInputQ3.toDouble()
                    val text4: Double = textInputQ4.toDouble()
                    val text5: Double = textInputQ5.toDouble()
                    val text6: Double = textInputQ6.toDouble()
                    val text7: Double = textInputQ7.toDouble()
                    val text8: Double = textInputQ8.toDouble()

                    val rataQ:Double = text1 + text2 + text3 + text4 + text5 + text6 + text7 + text8
                    val totalRataQ: Double = rataQ / 8

                    showText(true)

                    binding.totalQ.text = DecimalFormat("##.#####").format(rataQ).replace(",",".")
                    binding.totalRataQ.text = DecimalFormat("##.#####").format(totalRataQ).replace(",",".")


                }
            }catch (e: NumberFormatException){
                Help.helpDialog(this@VnoteActivity,"Harap lihat inputan anda.. Jangan ada inputan kosong"
                    ,"KESALAHAN: Terjadi Error Pada System " + e.message.toString())
                binding.totalQ.visibility = View.GONE
                binding.totalRataQ.visibility = View.GONE
            }
        }

        binding.clearButtonPagi.setOnClickListener {
            clearData()
            showText(false)
        }

        binding.cleanDataCalculator.setOnClickListener {
            clearDataCalculator()
        }
    }

    private fun clearData(){
        binding.userInputJam1.editText?.text?.clear()
        binding.userInputQ1.editText?.text?.clear()
        binding.userInputJam2.editText?.text?.clear()
        binding.userInputQ2.editText?.text?.clear()
        binding.userInputJam3.editText?.text?.clear()
        binding.userInputQ3.editText?.text?.clear()
        binding.userInputJam4.editText?.text?.clear()
        binding.userInputQ4.editText?.text?.clear()
        binding.userInputJam5.editText?.text?.clear()
        binding.userInputQ5.editText?.text?.clear()
        binding.userInputJam6.editText?.text?.clear()
        binding.userInputQ6.editText?.text?.clear()
        binding.userInputJam7.editText?.text?.clear()
        binding.userInputQ7.editText?.text?.clear()
        binding.userInputJam8.editText?.text?.clear()
        binding.userInputQ8.editText?.text?.clear()

        showText(false)

    }


    private fun clearDataCalculator(){
       binding.textBConversi.editText?.text?.clear()
       binding.dConversiMeter.editText?.text?.clear()
       binding.hM.editText?.text?.clear()

       binding.bCm.editText?.text?.clear()
       binding.dCm.editText?.text?.clear()
       binding.hCm.editText?.text?.clear()

        showTextCalculator(false)
    }

    private fun showTextCalculator(show: Boolean){
        if (show){
            binding.textHasilQ1.visibility = View.VISIBLE
            binding.textHasilQ2.visibility = View.VISIBLE
        }else{
            binding.textHasilQ1.visibility = View.GONE
            binding.textHasilQ2.visibility = View.GONE
        }
    }

    private fun showText(show: Boolean){
        if (show){
            binding.totalRataQ.visibility = View.VISIBLE
            binding.totalQ.visibility = View.VISIBLE
        }else{
            binding.totalRataQ.visibility = View.GONE
            binding.totalQ.visibility = View.GONE
        }
    }

    private fun sharePdf(){
        val simpelDateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(
            Date()
        )
        val tanggalDate: String = simpelDateFormat
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(path,"$tanggalDate debitAirVNote.pdf")

        val pdfUri: Uri?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            pdfUri = FileProvider.getUriForFile(this,this.packageName + ".provider",file)
        }else{
            pdfUri = Uri.fromFile(file)
        }

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "application/pdf"
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.putExtra(Intent.EXTRA_STREAM,pdfUri)
        shareIntent.setPackage("com.whatsapp")
        startActivity(shareIntent)

    }

    private fun cetakPdf(Qa: String,JamA: String,Qb: String,JamB: String,Qc: String,JamC: String,
    Qd: String,JamD: String,Qe: String,JamE: String,Qf: String,JamF: String,Qg: String,JamG: String,
    Qh: String,JamH: String,totalRata: String,finishTotalRata: String){

        // masukkan
        val path: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()

        val simpelDateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(Date())
        val tanggaDate: String = simpelDateFormat

        val file = File(path,"$tanggaDate debitAirVNote.pdf")
        val outputStrean = FileOutputStream(file)

        val pdfWriter: PdfWriter = PdfWriter(file)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)


        pdfDocument.defaultPageSize = PageSize.A4
        document.setMargins(0F,0F, 0F,0F)

       val textTitle = Paragraph("Data Debit Air Ipal V Note").setBold().setFontSize(24F).setTextAlignment(TextAlignment.CENTER)

       val describeTitle = Paragraph("Ini adalah pdf untuk Debit Air").setTextAlignment(TextAlignment.CENTER)


       val date = Paragraph(simpelDateFormat).setTextAlignment(TextAlignment.CENTER).setFontSize(12F)


        val width = floatArrayOf(
            150F,150F,150F
        )

        val table = Table(width)
        table.setHorizontalAlignment(HorizontalAlignment.CENTER)

        table.addCell(Cell().add(Paragraph("No")))
        table.addCell(Cell().add(Paragraph("Jam")))
        table.addCell(Cell().add(Paragraph("Q / Jam")))

        table.addCell(Cell().add(Paragraph("1")))
        table.addCell(Cell().add(Paragraph(JamA)))
        table.addCell(Cell().add(Paragraph(Qa)))

        table.addCell(Cell().add(Paragraph("2")))
        table.addCell(Cell().add(Paragraph(JamB)))
        table.addCell(Cell().add(Paragraph(Qb)))

        table.addCell(Cell().add(Paragraph("3")))
        table.addCell(Cell().add(Paragraph(JamC)))
        table.addCell(Cell().add(Paragraph(Qc)))

        table.addCell(Cell().add(Paragraph("4")))
        table.addCell(Cell().add(Paragraph(JamD)))
        table.addCell(Cell().add(Paragraph(Qd)))

        table.addCell(Cell().add(Paragraph("5")))
        table.addCell(Cell().add(Paragraph(JamE)))
        table.addCell(Cell().add(Paragraph(Qe)))

        table.addCell(Cell().add(Paragraph("6")))
        table.addCell(Cell().add(Paragraph(JamF)))
        table.addCell(Cell().add(Paragraph(Qf)))

        table.addCell(Cell().add(Paragraph("7")))
        table.addCell(Cell().add(Paragraph(JamG)))
        table.addCell(Cell().add(Paragraph(Qg)))

        table.addCell(Cell().add(Paragraph("8")))
        table.addCell(Cell().add(Paragraph(JamH)))
        table.addCell(Cell().add(Paragraph(Qh)))

        table.addCell(Cell().add(Paragraph("9")))
        table.addCell(Cell().add(Paragraph("Rata Rata Q2")))
        table.addCell(Cell().add(Paragraph(finishTotalRata)))

        table.addCell(Cell().add(Paragraph("10")))
        table.addCell(Cell().add(Paragraph("Total Rata Rata Q2")))
        table.addCell(Cell().add(Paragraph(totalRata)))

        document.add(textTitle)
        document.add(describeTitle)
        document.add(date)
        document.add(table)

        document.close()

        Help.helpDialog(this@VnoteActivity,"Silahkan cek di memory telepon \n lalu lihat folder download" +
                " setelah itu cari file $file ","File berada: $file")
        Help.makeToast(this@VnoteActivity,"$file")

        binding.sharePDf.isEnabled = true


    }

//    private fun sharePdf(){
//        val builder = StrictMode.VmPolicy.Builder()
//        StrictMode.setVmPolicy(builder.build())
//        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
//        val file = File(path,"debitAirVNote.pdf")
//        val uri = Uri.fromFile(file)
//        val shareIntent = Intent()
//        shareIntent.action = Intent.ACTION_SEND
//        shareIntent.type = "application/pdf"
//        shareIntent.putExtra(Intent.EXTRA_STREAM,uri)
//        shareIntent.setPackage("com.whatsapp")
//
//        startActivity(shareIntent)
//    }

//    @SuppressLint("ObsoleteSdkInt")
//    private fun sharePdf2(){
//        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"debitAirVNote.pdf")
//        if (file.exists()){
//            val uri = if (Build.VERSION.SDK_INT < 24) Uri.fromFile(file) else Uri.parse(file.path)
//            val share = Intent()
//            share.action = Intent.ACTION_SEND
//            share.type = "application/pdf"
//            share.putExtra(Intent.EXTRA_STREAM,uri)
//            share.setPackage("com.whatsapp")
//            startActivity(share)
//        }else{
//            Help.makeToast(this,"File tidak ditemukan")
//        }
//
//    }


}
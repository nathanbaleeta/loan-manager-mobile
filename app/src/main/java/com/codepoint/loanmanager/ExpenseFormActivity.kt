package com.codepoint.loanmanager

import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.widget.Button
import android.widget.EditText
import com.codepoint.loanmanager.models.Expense
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_expense_form.*
import java.lang.Integer.parseInt
import java.text.SimpleDateFormat
import java.util.Date
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ExpenseFormActivity : AppCompatActivity() {

    // Declare Views
    lateinit var txtParticulars: EditText
    lateinit var txtAmount: EditText
    lateinit var txtDate: EditText
    lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_form)

        //actionbar
        val actionbar = supportActionBar

        //set back button
        actionbar!!.setDisplayHomeAsUpEnabled(true)

        // Initialize Views
        txtParticulars = findViewById(R.id.txtParticulars)
        txtAmount = findViewById(R.id.txtAmount)
        txtDate = findViewById(R.id.txtDate)

        /***************** Year established Date picker ****************/
        var cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "yyyy-MM-dd" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            val date = sdf.format(cal.time)

            // Display Selected date in textbox
            txtDate.setText(date)

        }

        txtDate.setOnClickListener {
            DatePickerDialog(this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        /***************** Year established Date picker ****************/

        btnSave = findViewById(R.id.btnSaveExpense)

        // Attach a click listener to save button
        btnSave.setOnClickListener {
            saveExpense()
        }

    }

    private fun saveExpense() {
        val description = txtParticulars.text.toString().toUpperCase().trim()
        val expenseDate = txtDate.text.toString().trim()

        // Capture datetime when expense was created and store in created
        val sdf = SimpleDateFormat("dd/M/yyyy, hh:mm:ss")
        val created = sdf.format(Date())


        // Implement Number format exception in try catch blocks to avoid app crashing
        val amount: Int? = try {
            parseInt(txtAmount.text.toString())
        } catch (e: NumberFormatException) {
            null
        }


        // Validate registration form before saving to Firebase database
        if (description.isEmpty()) {
            txtParticulars.error = "Please enter a first name"
            return
        }  else if (amount == null) {
            txtAmount.error = "Please enter an amount in digits"
            return
        } else if (expenseDate.isEmpty()) {
            txtDate.error = "Please enter a date"
            return
        }
        else {
            // Instantiate new farmer using Farmer data class model
            val expense = Expense(description, amount, expenseDate, created)

            // Support offline data entry by enabling disk persistence
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
            val ref = FirebaseDatabase.getInstance().getReference("expenses")

            // Push the data to Fire base cloud data store
            ref.push().setValue(expense)

            // Clear registration form after saving farmer
            txtParticulars.setText("")
            txtAmount.setText("")
            txtDate.setText("")


            // Display response message after saving farmer
            Snackbar.make(scroll_layout, "Expense was successfully saved", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()

        }

    }
    // Back arrow click event to go back to the parent Activity
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

package com.example.bankinfo

import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.TextView
import com.android.volley.RequestQueue
import android.os.Bundle
import com.example.bankinfo.R
import com.android.volley.toolbox.Volley
import android.text.TextUtils
import android.widget.Toast
import android.annotation.SuppressLint
import android.view.View
import android.widget.Button
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import org.json.JSONException
import com.android.volley.VolleyError

class MainActivity : AppCompatActivity() {
    private var ifscCodeEdt: EditText? = null
    private var bankDetailsTV: TextView? = null
    var ifscCode: String? = null
    private var mRequestQueue: RequestQueue? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ifscCodeEdt = findViewById(R.id.idedtIfscCode)
        val getBankDetailsBtn = findViewById<Button>(R.id.idBtnGetBankDetails)
        bankDetailsTV = findViewById(R.id.idTVBankDetails)
        mRequestQueue = Volley.newRequestQueue(this@MainActivity)
        getBankDetailsBtn.setOnClickListener { v: View? ->
            ifscCode = ifscCodeEdt.getText().toString()
            if (TextUtils.isEmpty(ifscCode)) {
                Toast.makeText(
                    this@MainActivity,
                    "Please enter valid IFSC code",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                getDataFromIFSCCode(ifscCode!!)
            }
        }
    }

    private fun getDataFromIFSCCode(ifscCode: String) {
        mRequestQueue!!.cache.clear()
        val url = "http://api.techm.co.in/api/v1/ifsc/$ifscCode"
        val queue = Volley.newRequestQueue(this@MainActivity)
        @SuppressLint("SetTextI18n") val objectRequest =
            JsonObjectRequest(Request.Method.GET, url, null, { response: JSONObject ->
                try {
                    if (response.getString("status") == "failed") {
                        bankDetailsTV!!.text = "Invalid IFSC Code"
                    } else {
                        val dataObj = response.getJSONObject("data")
                        val state = dataObj.optString("STATE")
                        val bankName = dataObj.optString("BANK")
                        val branch = dataObj.optString("BRANCH")
                        val address = dataObj.optString("ADDRESS")
                        val contact = dataObj.optString("CONTACT")
                        val micrcode = dataObj.optString("MICRCODE")
                        val city = dataObj.optString("CITY")
                        bankDetailsTV!!.text =
                            "Bank Name : $bankName\nBranch : $branch\nAddress : $address\nMICR Code : $micrcode\nCity : $city\nState : $state\nContact : $contact"
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    bankDetailsTV!!.text = "Invalid IFSC Code"
                }
            }) { error: VolleyError? -> bankDetailsTV!!.text = "Invalid IFSC Code" }
        queue.add(objectRequest)
    }
}
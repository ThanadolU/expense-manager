package org.classapp.expensemanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val usernameTxt: EditText = findViewById(R.id.signUpUsername)
        val passwordTxt: EditText = findViewById(R.id.signUpPassword)
        val emailTxt: EditText = findViewById(R.id.signUpEmail)
        val phoneNumberTxt: EditText = findViewById(R.id.signUpPhoneNumber)

        var username:String; var password:String; var email:String; var phoneNumber: String

        // condition for checking username, password, email, and phone number.
//            if () {
//
//            } else {
//
//            }
    }
}
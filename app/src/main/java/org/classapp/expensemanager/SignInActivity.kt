package org.classapp.expensemanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val usernameTxt:EditText = findViewById(R.id.signInUsername)
        val passwordTxt:EditText = findViewById(R.id.signInPassword)
        val enterSignInBtn: Button = findViewById(R.id.signInBtn)

        var username:String; var password: String
        enterSignInBtn.setOnClickListener {
            username = usernameTxt.text.toString()
            password = passwordTxt.text.toString()

            // condition for checking username and password
//            if () {
//
//            } else {
//
//            }
        }
    }
}
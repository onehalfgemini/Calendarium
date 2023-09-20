package com.example.calendarium

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.calendarium.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast



class SignUpActivity : AppCompatActivity()
{

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnBackSignup.setOnClickListener{
            val startIntent = Intent(this, MainActivity::class.java)
            startActivity(startIntent)
        }

        binding.btnSubmit.setOnClickListener {

            val email = binding.etUserMail.text.toString()
            val password = binding. etPasswordFirst.text.toString()
            val confPassword = binding.etPasswordSecond.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confPassword.isNotEmpty()){

                if(password == confPassword)
                {
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                        if (it.isSuccessful)
                        {
                            val intent = Intent(this, LogInActivity::class.java)
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(this,"Password does not match" , Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }



        }
        binding.loginRedirectText.setOnClickListener {
            val loginIntent= Intent(this, LogInActivity::class.java)
            startActivity(loginIntent)
        }

    }
}
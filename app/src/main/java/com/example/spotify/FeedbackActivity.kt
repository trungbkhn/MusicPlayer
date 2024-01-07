package com.example.spotify

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spotify.databinding.ActivityFeedbackBinding
import java.util.Properties
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

private lateinit var binding: ActivityFeedbackBinding
class FeedbackActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Spotify)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Feedback"
        binding.sendMW.setOnClickListener{
            val feedbackMsg = binding.feedbackMsgMW.text.toString() + "\n" + binding.emailFeedback.text.toString()
            val subject = binding.topicMW.text.toString()
            val userName = "vuductrung2592000@gmail.com"
            val pass = "qaswzmqicqeiokgb"
            val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if(feedbackMsg.isNotEmpty() && subject.isNotEmpty()&&(cm.activeNetworkInfo?.isConnectedOrConnecting == true)){
                Thread{
                    try{
                        val properties = Properties()
                        properties["mail.smtp.auth"] = true
                        properties["mail.smtp.starttls.enable"] = true
                        properties["mail.smtp.host"] = "smtp.gmail.com"
                        properties["mail.smtp.port"] = "587"
                        val session = javax.mail.Session.getInstance(properties, object :javax.mail.Authenticator(){
                            override fun getPasswordAuthentication(): javax.mail.PasswordAuthentication {
                                return javax.mail.PasswordAuthentication(userName,pass)
                            }
                        })
                        val mail = MimeMessage(session)
                        mail.subject = subject
                        mail.setText(feedbackMsg)
                        mail.setFrom(InternetAddress(userName))
                        mail.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(userName))
                        Transport.send(mail)
                        Toast.makeText(this,"Thanks for your Feedback!",Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    catch (e: Exception) {
                        Toast.makeText(this,e.toString(), Toast.LENGTH_SHORT).show()}
                }.start()
            }
            else{
                Toast.makeText(this,"Oops... Something Wrong!",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
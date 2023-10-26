package com.example.emailapp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button sendEmail;
    EditText email, subject, message, cc, bcc;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.emailEdt);
        subject = findViewById(R.id.subjectEdt);
        message = findViewById(R.id.messageEdt);
        sendEmail = findViewById(R.id.sendButton);
        progressBar = findViewById(R.id.progressBar);
        cc = findViewById(R.id.ccEdt);
        bcc = findViewById(R.id.bccEdt);

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (email.getText().toString().isEmpty() && subject.getText().toString().isEmpty() && message.getText().toString().isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Invalid Details!", Toast.LENGTH_SHORT).show();
                    return;
                }
                new SendEmailTask().execute();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class SendEmailTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            String emailsend = email.getText().toString();
            String emailsubject = subject.getText().toString();
            String emailbody = message.getText().toString();

            String emailCc = cc.getText().toString();
            String emailBcc = bcc.getText().toString();

            // Set up the JavaMail properties
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            final String username = "yourgmail@gmail.com"; // Your Gmail address
            final String password = "your_app_password"; // Your app password

            // Create a session with the Gmail SMTP server
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            try {
                // Create a MimeMessage object
                MimeMessage mimeMessage = new MimeMessage(session);
                mimeMessage.setFrom(new InternetAddress(username));
                mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailsend));

                if (!emailCc.isEmpty()) {
                    mimeMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(emailCc));
                }
                if (!emailBcc.isEmpty()) {
                    mimeMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress(emailCc));
                }

                mimeMessage.setSubject(emailsubject);
                mimeMessage.setText(emailbody);

                // Send the message using the Transport class
                Transport.send(mimeMessage);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        subject.setText("");
                        message.setText("");
                        email.setText("");
                        cc.setText("");
                        bcc.setText("");
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Mail Sent Successfully!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Some Error Occurred!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}

package com.example.easyloan.util;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

public class MailMessages {
    public static String verifyEmail(String name){
        return
                        "<p>Hi "+ name+", </p>"+
                        "<p>Thank you for registering with us, Please follow the link below to complete your registration.</p>"+
                        "<p><a href=\"#\"><h4>Click this to verify your email</h4></a></p>";


    }
}

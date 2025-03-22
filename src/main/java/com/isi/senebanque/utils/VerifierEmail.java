package com.isi.senebanque.utils;

public class VerifierEmail {
    private static final String regex_email = "^[A-Za-z0-9+_.-]+@(.+)$";
    public static boolean estValide(String email) {
        return email.matches(regex_email);
    }
}

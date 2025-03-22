package com.isi.senebanque.services;

import java.util.Random;

public class GenererUsernameService {

        public static String genererUsername(String nom, String prenom) {
            String baseUsername = prenom.toLowerCase() + "." + nom.toLowerCase();
            Random random = new Random();
            int randomNumber = random.nextInt(1000);
            return baseUsername + randomNumber;
        }
}

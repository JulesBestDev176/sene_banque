package com.isi.senebanque.utils;

public class VerifierTelephone {
    private static final String[] debut = {"70", "75", "76", "77", "78"};

    public static boolean estValide(String telephone) {
        if (!telephone.startsWith("+221")) {
            return false;
        }
        String prefixe = telephone.substring(4, 6);
        for (String prefixeValide : debut) {
            if (prefixe.equals(prefixeValide)) {
                return true;
            }
        }
        return false;
    }
}

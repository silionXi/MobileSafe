package com.silion.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by silion on 2016/3/30.
 */
public class MD5Utils {

    public static String encode(String input) {
        MessageDigest digester = null;
        try {
            digester = MessageDigest.getInstance("MD5");
            byte[] digests = digester.digest(input.getBytes());
            StringBuffer sb = new StringBuffer();
            for (byte digest : digests) {
                int i = digest & 0xff;
                String hexString = Integer.toHexString(i);
                if (hexString.length() == 1) {
                    hexString = "0" + hexString;
                }
                sb.append(hexString);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}

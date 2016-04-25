package com.silion.mobilesafe.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by silion on 2016/3/21.
 */
public class StreamUtils {

    public static String inputStream2String1(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len = 0;
        byte[] buffer = new byte[1024];
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }

        String result = out.toString();

        in.close();
        out.close();

        return result;
    }

    public static String inputStream2String2(InputStream in) throws IOException {
        StringBuffer buffer = new StringBuffer();
        BufferedReader out = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = out.readLine()) != null) {
            buffer.append(line + "/n");
        }

        String result = buffer.toString();

        in.close();
        out.close();

        return result;
    }

    public static String inputStream2String3(InputStream in) throws IOException {
        StringBuffer buffer = new StringBuffer();
        byte[] bytes = new byte[1024];
        int len;
        while ((len = in.read(bytes)) != -1) {
            buffer.append(new String(bytes, 0, len));
        }

        String result = buffer.toString();

        in.close();

        return result;
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }
        if (!(out instanceof BufferedOutputStream)) {
            out = new BufferedOutputStream(out);
        }

        int len;
        byte[] buffer = new byte[2048];
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        out.flush();
    }
}

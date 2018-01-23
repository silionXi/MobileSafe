package com.silion.mobilesafe.utils;

import com.silion.mobilesafe.bean.AppInfo;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by silion on 2017/6/2.
 */

public class FileUtils {

    public static boolean runRootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            /**
             * su是root用户
             * 其他用户是
             * process = Runtime.getRuntime().exec("sh");
             */
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null)
                    os.close();
                process.destroy();
            } catch (Exception e) {
                // nothing
                e.printStackTrace();
            }
        }
        return true;
    }

    public static boolean backup(AppInfo appInfo, String dst) {
        copyFile(appInfo.getSourceDir(), dst + "/" + appInfo.getName() + ".apk");
        String dataDir = appInfo.getDataDir();
        copyDirectory(dataDir, dst + "/data");
        return false;
    }

    public static void copyDirectory(String src, String dest) {
        if (!src.contains("cache")) {
            runRootCommand("chmod 777 " + src);
            File srcFile = new File(src);
            File destFile = new File(dest);
            if (srcFile != null && srcFile.exists() && destFile != null) {
                if (srcFile.isDirectory()) {
                    if (!destFile.exists()) {
                        destFile.mkdirs();
                    }

                    String[] fileNameList = srcFile.list();

                    if (fileNameList != null && fileNameList.length > 0) {
                        for (String fileName : fileNameList) {
                            copyDirectory(srcFile + "/" + fileName, destFile + "/" + fileName);
                        }
                    }
                } else {
                    copyFile(src, dest);
                }
            }
        }
    }

    public static void copyFile(String src, String dest) {
        runRootCommand("chmod 777 " + src);
        File srcFile = new File(src);
        File destFile = new File(dest);
        if (srcFile != null && srcFile.exists() && srcFile.length() > 0 && destFile != null) {
            if (destFile.exists()) {
                destFile.delete();
            }
            try {
                destFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try(InputStream inStream = new FileInputStream(srcFile);
                FileOutputStream fs = new FileOutputStream(destFile)) {
                int byteread = 0;
                byte[] buffer = new byte[1024 * 1024];// 1MB
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

package com.bits.cps.Helper;

import android.os.StrictMode;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class UploadFileNoAsync {
    private int serverResponseCode;
    private String serverResponseMessage;
    private ArrayList responseMessage;

    public ArrayList upload(String sourceFileUri, String serverPath, int rowid, String tbname) {
        try {
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(sourceFileUri);
            if (sourceFile.isFile()) {
                try {

                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(serverPath);
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    // Open a HTTP connection to the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE",
                            "multipart/form-data");
                    conn.setRequestProperty("Content-Type",
                            "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("post_id", rowid + "");
                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\""
                            + sourceFileUri + "\"" + lineEnd);

                    dos.writeBytes(lineEnd);

                    // create a buffer of maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math
                                .min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0,
                                bufferSize);

                    }

                    // send multipart form data necesssary after file
                    // data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens
                            + lineEnd);

                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    serverResponseMessage = conn.getResponseMessage();

                    if (serverResponseCode == 200) {

                        //success

                    }
                    // close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                    String line;
                    responseMessage = new ArrayList();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        Log.d("line---", line);
                        responseMessage.add(line);
                    }

                } catch (Exception e) {

                    // dialog.dismiss();
                    e.printStackTrace();

                }
                // dialog.dismiss();

            } // End else block


        } catch (Exception ex) {
            // dialog.dismiss();

            ex.printStackTrace();
        }
        return responseMessage;
    }
}

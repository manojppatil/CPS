package com.bits.cps.Helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class AllRequest {
    static ArrayList responseMessage = new ArrayList();

    ArrayList getRequest() {
        return null;
    }

    public static ArrayList postRequest(String path, String query) {
        try {

            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(30000);
            connection.setConnectTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStream out = connection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            bufferedWriter.write(query);
            bufferedWriter.flush();
            bufferedWriter.close();
            out.close();
            int responseCode = connection.getResponseCode();
            responseMessage.clear();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    responseMessage.add(line);
                }
                br.close();
                connection.disconnect();
                return responseMessage;
            } else {
                responseMessage.add(connection.getResponseMessage());
                return responseMessage;
            }
        } catch (Exception ex) {
            responseMessage.add(ex.toString());
            return responseMessage;
        }

    }
}

package com.example.textpdf;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class Gpt3ApiTask extends AsyncTask<String, Void, String> {
    private WeakReference<Activity> activityRef;

    public Gpt3ApiTask(Activity activity) {
        activityRef = new WeakReference<>(activity);
    }
    @Override
    protected String doInBackground(String... strings) {
        try {
            // Set up the API endpoint URL
            URL url = new URL("https://api.openai.com/v1/chat/completions");

            // Create a HttpURLConnection object
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            // Set the API key in the Authorization header
            connection.setRequestProperty("Authorization", "Bearer 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");

            // Set the content type header
            connection.setRequestProperty("Content-Type", "application/json");

            // Enable input and output streams
            connection.setDoOutput(true);
            connection.setDoInput(true);
            String str="explain "+strings[0];
            // Set the request body
            String data = "{\"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"user\", \"content\": \""+str+"\"}], \"temperature\": 0.7}";

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(data.getBytes());
            outputStream.flush();

            // Get the response
            int responseCode = connection.getResponseCode();
            BufferedReader reader;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Close the connection
            connection.disconnect();
            Log.e("chatgptApiResponse", "response is "+response.toString() );

            String responseMessage = null;
            try {
                JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray choicesArray = jsonObject.getJSONArray("choices");
                Log.e("chatgptmsg","length is "+choicesArray.length() );
                if (choicesArray.length() > 0) {
                    JSONObject choiceObject = choicesArray.getJSONObject(0);
                    JSONObject messageObject = choiceObject.getJSONObject("message");
                    return messageObject.getString("content");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (responseMessage == null) {
                return "No response from ChatGPT";
            }
            return responseMessage;

        } catch (IOException e) {
            Log.e("ekkada", "line 80->" + "Error: " + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        Activity activity = activityRef.get();
        if (activity != null) {
            TextView textView=activity.findViewById(R.id.output_textview);
            textView.setText(response);
        } else {
            // Handle the error case
            Log.e("ekkada", "postexecute else" + "Error: Failed to retrieve API response.");
        }
    }
}

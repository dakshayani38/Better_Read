package com.example.textpdf;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchMeaning {
    private static final String API_ENDPOINT = "https://api.dictionaryapi.dev/api/v2/entries/en/";
    private String wordInput;
    private String textToReturn;
    public String searchWord(String wordInput) {

        this.wordInput=wordInput;
        String userInput = wordInput.toLowerCase();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(API_ENDPOINT + userInput)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("DictionaryActivity", "Error making API request: " + e.getMessage());
                textToReturn =  "An error occurred. Please try again later.";
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseBody = response.body().string();
                    ParseResultsTask task = new ParseResultsTask();
                    Log.e("DictionaryActivity", "made http request");
                    task.execute(responseBody);


                } else {
                    Log.e("DictionaryActivity", "Request failed with status code: " + response.code());
                    textToReturn = "No meanings found for the word \"" + userInput + "\".";
                }
            }
        });
        return textToReturn;
    }

    private class ParseResultsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String jsonResponse = params[0];

            String userInput = wordInput.toLowerCase();
            try {
                JSONArray jsonArray = new JSONArray(jsonResponse);

                if (jsonArray.length() > 0) {
                    StringBuilder meaningsBuilder = new StringBuilder();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject wordObject = jsonArray.getJSONObject(i);
                        JSONArray meaningsArray = wordObject.getJSONArray("meanings");

                        // Append word and phonetic information if needed
                        meaningsBuilder.append("Word: ").append(wordObject.getString("word")).append("\n");
                        if (wordObject.has("phonetic")) {
                            meaningsBuilder.append("Phonetic: ").append(wordObject.getString("phonetic")).append("\n\n");
                        }

                        for (int j = 0; j < meaningsArray.length(); j++) {
                            JSONObject meaningObject = meaningsArray.getJSONObject(j);
                            String partOfSpeech = meaningObject.getString("partOfSpeech");
                            JSONArray definitionsArray = meaningObject.getJSONArray("definitions");

                            meaningsBuilder.append(partOfSpeech).append(":").append("\n");
                            for (int k = 0; k < definitionsArray.length(); k++) {
                                JSONObject definitionObject = definitionsArray.getJSONObject(k);
                                String definition = definitionObject.getString("definition");
                                String example = definitionObject.optString("example", "");

                                meaningsBuilder.append("- Definition: ").append(definition).append("\n");
                                if (!example.isEmpty()) {
                                    meaningsBuilder.append("  Example: ").append(example).append("\n");
                                }
                            }
                            meaningsBuilder.append("\n");
                        }
                        meaningsBuilder.append("\n");
                    }
                    return meaningsBuilder.toString();
                } else {
                    return ("No meanings found for the word \"" + userInput + "\".");
                }
            } catch (JSONException e) {
                Log.e("DictionaryActivity", "Error parsing JSON data: " + e.getMessage());
                return("An error occurred while processing the response.");
            }
        }
        @Override
        protected void onPostExecute(String result) {
            textToReturn = result;
        }
    }
}

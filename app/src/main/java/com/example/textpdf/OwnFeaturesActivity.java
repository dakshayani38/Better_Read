package com.example.textpdf;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OwnFeaturesActivity extends AppCompatActivity{
    private static int textviewSize = 16;
    private String selectedText;
    private SearchMeaning searchMeaning;
    private TextToSpeechClass textToSpeechClass;
    private TextView outputTextview;
    private Gpt3ApiTask gpt3ApiTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_features);

        Intent i = getIntent();
        selectedText = i.getStringExtra("selectedtext");
        TextView selectedTextTextview = findViewById(R.id.selectedText);
        selectedTextTextview.setText(selectedText);

        outputTextview = findViewById(R.id.output_textview);
        searchMeaning = new SearchMeaning();
        textToSpeechClass = new TextToSpeechClass(OwnFeaturesActivity.this);
        gpt3ApiTask = new Gpt3ApiTask(OwnFeaturesActivity.this);
    }

    public void Meaning(View view){
        outputTextview.setText(searchMeaning.searchWord(selectedText));
    }

    public void Pronounciation(View view){
        textToSpeechClass.speak(selectedText);
    }

    public void ChatGPT(View view){
        // Create a new instance of Gpt3ApiTask each time ChatGPT is called
        Gpt3ApiTask gpt3ApiTask = new Gpt3ApiTask(OwnFeaturesActivity.this);
        gpt3ApiTask.execute(selectedText);

    }

    public void ZoomInOutput(View view){
        if(textviewSize>40) return;
        textviewSize+=2;
        outputTextview.setTextSize(textviewSize);
    }
    public void ZoomOutOutput(View view){
        if(textviewSize<24) return;
        textviewSize-=2;
        outputTextview.setTextSize(textviewSize);
    }

}






package com.example.textpdf;

import android.content.Context;
import android.speech.tts.TextToSpeech;


public class TextToSpeechClass implements TextToSpeech.OnInitListener{
    private TextToSpeech textToSpeech;
    TextToSpeechClass(Context context){
        textToSpeech = new TextToSpeech(context, this);
    }

    public void speak(String textToSpeak){
        textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    public void onInit(int status) {

    }

    protected void finalize(){
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}

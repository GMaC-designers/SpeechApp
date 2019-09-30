package com.gmail.speechapp;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final int REQ_CODE = 100;
    TextView textView, clear;
    String outPut;
    private TextToSpeech textToSpeech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text);
        clear = findViewById(R.id.tvClear);
        clear.setVisibility(View.VISIBLE);
        clear.setText("What is my name?");
        ImageView speak = findViewById(R.id.speak);
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
                try {
                    startActivityForResult(intent, REQ_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry your device not supported",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    outPut = (result != null ? result.get(0) : null);

                    if (outPut != null) {
                        if (!outPut.equals("George")) {
                            textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status == TextToSpeech.SUCCESS) {
                                        int ttsLang = textToSpeech.setLanguage(Locale.US);

                                        if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                                                || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                                            Log.e("TTS", "The Language is not supported!");
                                        } else {
                                            Log.i("TTS", "Language Supported.");
                                        }
                                        Log.i("TTS", "Initialization success.");



                                        String Data = "That's not my name";
                                        onSpeech(Data);

                                    } else {
                                        Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            // Toast.makeText(getApplicationContext(), "That's not my name", Toast.LENGTH_LONG).show();
                        } else {
                            textView.setText(outPut);
                            String Data = "Your are correct";
                            onSpeech(Data);
                            clear.setText("Next");
                        }
                    }
                }
                break;
            }
        }
    }

    private void onSpeech(String Data) {
        int speechStatus = textToSpeech.speak(Data, TextToSpeech.QUEUE_FLUSH, null);

        if (speechStatus == TextToSpeech.ERROR) {
            Log.e("TTS", "Error in converting Text to Speech!");
        }
    }

    public void onClear(View view) {
        textView.setText("");
        clear.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), outPut, Toast.LENGTH_LONG).show();

    }
}

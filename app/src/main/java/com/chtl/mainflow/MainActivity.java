package com.chtl.mainflow;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.AudioManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {

    private final int REQ_CODE_SPEECH_INPUT = 100, REQ_CODE_TEXT_INPUT = 110;
    RelativeLayout main_screen;
    TextView welcome_text;
    String TAG = "MainActivity";
    int tts_flag;
    private TextToSpeech tts;
    private boolean ttsIsInit = false;
    String welcome_speech = "welcome sir! to send an email say, \"send email\", to go to inbox say, \"inbox\", to go to sent items say, \"sent items\".";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_screen = (RelativeLayout)findViewById(R.id.activity_main);
        welcome_text = (TextView)findViewById(R.id.welcome_text);
        main_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Welcome Speech Started");
                startTextToSpeech();
                welcome_text.setText(welcome_speech);

            }
        });
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Hi speak something");
        try {
            Log.i(TAG, "Try e dhukse");
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Log.i(TAG, "Catch e dhukse");
        }
    }

    private void startTextToSpeech() {
        Intent intent = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, REQ_CODE_TEXT_INPUT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult e dhukse");
        if (requestCode == REQ_CODE_TEXT_INPUT)
        {
            Log.i(TAG, "onActivityResult when TEXT_INPUT");
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts_flag = 1;
                tts = new TextToSpeech(this, this);
            }
            else {
                Intent installVoice = new Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installVoice);
            }
        }
        if (requestCode == REQ_CODE_SPEECH_INPUT)
        {
            Log.i(TAG, "onActivityResult when SPEECH_INPUT");
            if (resultCode == RESULT_OK && null != data)
            {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                Log.i(TAG, "Speaker Output : " + result.get(0));
                if (result.get(0).equals("send email"))
                {
                    welcome_text.setText("going to send email");
                    tts_flag = 2;
                    tts = new TextToSpeech(this, this);
                }
                else if (result.get(0).equals("inbox"))
                {
                    welcome_text.setText("going to inbox");
                    tts_flag = 3;
                    tts = new TextToSpeech(this, this);
                }
                else if (result.get(0).equals("sent items"))
                {
                    welcome_text.setText("going to sent Items");
                    tts_flag = 4;
                    tts = new TextToSpeech(this, this);
                }
                else
                {
                    welcome_text.setText("somthing else said");
                    tts_flag = 5;
                    tts = new TextToSpeech(this, this);
                }
            }
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            ttsIsInit = true;
            int result = tts.setOnUtteranceCompletedListener(this);
            if (tts.isLanguageAvailable(Locale.ENGLISH) >= 0)
                tts.setLanguage(Locale.ENGLISH);
            //tts.setPitch(5.0f);
            tts.setSpeechRate(1.0f);

            HashMap<String, String> myHashAlarm = new HashMap<String, String>();
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "SOME MESSAGE");
            if (tts_flag==1)
            {
                tts.speak(welcome_speech, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
            }
            else if (tts_flag==2)
            {
                tts.speak("going to send email", TextToSpeech.QUEUE_FLUSH, myHashAlarm);
            }
            else if (tts_flag==3)
            {
                tts.speak("going to inbox", TextToSpeech.QUEUE_FLUSH, myHashAlarm);
            }
            else if (tts_flag==4)
            {
                tts.speak("going to sent items", TextToSpeech.QUEUE_FLUSH, myHashAlarm);
            }
        }
    }

    @Override
    public void onUtteranceCompleted(String s) {
        Log.i(TAG, "Welcome Speech Finished");
        if (tts_flag==1)
        {
            Log.i(TAG, "1e dhukse");
            promptSpeechInput();
        }
        else if (tts_flag==2)
        {
            //Log.i(TAG, "2e dhukse");
            Intent intent = new Intent(MainActivity.this, WriteEmailAddress.class);
            startActivity(intent);
        }
        else if (tts_flag==3)
        {
            //Log.i(TAG, "2e dhukse");
            Intent intent = new Intent(MainActivity.this, MailReaderList.class);
            startActivity(intent);
        }
        else if (tts_flag==4)
        {
            //Log.i(TAG, "2e dhukse");
            Intent intent = new Intent(MainActivity.this, SentItemsActivity.class);
            startActivity(intent);
        }
    }
}

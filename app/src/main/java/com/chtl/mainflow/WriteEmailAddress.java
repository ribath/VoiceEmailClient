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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class WriteEmailAddress extends AppCompatActivity implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {

    private TextToSpeech tts;
    LinearLayout upperHalf, lowerHalf;
    TextView receiverEmailTextView, textHint;
    int REQ_CODE_TEXT_INPUT = 100, REQ_CODE_SPEECH_INPUT = 200;
    String receiverEmail;
    int flag, anotherFlag;
    String TAG = "WriteEmailAddress";
    String welcomeSpeech = "speak address of the receiver. tap the lower half of your screen to add letter to the address. tap upper half before speaking the domain name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_email_address);

        upperHalf = (LinearLayout)findViewById(R.id.upperHalf);
        lowerHalf = (LinearLayout)findViewById(R.id.lowerHalf);
        receiverEmailTextView = (TextView)findViewById(R.id.receiverEmailId);
        textHint = (TextView)findViewById(R.id.textHint);
        textHint.setText(welcomeSpeech);

        flag = 1;
        startTextToSpeech();

        lowerHalf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag++;
                promptSpeechInput();
            }
        });
        upperHalf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anotherFlag++;
                if (anotherFlag==1)
                {
                    receiverEmail = receiverEmail + "@";
                    receiverEmailTextView.setText(receiverEmail);
                }
                if (anotherFlag==2)
                {
                    startTextToSpeech();
//                    Intent intent = new Intent(WriteEmailAddress.this, WriteSubject.class);
//                    intent.putExtra("receiver", receiverEmail);
//                    startActivity(intent);
                }
            }
        });
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say something cool");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn\'t support speech input",
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQ_CODE_SPEECH_INPUT)
        {
            if (resultCode == RESULT_OK && null != data)
            {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                Log.i(TAG, "Speaker Output : " + result.get(0));
                if (flag==1)
                {
                    receiverEmail = result.get(0).replace(" ", "");
                }
                else
                {
                    receiverEmail = receiverEmail + result.get(0);
                }
                receiverEmailTextView.setText(receiverEmail);
            }
        }
        if (requestCode == REQ_CODE_TEXT_INPUT)
        {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts = new TextToSpeech(this, this);
            }
            else {
                Intent installVoice = new Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installVoice);
            }
        }
    }

    private void startTextToSpeech() {
        Intent intent = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, REQ_CODE_TEXT_INPUT);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setOnUtteranceCompletedListener(this);
            if (tts.isLanguageAvailable(Locale.ENGLISH) >= 0)
                tts.setLanguage(Locale.ENGLISH);
            //tts.setPitch(5.0f);
            tts.setSpeechRate(1.0f);

            HashMap<String, String> myHashAlarm = new HashMap<String, String>();
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "SOME MESSAGE");
            if(anotherFlag!=2)
            {
                tts.speak(welcomeSpeech, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
            }
            if(anotherFlag==2)
            {
                tts.speak(receiverEmail, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
            }

        }
    }

    @Override
    public void onUtteranceCompleted(String s) {
        Log.i(TAG, "Welcome Speech Finished");
        if(anotherFlag!=2)
        {
            promptSpeechInput();
        }
        if(anotherFlag==2)
        {
            Intent intent = new Intent(WriteEmailAddress.this, WriteSubject.class);
            intent.putExtra("receiver", receiverEmail);
            startActivity(intent);
        }

    }
}

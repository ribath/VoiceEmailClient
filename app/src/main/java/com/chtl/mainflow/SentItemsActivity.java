package com.chtl.mainflow;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.AudioManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SentItemsActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {

    private TextToSpeech tts;
    RecyclerView recyclerView;
    SentItemsAdapter adapter;
    List<SentMail> mlist;
    int REQ_CODE_TEXT_INPUT = 100, REQ_CODE_SPEECH_INPUT = 200;
    int flag, subNo=0;
    boolean openFlag=false;
    String TAG = "SentItemsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_items);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);

        //////////////////// DB + RecyclerView /////////////////////
        DBHelper dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);
        try {
            Dao<SentMail, String> SentMailDao = dbHelper.getSentMailDao();
            mlist = SentMailDao.queryForAll();
            adapter = new SentItemsAdapter(mlist);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //////////////////// DB + RecyclerView /////////////////////

        flag = 1;
//        for (int i=0; i<mlist.size(); i++)
//        {
//            allSunjects = allSunjects + mlist.get(i).getSubject() + ", ";
//        }
        startTextToSpeech();
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
                //tts_flag = 1;
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
                if (result.get(0).equals("open"))
                {
                    openFlag = true;
                    tts = new TextToSpeech(this, this);
                }
                else if (result.get(0).equals("next"))
                {
                    openFlag = false;
                    subNo++;
                    if (subNo<mlist.size())
                    {
                        tts = new TextToSpeech(this, this);
                    }
                }
            }
        }
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
            if (openFlag==false)
            {
                tts.speak(mlist.get(subNo).getSubject(), TextToSpeech.QUEUE_FLUSH, myHashAlarm);
            }
            if (openFlag==true)
            {
                tts.speak(mlist.get(subNo).getBody(), TextToSpeech.QUEUE_FLUSH, myHashAlarm);
            }
        }
    }

    @Override
    public void onUtteranceCompleted(String s) {
        Log.i(TAG, "Welcome Speech Finished");
        if (flag==1)
        {
            flag++;
            Log.i(TAG, "1e dhukse");
            promptSpeechInput();
        } else if(openFlag==false)
        {
            promptSpeechInput();
        }

    }


}

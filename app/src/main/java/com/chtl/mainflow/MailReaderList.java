package com.chtl.mainflow;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

public class MailReaderList extends AppCompatActivity implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener{

    public ArrayList<SentMail> msgList;
    RecyclerView recyclerView;
    SentItemsAdapter adapter;
    private String user;
    private String password;
    private String TAG = "MailReaderList";
    boolean openFlag=false;
    int REQ_CODE_TEXT_INPUT = 100, REQ_CODE_SPEECH_INPUT = 200;
    int flag, subNo=0;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_reader_list);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);

        checkMail();

    }

    private void checkMail() {
        //user = "corolla.74x";
        //password = "Iamback74";
        user = "moni.asmasultana";
        password = "Password#1";

        AsyncTask at = new MyAsyncTask(user,password,this);
        at.execute();
    }

    public class MyAsyncTask extends AsyncTask {

        private final String password;
        private final String user;
        private Message[] msgs;
        private GMailReader gm;
        Context context;

        MyAsyncTask(String user, String password, Context context){
            this.user = user;
            this.password = password;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Object doInBackground(Object[] params) {

            try {
                gm = new GMailReader(user,password);
                Folder f = gm.readMail();
                msgs = f.getMessages(f.getMessageCount()-14,f.getMessageCount());
                msgList = new ArrayList<>();
                for (int i=0;i<msgs.length;i++){
                    msgList.add(new SentMail(msgs[msgs.length-1-i].getSentDate().toString(), msgs[msgs.length-1-i].getFrom().toString(), msgs[msgs.length-1-i].getSubject(), getTextFromMessage(msgs[msgs.length-1-i])));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {

            adapter = new SentItemsAdapter(msgList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
            startTextToSpeech();
            Log.i(TAG, "last sub:"+msgList.get(msgList.size()-1).getSubject());
            Log.i(TAG, "last msg:"+msgList.get(msgList.size()-1).getBody());

            super.onPostExecute(o);
        }
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Hi speak something");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
        }
    }

    private void startTextToSpeech() {
        Intent intent = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, REQ_CODE_TEXT_INPUT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.i(TAG, "onActivityResult e dhukse");
        if (requestCode == REQ_CODE_TEXT_INPUT)
        {
            //Log.i(TAG, "onActivityResult when TEXT_INPUT");
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
            //Log.i(TAG, "onActivityResult when SPEECH_INPUT");
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
                    if (subNo<msgList.size())
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
                Log.i(TAG, msgList.get(subNo).getSubject());
                tts.speak(msgList.get(subNo).getSubject(), TextToSpeech.QUEUE_FLUSH, myHashAlarm);
            }
            if (openFlag==true)
            {
                Log.i(TAG, msgList.get(subNo).getBody());
                tts.speak(msgList.get(subNo).getBody(), TextToSpeech.QUEUE_FLUSH, myHashAlarm);
            }
        }
    }

    @Override
    public void onUtteranceCompleted(String s) {
        //Log.i(TAG, "Welcome Speech Finished");
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

    private String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain")){
            return message.getContent().toString();
        }else if (message.isMimeType("multipart/*")) {
            String result = "";
            MimeMultipart mimeMultipart = (MimeMultipart)message.getContent();
            int count = mimeMultipart.getCount();
            for (int i = 0; i < count; i ++){
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")){
                    result = result + "\n" + bodyPart.getContent();
                    break;  //without break same text appears twice in my tests
                } else if (bodyPart.isMimeType("text/html")){
                    String html = (String) bodyPart.getContent();
                    result = result + "\n" + Jsoup.parse(html).text();

                }
            }
            return result;
        }
        return "";
    }

}

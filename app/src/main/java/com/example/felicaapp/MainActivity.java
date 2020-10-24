package com.example.felicaapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Formatter;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //Viewで使う変数を初期化（別にここじゃなくてもいいけど）
    TextView txt01;
    Button btn01;
    Button btn02;

    //NfcAdapterを初期化
    NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UIのマーツをマッピング
        txt01 = findViewById(R.id.txt01);
        btn01 = findViewById(R.id.btn01);
        btn02 = findViewById(R.id.btn02);

        //nfcAdapter初期化
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        //Reader Mode Offボタンのenabledをfalseに（トグルにするため）
        btn02.setEnabled(false);

        btn01.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                //トグル機能
                btn01.setEnabled(false);
                btn02.setEnabled(true);

                //Redermode On
                nfcAdapter.enableReaderMode(MainActivity.this,new MyReaderCallback(),NfcAdapter.FLAG_READER_NFC_F,null);
            }
        });

        btn02.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                //トグル機能
                btn01.setEnabled(true);
                btn02.setEnabled(false);

                //Readermode Off
                nfcAdapter.disableReaderMode(MainActivity.this);

                //表示初期化
                txt01.setText("Read ID ...");
            }
        });
    }

    //Callback Class
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private class MyReaderCallback implements NfcAdapter.ReaderCallback{
        @Override
        public void onTagDiscovered(Tag tag){

            Log.d("Hoge","Tag discoverd.");

            //get idm
            byte[] idm = tag.getId();
            final String idmString = bytesToHexString(idm);

            //idm取るだけじゃなくてread,writeしたい場合はtag利用してごにょごにょする

            //親スレッドのUIを更新するためごにょごにょ
            final Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    txt01.setText(idmString);
                }
            });

        }
    }

    //bytes列を16進数文字列に変換（めんどい）
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return sb.toString().toUpperCase(Locale.getDefault());
    }
}
package com.example.x3033074.final_progjissen;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/*
問題説明画面を表示するクラス
半透明のActivityを背景としたクラス
 */
public class ExplanationActivity extends AppCompatActivity implements View.OnClickListener {

    private Button returnButton; // MainActivityに戻るButtonクラス

    private TextView quiznameText, exampleText, exampleanswerText, quizexplainText; // それぞれクイズ名、クイズ例題、答、問題説明のテキストを表示

    private boolean quiz1mode, quiz2mode, quiz3mode;// それぞれのクイズについて、選択したらtrue, していなければfalse
    private boolean Bgm, Se; // MainActivityからのBgm, Seの状態を保持するためにこのActivityで受け取る
    private boolean button_status; // Activity遷移に関するボタンを一度しか押せないようにする

    private SoundPool soundPool; // Seを鳴らすためのクラス
    private int soundTap; // ボタンを押したときの音源

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explanation); // 問題説明画面の表示

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                // USAGE_MEDIA
                // USAGE_GAME
                .setUsage(AudioAttributes.USAGE_GAME)
                // CONTENT_TYPE_MUSIC
                // CONTENT_TYPE_SPEECH, etc.
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                // ストリーム数に応じて
                .setMaxStreams(2)
                .build();

        // tap.mp3 をロードしておく
        soundTap = soundPool.load(this, R.raw.tap, 1);

        // load が終わったか確認する場合
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.d("debug","sampleId="+sampleId);
                Log.d("debug","status="+status);
            }
        });

        button_status = false; // まだ押せる

        Intent intent = getIntent(); // Intent を取得する
        quiz1mode = intent.getBooleanExtra("EXTRA_DATA1", false); // MainActivityから受け取る,データがなければfalse
        quiz2mode = intent.getBooleanExtra("EXTRA_DATA2", false); // MainActivityから受け取る,データがなければfalse
        quiz3mode = intent.getBooleanExtra("EXTRA_DATA3", false);// MainActivityから受け取る,データがなければfalse
        Bgm = intent.getBooleanExtra("CONFIG_EXTRA_DATA1", true); // MainActivityから受け取る,データがなければtrue
        Se = intent.getBooleanExtra("CONFIG_EXTRA_DATA2", true); // MainActivityから受け取る,データがなければtrue

        returnButton = (Button) findViewById(R.id.selectreturn_but); // そのIDが振られたウィジェットをコード側で操作をする
        returnButton.setOnClickListener(this); // 取得したボタンオブジェクトに、ボタンで発生したイベントを送る先と して、この Activity オブジェクトを指定
        // そのIDが振られたウィジェットをコード側で操作をする
        quiznameText = (TextView) findViewById(R.id.quizname_txt);
        exampleText = (TextView) findViewById(R.id.example_txt);
        exampleanswerText = (TextView) findViewById(R.id.exampleanswer_txt);
        quizexplainText = (TextView) findViewById(R.id.explanation_txt);

        // 選択したクイズについての情報をそれぞれのTextViewに表示する
        if(quiz1mode) { // サンプルクイズの説明
            quiznameText.setText("サンプルクイズ");
            exampleText.setText("SAMPLE");
            exampleanswerText.setText("SAMPLE");
            quizexplainText.setText("SAMPLE");
        }
        else if(quiz2mode) { // 四則演算クイズの説明
            quiznameText.setText("四則演算クイズ");
            exampleText.setText("（例）１＋１");
            exampleanswerText.setText("（答）２");
            quizexplainText.setText("正しい計算結果を答える");
        }
        else if(quiz3mode) { // 四字熟語クイズの説明
            quiznameText.setText("四字熟語クイズ");
            exampleText.setText("（例）一期一〇");
            exampleanswerText.setText("（答）会");
            quizexplainText.setText("〇に入る正しい漢字を答える");
        }
    }

    @Override
    public void onClick(View v) {
        // ボタンを押したら
        if(!button_status) {
            button_status = true; // Activityが多重にならないようにする
            boolean returnExplanation = true; // ExplanationActivityからの遷移であることをMainActivityに知らせる

            if(Se) soundPool.play(soundTap, 1.0f, 1.0f, 0, 0, 1); // タップ音を鳴らす

            Intent intent = new Intent(this, MainActivity.class); // MainActivityに遷移準備
            intent.putExtra("EXTRA_DATA1", quiz1mode); // このクラスのquiz1modeをMainActivityに渡す
            intent.putExtra("EXTRA_DATA2", quiz2mode); // このクラスのquiz2modeをMainActivityに渡す
            intent.putExtra("EXTRA_DATA3", quiz3mode); // このクラスのquiz3modeをMainActivityに渡す
            intent.putExtra("CONFIG_EXTRA_DATA1", Bgm); // このクラスのBgmをMainActivityに渡す
            intent.putExtra("CONFIG_EXTRA_DATA2", Se); // このクラスのSeをMainActivityに渡す
            intent.putExtra("RETURN_EXPLANATION_DATA", returnExplanation); // このクラスのreturnExplanationをMainActivityに渡す
            startActivity(intent); // 遷移開始
        }
    }
}

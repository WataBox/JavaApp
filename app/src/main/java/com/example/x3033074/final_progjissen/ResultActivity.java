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
クイズを終え最終結果を表示するクラス
 */
public class ResultActivity extends AppCompatActivity implements View.OnClickListener {

    private Button finishButton; // タイトル画面に戻るためのButtonクラス
    private Button RetlyButton; // リトライするためのButtonクラス
    private TextView resultText ,commentText; // 得点を表示するためのTextViewクラス
    private boolean quiz1mode; // QuizActivityからのデータ,quiz1modeを受け取るための変数
    private boolean quiz2mode; // QuizActivityからのデータ,quiz2modeを受け取るための変数
    private boolean quiz3mode; // QuizActivityからのデータ,quiz3modeを受け取るための変数
    private boolean Bgm; // QuizActivityからのデータ,Bgmを受け取るための変数
    private boolean Se; // QuizActivityからのデータ,Seを受け取るための変数
    private boolean button_status; // 一度しかボタンを押せないようにする
    private SoundPool soundPool; // SEを再生するためのクラス
    private int soundbestResult, soundnormalResult, soundworstResult, soundTap; // SEのロードのための変数
    private int point; // QuizActivityからのデータ,pointを受け取るための変数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result); // 結果画面の表示

        button_status = false; // まだ押せる

        Intent intent = getIntent(); // Intent を取得する
        quiz1mode = intent.getBooleanExtra("EXTRA_DATA1", false); // QuizActivityから受け取る,データがなければfalse
        quiz2mode = intent.getBooleanExtra("EXTRA_DATA2", false); // QuizActivityから受け取る,データがなければfalse
        quiz3mode = intent.getBooleanExtra("EXTRA_DATA3", false);// QuizActivityから受け取る,データがなければfalse
        point = intent.getIntExtra("EXTRA_DATA4", 0);// QuizActivityから受け取る,データがなければ0
        Bgm = intent.getBooleanExtra("CONFIG_EXTRA_DATA1", true); // QuizActivityから受け取る,データがなければtrue
        Se = intent.getBooleanExtra("CONFIG_EXTRA_DATA2", true); // QuizActivityから受け取る,データがなければtrue

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

        // bestresult.mp3 をロードしておく
        soundbestResult = soundPool.load(this, R.raw.bestresult, 1);

        // result.mp3 をロードしておく
        soundnormalResult = soundPool.load(this, R.raw.result, 1);

        // worstresult.mp3 をロードしておく
        soundworstResult = soundPool.load(this, R.raw.worstresult, 1);

        // tap.mp3 をロードしておく
        soundTap = soundPool.load(this, R.raw.tap, 1);

        // load が終わったか確認する場合
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if(Se && point == 100) soundPool.play(soundbestResult, 1.0f, 1.0f, 0, 0, 1); // 100点ならこの音を鳴らす
                if(Se && point < 100 && point >0) soundPool.play(soundnormalResult, 1.0f, 1.0f, 0, 0, 1); // 0, 100点以外ならこの音を鳴らす
                else if(Se && point == 0) soundPool.play(soundworstResult, 1.0f, 1.0f, 0, 0, 1); //0点ならこの音を鳴らす
                Log.d("debug","sampleId="+sampleId);
                Log.d("debug","status="+status);
            }
        });

        // そのIDが振られたウィジェットをコード側で操作をする
        finishButton = findViewById(R.id.finish_but);
        RetlyButton = findViewById(R.id.retly_but);
        // 取得したボタンオブジェクトに、ボタンで発生したイベントを送る先と して、この Activity オブジェクトを指定
        finishButton.setOnClickListener(this);
        RetlyButton.setOnClickListener(this);

        // そのIDが振られたウィジェットをコード側で操作をする
        resultText = findViewById(R.id.result_txt);
        resultText.setText(Integer.toString(point)); // テキストビューに点数を表示
        // そのIDが振られたウィジェットをコード側で操作をする
        commentText = (TextView) findViewById(R.id.comment_txt);
        if(point == 100) commentText.setText("Excellent!!"); // 満点ならこの文字を表示
        else if(point != 100 && point != 0) commentText.setText("Good Job!"); // 0, 100点以外ならこの文字を表示
        else if(point == 0) commentText.setText("Don't mind"); // 0点ならこの文字を表示
    }

    @Override
    public void onClick(View view) {
        if(!button_status) {
            if(Se) soundPool.play(soundTap, 1.0f, 1.0f, 0, 0, 1);
            button_status = true; // 一度押したら, この分岐に入らずActivityが多重にかかることを防ぐ
            if (view.getId() == R.id.finish_but) {
                Intent intent = new Intent(this, MainActivity.class); // MainActivityに遷移準備
                intent.putExtra("CONFIG_EXTRA_DATA1", Bgm); // このクラスのBgmをMainActivityに渡す
                intent.putExtra("CONFIG_EXTRA_DATA2", Se); // このクラスのSeをMainActivityに渡す
                startActivity(intent); // 遷移開始
            } else if (view.getId() == R.id.retly_but) {
                Intent intent = new Intent(this, CountDownActivity.class); // リトライでCountDownActivityに遷移準備
                intent.putExtra("EXTRA_DATA1", quiz1mode); // このクラスのquiz1modeをCountDownActivityに渡す
                intent.putExtra("EXTRA_DATA2", quiz2mode); // このクラスのquiz2modeをCountDownActivityに渡す
                intent.putExtra("EXTRA_DATA3", quiz3mode); // このクラスのquiz3modeをCountDownActivityに渡す
                intent.putExtra("CONFIG_EXTRA_DATA1", Bgm); // このクラスのBgmをCountDownActivityに渡す
                intent.putExtra("CONFIG_EXTRA_DATA2", Se); // このクラスのSeをCountDownActivityに渡す
                startActivity(intent); // 遷移開始
            }
        }
    }
}

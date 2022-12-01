package com.example.x3033074.final_progjissen;

import android.media.AudioAttributes;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/*
クイズスタートまでのカウントダウンを行うクラス
 */
public class CountDownActivity extends AppCompatActivity {

    private TextView timerText; // カウントダウンの数字を表示

    private boolean quiz1mode; // 他Activityからのquiz1modeの値を受け取るための変数
    private boolean quiz2mode; // 他Activityからのquiz2modeの値を受け取るための変数
    private boolean quiz3mode; // 他Activityからのquiz3modeの値を受け取るための変数
    private boolean count; // カウントダウン音源を一度だけ再生するための変数
    private boolean Bgm; // 他ActivityからのBgmの値を受け取るための変数
    private boolean Se; // 他ActivityからのSeの値を受け取るための変数

    private SoundPool soundPool; // SEを再生するためのクラス
    private int soundCountDown; // SEのロードのための変数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown); // カウントダウン画面を表示
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

        // count3.mp3 をロードしておく
        soundCountDown = soundPool.load(this, R.raw.count3, 1);

        // load が終わったか確認する場合
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.d("debug","sampleId="+sampleId);
                Log.d("debug","status="+status);
            }
        });

        Intent intent = getIntent(); // Intent を取得する
        quiz1mode = intent.getBooleanExtra("EXTRA_DATA1", false); // 他Activityから受け取る,データがなければfalse
        quiz2mode = intent.getBooleanExtra("EXTRA_DATA2", false); // 他Activityから受け取る,データがなければfalse
        quiz3mode = intent.getBooleanExtra("EXTRA_DATA3", false);// 他Activityから受け取る,データがなければfalse
        Bgm = intent.getBooleanExtra("CONFIG_EXTRA_DATA1", true); // 他Activityから受け取る,データがなければtrue
        Se = intent.getBooleanExtra("CONFIG_EXTRA_DATA2", true); // 他Activityから受け取る,データがなければtrue

        count = false; // まだカウントダウン音声が再生されていない

        // 4.3秒= 3.3x1000 = 4300 msec
        long countNumber = 4300;
        // インターバル msec
        long interval = 100;
        // そのIDが振られたウィジェットをコード側で操作をする
        timerText = findViewById(R.id.timer);

        // インスタンス生成
        final CountDown countDown = new CountDown(countNumber, interval);
        countDown.start(); // カウントダウンスタート

    }

    class CountDown extends CountDownTimer {
        CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            // 完了(カウントダウンが終了したら)
            Intent intent = new Intent(CountDownActivity.this, QuizActivity.class); // Activity遷移のためのクラス
            intent.putExtra("EXTRA_DATA1", quiz1mode); // このクラスのquiz1modeをQuizActivityに渡す
            intent.putExtra("EXTRA_DATA2", quiz2mode); // このクラスのquiz2modeをQuizActivityに渡す
            intent.putExtra("EXTRA_DATA3", quiz3mode); // このクラスのquiz3modeをQuizActivityに渡す
            intent.putExtra("CONFIG_EXTRA_DATA1", Bgm); // このクラスのBgmをQuizActivityに渡す
            intent.putExtra("CONFIG_EXTRA_DATA2", Se); // このクラスのSeをQuizActivityに渡す
            startActivity(intent); // QuizActivityを開始する
        }

        // インターバルで呼ばれる
        @Override
        public void onTick(long millisUntilFinished) {
            // 残り時間を分、秒、ミリ秒に分割
            //long mm = millisUntilFinished / 1000 / 60;
            //long ss = millisUntilFinished / 1000 % 60;
            //long ms = millisUntilFinished - ss * 1000 - mm * 1000 * 60;
            int time = (int)millisUntilFinished; // intにキャスト
            if(time < 4000 && time > 3000){ // 残り4秒以下3秒以上のとき
                timerText.setText("3"); // TextViewに3秒前であることを文字列で表示
                // 一度だけ再生（これをしないと音源が多重に再生される）
                if(!count && Se) { // CountDown音源が一度も再生されていないなら
                    soundPool.play(soundCountDown, 1.0f, 1.0f, 0, 0, 1); // 再生
                    count = true; // 一度再生されたらもうこの分岐に入らないようにする
                }
            }
            else if (time < 3000 && time > 2000){ // 残り3秒以下2秒以上のとき
                timerText.setText("2"); // TextViewに2秒前であることを文字列で表示
            }
            else if (time < 2000 && time > 1000){ // 残り2秒未満1秒以上
                timerText.setText("1"); // TextViewに1秒前であることを文字列で表示
            }
            else if(time < 1000){ // 残り1秒未満
                timerText.setText("START!"); // TextViewにクイズ開始を表す文字列を表示
            }
        }
    }
}


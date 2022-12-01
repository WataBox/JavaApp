package com.example.x3033074.final_progjissen;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/*
タイトル画面, 問題選択画面, 設定画面を表示するクラス
 */
/*
参考文献
https://akira-watson.com/android/activity-1.html
https://qiita.com/mii-chang/items/94fad3a778377a18ccf5
http://s-takumi.hatenablog.com/entry/2014/06/01/173236
https://akira-watson.com/android/soundpool.html#1
https://akira-watson.com/android/theme-translucent.html
https://akira-watson.com/android/touchevent.html
https://www.koheiando.com/tech/android/204
https://teratail.com/questions/95341
https://www.kunimiyasoft.com/program_tess_two/#tess-two
https://android.keicode.com/basics/ui-canvas-path.php
https://esthersoftware.hatenablog.com/entry/2019/09/23/194009
https://akira-watson.com/android/imageview.html
https://qiita.com/tktktks10/items/62d85dabac4bdb8c1f94
https://www.javadrive.jp/android/framelayout/index7.html
https://akira-watson.com/android/countdowntimer.html
https://qiita.com/shinya-tk/items/515ac1f9aef0dcc546c0
https://note.com/gokawashima/n/n2eb6dbe8b30c
https://www.javadrive.jp/android/activity/index2.html
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button quiz1Button; //クイズ1を選択するButtonクラス
    private Button quiz2Button; //クイズ2を選択するButtonクラス
    private Button quiz3Button; //クイズ3を選択するButtonクラス
    private Button startButton; //クイズをスタートするButtonクラス
    private Button configButton; //設定画面に遷移するButtonクラス
    private Button configreturnButton; //設定画面からタイトルに戻るButtonクラス
    private Button toExplanationButton; //問題説明のActivityに遷移するButtonクラス

    private TextView startText, quiznameText; //画面タッチによる画面遷移を示すテキスト, クイズ名を表示するテキスト

    private ConstraintLayout quizexplanation; //クイズによって背景色を一部変化させるためのConstraintLayoutクラス

    private boolean quiz1mode, quiz2mode, quiz3mode; //それぞれのクイズについて、選択したらtrue, していなければfalse
    private boolean Bgm, Se; //Bgm, Seをかけるならtrue, かけないならfalse
    private boolean returnExplanation; //trueならExplanationActivityから戻るときの動作, falseならデフォルト
    private boolean button_status1, button_status2; //Activity遷移に関するボタンを一度しか押せないようにする

    private Switch bgmSwitch, seSwitch; //Bgm, Seの有無を設定するSwitchクラス

    private SoundPool soundPool; //Seを鳴らすためのクラス
    private int soundTap; //ボタンを押したときの音源

    private MediaPlayer titleMusic; //Bgmを再生するためのクラス
    String musicpath; //Bgmの名前
    // 指定された文字列の内容に初期化された文字列バッファを構築
    private StringBuffer info = new StringBuffer("Test onTouchEvent\n\n");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                //USAGE_MEDIA
                //USAGE_GAME
                .setUsage(AudioAttributes.USAGE_GAME)
                //CONTENT_TYPE_MUSIC
                //CONTENT_TYPE_SPEECH, etc.
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                //ストリーム数に応じて
                .setMaxStreams(2)
                .build();

        //tap.mp3 をロードしておく
        soundTap = soundPool.load(this, R.raw.tap, 1);

        // load が終わったか確認する場合
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.d("debug","sampleId="+sampleId);
                Log.d("debug","status="+status);
            }
        });

        Intent intent = getIntent(); // Intent を取得する
        quiz1mode = intent.getBooleanExtra("EXTRA_DATA1", true); // 他Activityから受け取る,データがなければtrue
        quiz2mode = intent.getBooleanExtra("EXTRA_DATA2", false); // 他Activityから受け取る,データがなければfalse
        quiz3mode = intent.getBooleanExtra("EXTRA_DATA3", false);// 他Activityから受け取る,データがなければfalse
        Bgm = intent.getBooleanExtra("CONFIG_EXTRA_DATA1", true); // 他Activityから受け取る,データがなければtrue
        Se = intent.getBooleanExtra("CONFIG_EXTRA_DATA2", true); // 他Activityから受け取る,データがなければtrue
        returnExplanation = intent.getBooleanExtra("RETURN_EXPLANATION_DATA", false); // ExplanationActivityから受け取る,データがなければfalse

        button_status1 = false; // まだ押せる
        button_status2 = false; // まだ押せる

        if(!returnExplanation) setScreenMain(); // タイトルスクリーンの表示
        else if(returnExplanation) setScreenSelect(); // ExplanationActivityからの遷移の場合, 問題説明画面を表示

        musicpath = "title.mp3"; // Bgmの名前
        if(Bgm) audioPlay(); // Bgmの再生
    }

    // Activiyt遷移やアプリの一時停止で実行
    @Override
    protected void onPause() {
        super.onPause();
        if(!button_status1 && !button_status2 && Bgm) audioPause(); // アプリ一時停止の場合, Bgmを一時停止
        if(button_status1 && Bgm || button_status2 && Bgm) audioStop(); // Activity遷移の場合, Bgmを停止, リソースの解放
    }

    // アプリの一時停止から復帰したら行うメソッド
    @Override
    protected void onStart() {
        super.onStart();
        if(Bgm) audioPlay(); // Bgmを再開
    }

    private void setScreenMain(){ // タイトル画面の画面表示
        setContentView(R.layout.activity_main); // タイトル画面の表示

        startText = (TextView)findViewById(R.id.start_txt);
        blinkText(startText, 1000, 500); // 1秒間隔で文字を点滅させる
    }

    private void blinkText(TextView txtView, long duration, long offset){ // 文字点滅メソッド
        Animation anm = new AlphaAnimation(0.0f, 1.0f); // インスタンス生成
        anm.setDuration(duration); // 文字を点滅させる間隔　duration = 1000
        anm.setStartOffset(offset); // 文字の点滅を開始するタイミング offset = 500
        anm.setRepeatMode(Animation.REVERSE); // 表示→消滅→表示→・・・とする
        anm.setRepeatCount(Animation.INFINITE); // 永遠に点滅させる
        txtView.startAnimation(anm); // アニメーションを開始する
    }

    private void setScreenSelect() {
        setContentView(R.layout.activity_select); // 問題選択画面の表示

        // そのIDが振られたウィジェットをコード側で操作をする
        quiz1Button = findViewById(R.id.quiz1_but);
        quiz2Button = findViewById(R.id.quiz2_but);
        quiz3Button = findViewById(R.id.quiz3_but);
        configButton = findViewById(R.id.config_but);
        toExplanationButton = findViewById(R.id.toExplanation_but);
        startButton = findViewById(R.id.start_but);
        // 取得したボタンオブジェクトに、ボタンで発生したイベントを送る先と して、この Activity オブジェクトを指定
        quiz1Button.setOnClickListener(this);
        quiz2Button.setOnClickListener(this);
        quiz3Button.setOnClickListener(this);
        configButton.setOnClickListener(this);
        toExplanationButton.setOnClickListener(this);
        startButton.setOnClickListener(this);

        // そのIDが振られたウィジェットをコード側で操作をする
        quiznameText = findViewById(R.id.Name_txt);
        quizexplanation = findViewById(R.id.quizexplainLayout1);

        if(quiz1mode) { // このクイズが選択されているとき
            quizexplanation.setBackgroundColor(Color.RED); // 背景色を赤に
            quiznameText.setText("サンプルクイズ"); // クイズ名を設定
        }
        else if(quiz2mode) { // このクイズが選択されているとき
            quizexplanation.setBackgroundColor(Color.GREEN); // 背景色を緑に
            quiznameText.setText("四則演算クイズ"); // クイズ名を設定
        }
        else if(quiz3mode) { // このクイズが選択されているとき
            quizexplanation.setBackgroundColor(Color.BLUE); // 背景色を青に
            quiznameText.setText("四字熟語クイズ"); // クイズ名を設定
        }
    }

    public void onClick(View view){
        if(Se) soundPool.play(soundTap, 1.0f, 1.0f, 0, 0, 1); // タップ音を鳴らす
        // このボタンを押したら
        if(view.getId() == R.id.quiz1_but) {
            quiz1Button.startAnimation(AnimationUtils.loadAnimation(this, R.anim.quiz1_btn_on_click)); // 押されたらアニメーションを行う
            quizexplanation.setBackgroundColor(Color.RED); // 背景色を赤に
            // trueのクイズを選択している
            quiz1mode = true;
            quiz2mode = false;
            quiz3mode = false;
            quiznameText.setText("サンプルクイズ"); // クイズ名を表示
        }
        // このボタンを押したら
        else if(view.getId() == R.id.quiz2_but) {
            quiz2Button.startAnimation(AnimationUtils.loadAnimation(this, R.anim.quiz1_btn_on_click)); // 押されたらアニメーションを行う
            quizexplanation.setBackgroundColor(Color.GREEN); // 背景色を緑に
            // trueのクイズを選択している
            quiz1mode = false;
            quiz2mode = true;
            quiz3mode = false;
            quiznameText.setText("四則演算クイズ"); // クイズ名を表示
        }
        // このボタンを押したら
        else if(view.getId() == R.id.quiz3_but) {
            quiz3Button.startAnimation(AnimationUtils.loadAnimation(this, R.anim.quiz1_btn_on_click)); // 押されたらアニメーションを行う
            quizexplanation.setBackgroundColor(Color.BLUE); // 背景色を青に
            // trueのクイズを選択している
            quiz1mode = false;
            quiz2mode = false;
            quiz3mode = true;
            quiznameText.setText("四字熟語クイズ"); // クイズ名を表示
        }
        // このボタンを押したら (一度しか押せないようにする)
        else if(view.getId() == R.id.start_but && !button_status1){
            button_status1 = true; // Activityが多重にならないようにする
            Intent intent = new Intent(MainActivity.this, CountDownActivity.class); // CountDownActiviyに遷移準備
            intent.putExtra("EXTRA_DATA1", quiz1mode); // このクラスのquiz1modeをMainActivityに渡す
            intent.putExtra("EXTRA_DATA2", quiz2mode); // このクラスのquiz2modeをMainActivityに渡す
            intent.putExtra("EXTRA_DATA3", quiz3mode); // このクラスのquiz3modeをMainActivityに渡す
            intent.putExtra("CONFIG_EXTRA_DATA1", Bgm); // このクラスのBgmをMainActivityに渡す
            intent.putExtra("CONFIG_EXTRA_DATA2", Se); // このクラスのSeをMainActivityに渡す
            startActivity(intent); // 遷移開始
        }
        // このボタンを押したら (一度しか押せないようにする)
        else if(view.getId() == R.id.toExplanation_but && !button_status2){
            button_status2 = true; // Activityが多重にならないようにする
            Intent intent = new Intent(MainActivity.this, ExplanationActivity.class); // ExplanationActiviyに遷移準備
            intent.putExtra("EXTRA_DATA1", quiz1mode); // このクラスのquiz1modeを ExplanationActiviy に渡す
            intent.putExtra("EXTRA_DATA2", quiz2mode); // このクラスのquiz2modeを ExplanationActiviy に渡す
            intent.putExtra("EXTRA_DATA3", quiz3mode); // このクラスのquiz3modeを ExplanationActiviy に渡す
            intent.putExtra("CONFIG_EXTRA_DATA1", Bgm); // このクラスのBgmを ExplanationActiviy に渡す
            intent.putExtra("CONFIG_EXTRA_DATA2", Se); // このクラスのSeを ExplanationActiviy に渡す
            startActivity(intent); // 遷移開始
        }
        // このボタンを押したら
        else if(view.getId() == R.id.config_but) {
            setScreenConfig(); // 設定画面を表示
        }
        // このボタンを押したら
        else if(view.getId() == R.id.configfin_but) {
            setScreenSelect(); // 設定画面から問題選択画面を表示
        }
    }

    private void setScreenConfig() {
        setContentView(R.layout.activity_config); // 設定画面の表示
        // そのIDが振られたウィジェットをコード側で操作をする
        bgmSwitch = findViewById(R.id.bgm_swi);
        seSwitch = findViewById(R.id.se_swi);
        // Switchの状態についてBgm, SeがtrueならON, falseならOFF
        bgmSwitch.setChecked(Bgm);
        seSwitch.setChecked(Se);

        configreturnButton = findViewById(R.id.configfin_but); // そのIDが振られたウィジェットをコード側で操作をする
        configreturnButton.setOnClickListener(this); // 取得したボタンオブジェクトに、ボタンで発生したイベントを送る先と して、この Activity オブジェクトを指定

        // bgmSwitchの状態が変化した際のリスナー
        bgmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(bgmSwitch.isChecked()) {
                    // bgmSwitch : Off -> On の時の処理
                    Bgm = true;
                    audioPlay();
                } else {
                    // bgmSwitch : On -> Off の時の処理
                    Bgm = false;
                    audioPause();
                }
            }
        });

        // seSwitchの状態が変化した際のリスナー
        seSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(seSwitch.isChecked()) {
                    // seSwitch : Off -> On の時の処理
                    Se = true;
                } else {
                    // seSwitch : On -> Off の時の処理
                    Se = false;
                }
            }
        });
    }

    //画面がタッチされたらタイトル画面から問題選択画面に画面切り替え
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: // タッチ開始
                info.append("ACTION_DOWN\n");
                info.append("Pressure");
                info.append(motionEvent.getPressure());
                info.append("\n");
                info.append("x1:");
                info.append(motionEvent.getX());
                info.append("\n");
                info.append("y1:");
                info.append(motionEvent.getY());
                info.append("\n\n");

                break;
            case MotionEvent.ACTION_UP: // タッチ終了
                info.append("ACTION_UP\n");
                info.append("x2:");
                info.append(motionEvent.getX());
                info.append("\n");
                info.append("y2:");
                info.append(motionEvent.getY());
                info.append("\n");
                long eventDuration2 = motionEvent.getEventTime() - motionEvent.getDownTime();
                info.append("duration: ");
                info.append(eventDuration2);
                info.append(" msec\n\n");

                break;
            case MotionEvent.ACTION_MOVE: // タッチしたまま指が動かされた
                info.append("ACTION_MOVE\n");

                break;
            case MotionEvent.ACTION_CANCEL: // タッチをキャンセルしたとき
                info.append("ACTION_CANCEL\n");

                break;
        }

        setScreenSelect(); // 問題選択画面を表示

        return false;
    }
    // BGMの読み込み
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean audioSetup(){
        boolean fileCheck = false; // 正常にMediaPlayerにデータが入ればtrue

        // インスタンスを生成
        titleMusic = new MediaPlayer();

        // assetsのmusicディレクトリから mp3 ファイルを読み込み
        try(AssetFileDescriptor afdescripter = getAssets().openFd("Bgm/" + musicpath);)
        {
            // MediaPlayerに読み込んだ音楽ファイルを指定
            titleMusic.setDataSource(afdescripter.getFileDescriptor(),
                    afdescripter.getStartOffset(),
                    afdescripter.getLength());
            // 音量調整を端末のボタンに任せる
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            // ループ再生の設定
            titleMusic.setLooping(true);
            titleMusic.prepare(); // 準備が整うまでstart()しないように
            fileCheck = true; // 正常に読み込みが行われた
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return fileCheck; // trueなら再生可能
    }
    // 再生
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void audioPlay() {

        if (titleMusic == null) { // データがない場合
            // audio ファイルを読出し
            if (audioSetup()){ // 返り値がtrueなら
                Toast.makeText(getApplication(), "Rread audio file", Toast.LENGTH_SHORT).show();
            }
            else{ // 返り値がfalseなら
                Toast.makeText(getApplication(), "Error: read audio file", Toast.LENGTH_SHORT).show();
                return; // メソッド強制終了
            }
        }
        // 再生する
        titleMusic.start();
    }
    // 停止
    private void audioStop() {
        // 再生終了
        titleMusic.stop();
        // リセット
        titleMusic.reset();
        // リソースの解放
        titleMusic.release();
        // データを空に
        titleMusic = null;
    }
    // 一時停止
    private void audioPause() { titleMusic.pause();}

}
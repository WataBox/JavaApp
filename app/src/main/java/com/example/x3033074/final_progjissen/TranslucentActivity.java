package com.example.x3033074.final_progjissen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/*
問題の結果と答え、解説を表示するクラス
半透明のActivityを背景としたクラス
 */
public class TranslucentActivity extends AppCompatActivity implements View.OnClickListener {

    private Button nextButton; // 次の問題に移動するための変数
    private TextView correctnessText, explanationText; // 正解か不正解かを表示するTextView
    private boolean quiz1mode; // 他Activityからのデータ,quiz1modeを受け取るための変数
    private boolean quiz2mode; // 他Activityからのデータ,quiz2modeを受け取るための変数
    private boolean quiz3mode; // 他Activityからのデータ,quiz3modeを受け取るための変数
    private boolean Bgm; // 他Activityからのデータ,Bgmを受け取るための変数
    private boolean Se; // 他Activityからのデータ,Seを受け取るための変数
    private boolean correctness; // 他Activityからのデータ,correctnessを受け取るための変数
    private boolean button_status; // 一度しか押せないようにする
    private int quiznum; // 他Activityからのデータ,quiznumを受け取るための変数
    private int point; // 他Activityからのデータ,pointを受け取るための変数
    private String explanation; // 問題の解説テキストを読み込むための変数
    private boolean isquizarray; // 参照する問題番号(CSVファイルのIDは関係なし)を格納した配列の有無
    private int[] referquizarray; // 参照する問題番号を格納した配列

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translucent); // 指定のレイアウトを表示

        button_status = false; // ボタンが押せる状態

        Intent intent = getIntent(); // Intent を取得する
        quiz1mode = intent.getBooleanExtra("EXTRA_DATA1", false); // 他Activityから受け取る,データがなければfalse
        quiz2mode = intent.getBooleanExtra("EXTRA_DATA2", false); // 他Activityから受け取る,データがなければfalse
        quiz3mode = intent.getBooleanExtra("EXTRA_DATA3", false); // 他Activityから受け取る,データがなければfalse
        point = intent.getIntExtra("EXTRA_DATA4", 0); // 他Activityから受け取る,データがなければ0
        quiznum = intent.getIntExtra("QUIZ_NUM", 1); // 他Activityから受け取る,データがなければ1
        correctness = intent.getBooleanExtra("CORRECTNESS_DATA", false); // 他Activityから受け取る,データがなければfalse
        Bgm = intent.getBooleanExtra("CONFIG_EXTRA_DATA1", true); // 他Activityから受け取る,データがなければtrue
        Se = intent.getBooleanExtra("CONFIG_EXTRA_DATA2", true); // 他Activityから受け取る,データがなければtrue
        explanation = intent.getStringExtra("EXPLANATION"); // 他Activityから受け取る
        isquizarray = intent.getBooleanExtra("ISARRAY", false); // 他Activityから受け取る,データがなければfalse
        referquizarray = intent.getIntArrayExtra("QUIZARRAY"); // 他Activityから受け取る

        explanationText = (TextView)findViewById(R.id.explanation_txt); // そのIDが振られたウィジェットをコード側で操作をする
        explanationText.setText(explanation); // 解説テキストを表示させる
        correctnessText = (TextView)findViewById(R.id.correctness_txt); // そのIDが振られたウィジェットをコード側で操作をする
        if(correctness) correctnessText.setText("正解！"); // correctnessがtrueのときの表示
        else correctnessText.setText("不正解・・・"); // correctnessがfalseのときの表示
        nextButton = (Button)findViewById(R.id.next_but); // そのIDが振られたウィジェットをコード側で操作をする
        nextButton.setOnClickListener(this); // 取得したボタンオブジェクトに、ボタンで発生したイベントを送る先と して、この Activity オブジェクトを指定
        if(quiz1mode){
            if(quiznum == 4) nextButton.setText("結果発表！"); // 最終問題まで終わったら結果発表へいくことを表示
            else nextButton.setText("次の問題"); // 問題が途中なら次の問題へいくことを表示
        }
        else {
            if (quiznum == 6) nextButton.setText("結果発表！"); // 最終問題まで終わったら結果発表へいくことを表示
            else nextButton.setText("次の問題"); // 問題が途中なら次の問題へいくことを表示
        }
    }

    @Override
    public void onClick(View v) {
        if(!button_status) {
            button_status = true; // これでActivityが多重にかからないようにする
            if(quiz1mode){
                if (quiznum == 4) { // 最終問題が終わったらResultActivityへ遷移
                    Intent intent = new Intent(this, ResultActivity.class); // ResultActivityに遷移準備
                    intent.putExtra("EXTRA_DATA1", quiz1mode); // このクラスのquiz1modeをResultActivityに渡す
                    intent.putExtra("EXTRA_DATA2", quiz2mode); // このクラスのquiz2modeをResultActivityに渡す
                    intent.putExtra("EXTRA_DATA3", quiz3mode); // このクラスのquiz3modeをResultActivityに渡す
                    intent.putExtra("EXTRA_DATA4", point);// このクラスのpointをResultActivityに渡す
                    intent.putExtra("CONFIG_EXTRA_DATA1", Bgm); // このクラスのBgmをResultActivityに渡す
                    intent.putExtra("CONFIG_EXTRA_DATA2", Se); // このクラスのSeをResultActivityに渡す
                    startActivity(intent); // Activityを開始する
                }
                else { // 問題が続いている場合 QuizActivity　に戻る
                    Intent intent = new Intent(this, QuizActivity.class);// QuizActivityに遷移準備
                    intent.putExtra("EXTRA_DATA1", quiz1mode); // このクラスのquiz1modeをQuizActivityに渡す
                    intent.putExtra("EXTRA_DATA2", quiz2mode); // このクラスのquiz2modeをQuizActivityに渡す
                    intent.putExtra("EXTRA_DATA3", quiz3mode); // このクラスのquiz3modeをQuizActivityに渡す
                    intent.putExtra("EXTRA_DATA4", point);// このクラスのpointをQuizActivityに渡す
                    intent.putExtra("QUIZ_NUM", quiznum); // このクラスのquiznumをQuizActivityに渡す
                    intent.putExtra("CONFIG_EXTRA_DATA1", Bgm); // このクラスのBgmをQuizActivityに渡す
                    intent.putExtra("CONFIG_EXTRA_DATA2", Se); // このクラスのSeをQuizActivityに渡す
                    intent.putExtra("ISARRAY", isquizarray); // このクラスのisquizarrayをQuizActivityに渡す
                    intent.putExtra("QUIZARRAY", referquizarray); // このクラスのreferquizarrayをQuizActivityに渡す
                    startActivity(intent); // Activityを開始する
                }
            }
            else {
                if (quiznum == 6) { // 最終問題が終わったらResultActivityへ遷移
                    Intent intent = new Intent(this, ResultActivity.class); // ResultActivityに遷移準備
                    intent.putExtra("EXTRA_DATA1", quiz1mode); // このクラスのquiz1modeをResultActivityに渡す
                    intent.putExtra("EXTRA_DATA2", quiz2mode); // このクラスのquiz2modeをResultActivityに渡す
                    intent.putExtra("EXTRA_DATA3", quiz3mode); // このクラスのquiz3modeをResultActivityに渡す
                    intent.putExtra("EXTRA_DATA4", point);// このクラスのpointをResultActivityに渡す
                    intent.putExtra("CONFIG_EXTRA_DATA1", Bgm); // このクラスのBgmをResultActivityに渡す
                    intent.putExtra("CONFIG_EXTRA_DATA2", Se); // このクラスのSeをResultActivityに渡す
                    startActivity(intent); // Activityを開始する
                }
                else {
                    Intent intent = new Intent(this, QuizActivity.class); // QuizActivityに遷移準備
                    intent.putExtra("EXTRA_DATA1", quiz1mode); // このクラスのquiz1modeをQuizActivityに渡す
                    intent.putExtra("EXTRA_DATA2", quiz2mode); // このクラスのquiz2modeをQuizActivityに渡す
                    intent.putExtra("EXTRA_DATA3", quiz3mode); // このクラスのquiz3modeをQuizActivityに渡す
                    intent.putExtra("EXTRA_DATA4", point); // このクラスのpointをQuizActivityに渡す
                    intent.putExtra("QUIZ_NUM", quiznum); // このクラスのquiznumをQuizActivityに渡す
                    intent.putExtra("CONFIG_EXTRA_DATA1", Bgm); // このクラスのBgmをQuizActivityに渡す
                    intent.putExtra("CONFIG_EXTRA_DATA2", Se); // このクラスのSeをQuizActivityに渡す
                    intent.putExtra("ISARRAY", isquizarray); // このクラスのisquizarrayをQuizActivityに渡す
                    intent.putExtra("QUIZARRAY", referquizarray); // このクラスのreferquizarrayをQuizActivityに渡す
                    startActivity(intent); // Activityを開始する
                }
            }
        }
    }
}

package com.example.x3033074.final_progjissen;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/*
CSVファイルを読み込むクラス
読み込んだ内容はItemEntityクラス配列を生成し格納していく
 */
public class CsvReader {

    List<ItemEntity> objects = new ArrayList<ItemEntity>(); // CSVファイルのそれぞれの属性（列）についてのクラス型のリスト
    private boolean quiz1mode; //　指定のCSVファイルを読み込むための変数
    private boolean quiz2mode; //　指定のCSVファイルを読み込むための変数
    private boolean quiz3mode; //　指定のCSVファイルを読み込むための変数
    String csvfilePath; // CSVファイルの名前

    public CsvReader(boolean quiz1mode, boolean quiz2mode, boolean quiz3mode){
        this.quiz1mode = quiz1mode; // このクラスのquiz1modeに, 受け取ったquiz1modeを入れる
        this.quiz2mode = quiz2mode; // このクラスのquiz2modeに, 受け取ったquiz2modeを入れる
        this.quiz3mode = quiz3mode; // このクラスのquiz3modeに, 受け取ったquiz3modeを入れる
        if(quiz1mode) csvfilePath = "sample_quiz1.csv"; // サンプルクイズを選択した場合に読み込むCSVファイル
        else if(quiz2mode) csvfilePath = "keisan_quiz.csv"; // 四則演算クイズを選択した場合に読み込むCSVファイル
        else if(quiz3mode) csvfilePath = "yozizyukugo_quiz.csv"; // 四字熟語クイズを選択した場合に読み込むCSVファイル
    }
    public void reader(Context context) {
        AssetManager assetManager = context.getResources().getAssets(); // assetsのmusicディレクトリから mp3 ファイルを読み込み
        try {
            // CSVファイルの読み込み
            InputStream inputStream = assetManager.open("quiz_csv/" + csvfilePath);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferReader = new BufferedReader(inputStreamReader);
            String line;
            // 読み込むものがなくなるまで読み込み続ける
            while ((line = bufferReader.readLine()) != null) {

                // カンマ区切りで１つづつ配列に入れる
                ItemEntity data = new ItemEntity();
                String[] RowData = line.split(",");

                // CSVの左([0]番目)から順番にセット
                data.setId(RowData[0]);
                data.setQuiz(RowData[1]);
                data.setAnswer(RowData[2]);
                data.setExplanation(RowData[3]);

                objects.add(data); // クラス型の配列の格納
            }
            // ちゃんと読み込むことが出来ているか確認用
            for (int i=0; i<objects.size(); ++i)
            {
                Log.i("csvReader", "読み込んだ問題コード:" + objects.get(i).getId());
                Log.i("csvReader", "読み込んだ問題:" + objects.get(i).getQuiz());
                Log.i("csvReader", "読み込んだ解答:" + objects.get(i).getAnswer());
                Log.i("csvReader", "読み込んだ解説:" + objects.get(i).getExplanation());
            }
            bufferReader.close(); // ストリームを閉じて, それに関連するすべてのシステムリソースを解放する
        } catch (IOException e) { // 例外処理
            e.printStackTrace();
        }
    }
}

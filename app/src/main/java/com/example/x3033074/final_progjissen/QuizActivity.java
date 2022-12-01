package com.example.x3033074.final_progjissen;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

/*
クイズ画面を表示するクラス
tess-twoを用いた文字認識機能の実現
 */
public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

    private Button recognizeButton; // 文字認識を行うButtonクラス
    private Button clearButton; // 描画内容を消去するButtonクラス
    private Button answerButton; // 正誤を判定するButtonクラス

    private TextView recoginizeText; // 文字認識した結果の読み取った文字を表示するTextViewクラス
    private TextView quizText; // 問題文を表示するTextView

    private CsvReader csvReader; // CSVファイル（問題文） を読み込むためのクラス

    private boolean quiz1mode, quiz2mode, quiz3mode; // 読み込む問題の種類を判定するために受け取る変数
    private boolean Bgm, Se; // Bgm, Seの有無を判定する変数
    private boolean correctness; // 答えがあっていれば true , 間違っていれば false を返す
    private boolean button_status; // Activityを遷移するようなボタン(answerボタン)を一度しか押せないようにする

    private SoundPool soundPool; // Seを再生するためのクラス
    private int soundCorrect, soundIncorrect, soundEraser, soundRecognize; // Seをロードするための変数

    private int point; // 獲得した点数を格納する, 正解すると +20, 不正解だと -10
    private int quiznum; // 現在何問目かをカウントするための変数

    private MediaPlayer thinkingMusic; // Bgmを再生するためのクラス
    String musicpath; // Bgmの名前

    private int[] referquizarray; // CSVファイルにおけるに何行目の問題を参照するかを格納する配列 (EX:referquizarray[0] = 3 なら)
    private boolean isquizarray; // 既に参照する問題が決まっているならtrue, 決まっていないならfalse

    String datapath = ""; // tess-twoに関するデータパス
    String explanation; // TextViewに問題解説のテキストを表示させるための変数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz); // クイズ画面を表示する

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

        // correct.mp3 をロードしておく
        soundCorrect = soundPool.load(this, R.raw.correct, 1);

        // incorrct.mp3 をロードしておく
        soundIncorrect = soundPool.load(this, R.raw.incorrect, 1);

        //eraser.mp3　をロードしておく
        soundEraser = soundPool.load(this, R.raw.eraser, 1);

        //recognize.mp3　をロードしておく
        soundRecognize = soundPool.load(this, R.raw.recognize, 1);

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
        quiz1mode = intent.getBooleanExtra("EXTRA_DATA1", false); // 他Activityから受け取る,データがなければfalse
        quiz2mode = intent.getBooleanExtra("EXTRA_DATA2", false); // 他Activityから受け取る,データがなければfalse
        quiz3mode = intent.getBooleanExtra("EXTRA_DATA3", false);// 他Activityから受け取る,データがなければfalse
        quiznum = intent.getIntExtra("QUIZ_NUM", 1); // 他Activityから受け取る, データがなければ1
        point = intent.getIntExtra("EXTRA_DATA4", 0); // 他Activityから受け取る, データがなければ0
        Bgm = intent.getBooleanExtra("CONFIG_EXTRA_DATA1", true); // 他Activityから受け取る,データがなければtrue
        Se = intent.getBooleanExtra("CONFIG_EXTRA_DATA2", true); // 他Activityから受け取る,データがなければtrue
        isquizarray = intent.getBooleanExtra("ISARRAY", false); // 他Activityから受け取る, データがなければfalse

        csvReader = new CsvReader(quiz1mode, quiz2mode, quiz3mode); //csvReaderのインスタンスを生成
        csvReader.reader(getApplicationContext()); // csvファイルの読み込みを行うメソッドを実行

        // 参照する問題が決まっている場合, 他Activityからの参照する問題の配列を受け取る
        // 参照する問題が決まっていない場合, サンプルクイズの場合3問, その他のクイズは5問, 出題する
        if(isquizarray) referquizarray = intent.getIntArrayExtra("QUIZARRAY");
        else if(!isquizarray) {
            if(quiz1mode) referquizarray = new int[3]; // サンプルクイズの場合参照するクイズは3問
            else if(quiz2mode || quiz3mode) referquizarray = new int[5]; // その他のクイズの場合参照するクイズは5問
            referquizarray = setArray(referquizarray, csvReader); // 出題する問題をランダムにセットするためのメソッドを実行
            isquizarray = true; // 出題する問題が決まればここの分岐に入らないように変数をtrueに設定.
        }

        musicpath = "thinking.mp3"; // Bgmの名前
        if(Bgm) audioPlay(); // Bgmがオンなら再生

        // そのIDが振られたウィジェットをコード側で操作をする
        recognizeButton = (Button) findViewById(R.id.recognize_but);
        clearButton = (Button) findViewById(R.id.clear_but);
        answerButton = (Button) findViewById(R.id.answer_but);
        // 取得したボタンオブジェクトに、ボタンで発生したイベントを送る先と して、この Activity オブジェクトを指定
        recognizeButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        answerButton.setOnClickListener(this);

        // そのIDが振られたウィジェットをコード側で操作をする
        recoginizeText = (TextView) findViewById(R.id.recognize_txt);
        quizText = (TextView) findViewById(R.id.quiz_txt);
        // quiznum問目の問題を表示する
        quizText.setText(csvReader.objects.get(referquizarray[quiznum-1]).getQuiz());
    }

    // クイズのランダム出題メソッド
    private int[] setArray(int[] referquizarray, CsvReader csvReader) {
        ArrayList<Integer> list = new ArrayList<Integer>(); // int型リストを用意
        // CSVファイルに格納されている問題数を参照し, 0 ~ 問題数-1　までの数字をリストに追加 (参照するときの添え字は 0 からなので)
        for (int i = 0; i < csvReader.objects.size(); i++) list.add(i);
        Collections.shuffle(list); // リストの中身をシャッフルさせる (ランダム化)
        // シャッフルさせた問題番号の入ったリストを先頭から参照して, 実際に用いる配列に格納していく
        for (int i = 0; i < referquizarray.length; i++) referquizarray[i] = list.get(i);
        // 配列を返す
        return referquizarray;
    }

    // アプリを離れる際や, Intentで遷移する際に行うメソッド
    @Override
    protected void onPause() {
        super.onPause();
        if(!button_status && Bgm) audioPause(); // アプリを離れる際は音源を一時停止
        if(button_status && Bgm) audioStop(); // IntentでActivity遷移する際は音源の停止, リソースを開放
    }

    // アプリを再開した際の挙動
    @Override
    protected void onStart() {
        super.onStart();
        if(Bgm && !button_status) audioPlay(); // 一時停止から再生させる
    }

    @Override
    public void onClick(View view) {
        // 消しゴムボタン
        if(view.getId() == R.id.clear_but) {
            if(Se) soundPool.play(soundEraser, 1.0f, 1.0f, 0, 0, 1); // 消しゴムを消す音

            AnswerCanvas answerView = (AnswerCanvas) findViewById(R.id.draw_view); // そのIDが振られたウィジェットをコード側で操作をする
            answerView.clear(); // 描画内容を消去する

            recoginizeText.setText(""); // 認識結果を空白に
        }
        // 文字認識ボタン
        else if(view.getId() == R.id.recognize_but) {
                Recognize(); // 文字認識メソッド
        }
        // 解答ボタン
        else if(view.getId() == R.id.answer_but){
            // 文字認識していない時点で解答ボタンを押した場合の動作
            if(recoginizeText.getText().equals("") && !button_status) {
                Context context = getApplicationContext(); // アプリケーションに関する様々な情報へのインタフェース
                CharSequence text = "文字を認識させてください"; // Toastで表示するテキスト
                int duration = Toast.LENGTH_SHORT; // 短い時間で表示
                // 上記の情報でToastを表示　
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            // 文字認識をしている時点で解答ボタンを押した場合の動作
            else if(!recoginizeText.getText().equals("") && !button_status) {
                button_status = true; // 一度押したら, この分岐に入らずActivityが多重にかかることを防ぐ

                // 文字認識結果とCSVファイルに格納される模範解答を比較して一致した場合の挙動
                if (recoginizeText.getText().equals(csvReader.objects.get(referquizarray[quiznum-1]).getAnswer())) {
                    correctness = true; // 正解であることを次の Activity に伝えるための変数
                    point += 20; // 点数を20点追加
                    if(Se) soundPool.play(soundCorrect, 1.0f, 1.0f, 0, 0, 1); // 正解音を鳴らす
                }
                // 文字認識結果とCSVファイルに格納される模範解答を比較して違った場合の挙動
                else {
                    correctness = false; // 不正解であることを次の Activity に伝えるための変数
                    point -= 10; // 点数を10点減点
                    if(point <= 0) point = 0; // 減点によって点数が 0 を下回ったら 0 固定にする
                    if(Se) soundPool.play(soundIncorrect, 1.0f, 1.0f, 1, 0, 1); // 不正解音を鳴らす
                }

                // 次の Activity に今回の問題の解説 (String) を渡す
                explanation = csvReader.objects.get(referquizarray[quiznum-1]).getExplanation();
                Log.i("csvReader", "解説:" + explanation); // 確認用

                Intent intent = new Intent(this, TranslucentActivity.class); // Activity遷移のためのクラス
                quiznum += 1; // 次の問題を参照するため
                intent.putExtra("EXTRA_DATA1", quiz1mode); // このクラスの quiz1mode を TranslucentActivity に渡す
                intent.putExtra("EXTRA_DATA2", quiz2mode); // このクラスの quiz2mode を TranslucentActivity に渡す
                intent.putExtra("EXTRA_DATA3", quiz3mode); // このクラスの quiz3mode を TranslucentActivity に渡す
                intent.putExtra("EXTRA_DATA4", point);// このクラスの point を TranslucentActivity に渡す
                intent.putExtra("QUIZ_NUM", quiznum); // このクラスの quiznum を TranslucentActivity に渡す
                intent.putExtra("CORRECTNESS_DATA", correctness); // このクラスの correctness を TranslucentActivity に渡す
                intent.putExtra("CONFIG_EXTRA_DATA1", Bgm); // このクラスの Bgm を TranslucentActivity に渡す
                intent.putExtra("CONFIG_EXTRA_DATA2", Se); // このクラスの Se を TranslucentActivity に渡す
                intent.putExtra("EXPLANATION", explanation); // このクラスの explanation を TranslucentActivity に渡す
                intent.putExtra("ISARRAY", isquizarray); // このクラスの isquizarray を TranslucentActivity に渡す
                intent.putExtra("QUIZARRAY", referquizarray); // このクラスの referquizarrayを TranslucentActivity に渡す
                startActivity(intent); // 遷移開始
            }
        }
    }

    private void Recognize() {
        View drewview = (View) findViewById(R.id.draw_view);      // 描いた絵をdrewviewとして読み込む
        drewview.setDrawingCacheEnabled(true);        // キャッシュを取得する設定にする
        drewview.destroyDrawingCache();               // 既存のキャッシュをクリアする
        Bitmap bmp = drewview.getDrawingCache();      // drewviewをbitmap bmpに変換
        bmp = bmp.copy(Bitmap.Config.ARGB_8888, true); // ARGB_8888に合わせる
        String language = null; // 言語の変数
        if(quiz1mode) {
            language = "jpn";  // tess-two言語の選択 (日本語)
        }
        if(quiz2mode) {
            language = "eng";  // tess-two言語の選択 (英語)
        }
        if(quiz3mode) {
            language = "jpn";  // tess-two言語の選択 (日本語)
        }
        datapath = getFilesDir() + "/tesseract/";     // directoryの指定
        checkFile(new File(datapath + "tessdata/"), language);  // 学習データの有無チェック

        TessBaseAPI tessOCRAPI = new TessBaseAPI();   // Init OCR
        tessOCRAPI.init(datapath, language);

        tessOCRAPI.setImage(bmp);                     // bmp型にした描いた絵をセットする

        String APIresult;
        APIresult = tessOCRAPI.getUTF8Text();          // OCRの結果をString型のAPIkekkaに
        tessOCRAPI.end();                             // Close OCR API

        recoginizeText.setText(APIresult);         // APIkekkaをtextviewに表示
        if(Se) soundPool.play(soundRecognize, 1.0f, 1.0f, 0, 0, 1); // 認識音を鳴らす
        Log.d(APIresult, "結果表示");                   // Logに結果表示
    }

    private void checkFile(File dir, String language) {
        // ディレクトリが存在しないなら, ディレクトリを作成し言語ファイルをディレクトリ内にコピー
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles(language);
        }
        // ディレクトリが存在するなら
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/" + language + ".traineddata"; // ファイルのパス
            File datafile = new File(datafilepath); // 指定したパスのファイルのクラスをインスタンス化
            // 言語データファイルがなければ, 再びファイル生成
            if (!datafile.exists()) {
                copyFiles(language);
            }
        }
    }

    private void copyFiles(String language) {
        try {
            String filepath = datapath + "/tessdata/" + language + ".traineddata"; // コピーファイルのパスの指定
            AssetManager assetManager = getAssets(); // assetsフォルダの中から参照

            InputStream instream = assetManager.open("tessdata/" + language +".traineddata"); // ファイルから入力ストリームを作る
            OutputStream outstream = new FileOutputStream(filepath); // バイトを書き込む出力ストリームを作る

            byte[] buffer = new byte[1024]; // 出力バイト
            // バイトでファイルをコピー
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read); // 指定されたバイト配列の、オフセット位置 off から始まる readバイト をこの出力ストリームに書き込む
            }


            outstream.flush(); // 出力ストリームをフラッシュして、バッファリングされていたすべての出力バイトを強制的に書き込む
            outstream.close(); // 出力ストリームを閉じ、このストリームに関連するすべてのシステムリソースを解放する
            instream.close(); // この入力ストリームを閉じて、そのストリームに関連するすべてのシステムリソースを解放する

            File file = new File(filepath); // ファイルが存在しているかを確認するための変数
            // ファイルが存在しない場合の例外処理
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) { // 例外処理
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Bgmの読み込みとその他設定
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean audioSetup(){
        boolean fileCheck = false; // 正常にMediaPlayerにデータが入ればtrue

        // インスタンスを生成
        thinkingMusic = new MediaPlayer();

        // assetsのmusicディレクトリから mp3 ファイルを読み込み
        try(AssetFileDescriptor afdescripter = getAssets().openFd("Bgm/" + musicpath);)
        {
            // MediaPlayerに読み込んだ音楽ファイルを指定
            thinkingMusic.setDataSource(afdescripter.getFileDescriptor(),
                    afdescripter.getStartOffset(),
                    afdescripter.getLength());
            // 音量調整を端末のボタンに任せる
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            // ループ再生の設定
            thinkingMusic.setLooping(true);
            thinkingMusic.prepare(); // 準備が整うまでstart()しないように
            fileCheck = true; // 正常に読み込みが行われた
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return fileCheck; // trueなら再生可能
    }

    // Bgmの再生
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void audioPlay() {

        if (thinkingMusic == null) { // データがない場合
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
        thinkingMusic.start();
    }

    // Bgmの停止
    private void audioStop() {
        // 再生終了
        thinkingMusic.stop();
        // リセット
        thinkingMusic.reset();
        // リソースの解放
        thinkingMusic.release();
        // データを空に
        thinkingMusic = null;
    }

    // Bgmの一時停止
    private void audioPause() { thinkingMusic.pause();}

}

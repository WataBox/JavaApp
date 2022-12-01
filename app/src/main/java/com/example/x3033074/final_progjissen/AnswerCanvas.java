package com.example.x3033074.final_progjissen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/*
クイズの解答を書くためのキャンバスクラス
 */
public class AnswerCanvas extends View {

    private Paint paint; // 描画方法の設定を行うクラス
    private Path path; // タッチ操作された座標（場所）を記憶するクラス

    public AnswerCanvas(Context context){
        this(context,null);
    }

    public AnswerCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        path = new Path(); // インスタンス生成
        paint = new Paint(); // インスタンス生成
        paint.setColor(0xFF000000); // 線の色を黒に設定
        paint.setStyle(Paint.Style.STROKE); // 描画スタイルは線
        // フチに丸みを帯びさせる
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        // フチの幅
        paint.setStrokeWidth(10);

    }
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint); // pathとpaintを参照して, キャンバスに描画
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(); // float型でx座標を取得する
        float y = event.getY(); // float型でy座標を取得する

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // タッチ開始
                path.moveTo(x, y); // パスの開始地点を決める
                invalidate(); // 再描画通知
                break;
            case MotionEvent.ACTION_MOVE:  // タッチしたまま指が動かされた
                path.lineTo(x, y); // 描画するポイントを追加
                invalidate(); // 再描画通知
                break;
            case MotionEvent.ACTION_UP: // タッチ終了
                path.lineTo(x, y); // 描画するポイントを追加
                invalidate(); // 再描画通知
                break;
        }
        return true;
    }
    public void clear(){
        path.reset(); // パスをリセットしてから新しい座標をセット
        invalidate(); // 再描画通知
    }
}
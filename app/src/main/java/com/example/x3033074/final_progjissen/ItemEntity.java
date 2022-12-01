package com.example.x3033074.final_progjissen;

/*
CSVファイルの読み込んだ内容を格納するクラス
それぞれID, 問題文, 答え, 解説を格納している
これを行の数だけインスタンスを作成する
つまりItemEntity[0]はCSVファイル1行目のID, 問題文, 答え, 解説を格納している
 */
public class ItemEntity {

    String id; // CSVファイルのID (当初使う予定だったが使わなくなった)
    String quiz; // CSVファイルの問題文
    String answer; // CSVファイルの答
    String explanation; // CSVファイルの解説

    // IDを設定するメソッド
    public void setId(String id) { this.id = id; }

    // 設定したIDを取得するメソッド
    public String getId(){
        return id;
    }

    // 問題文を設定するメソッド
    public void setQuiz(String quiz) { this.quiz = quiz; }

    // 設定した問題文を取得するメソッド
    public String getQuiz(){
        return quiz;
    }

    // 答えを設定するメソッド
    public void setAnswer(String answer) { this.answer = answer; }

    // 設定した答えを取得するメソッド
    public String getAnswer() { return answer; }

    // 解説を設定するメソッド
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    // 設定した解説を取得するメソッド
    public String getExplanation() {
        return explanation;
    }

}

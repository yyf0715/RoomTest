package com.example.words;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
//@Entity实体类
public class Word {
    @PrimaryKey(autoGenerate = true)
//    @PrimaryKey标记为主键 autoGenerate = true 自动生成
    private int id;

    @ColumnInfo(name = "english_word")
//    @ColumnInfo标记为一个列 name = "english_word"定义列名
    private String word;
    @ColumnInfo(name = "chinese_meaning")
    private String chineseMeaning;
    @ColumnInfo(name = "chinese_invisible")
    private boolean chinese_invisible;

    public boolean isChinese_invisible() {
        return chinese_invisible;
    }

    public void setChinese_invisible(boolean chinese_invisible) {
        this.chinese_invisible = chinese_invisible;
    }

    public Word(String word, String chineseMeaning) {
        this.word = word;
        this.chineseMeaning = chineseMeaning;
    }

    public String getWord() {
        return word;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getChineseMeaning() {
        return chineseMeaning;
    }

    public void setChineseMeaning(String chineseMeaning) {
        this.chineseMeaning = chineseMeaning;
    }
}

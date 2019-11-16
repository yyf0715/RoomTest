package com.example.words;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao//DataBase access object
//@Dao 是一个访问数据库的接口

public interface WordDao {
    @Insert
    void insertWords(Word...words);

    @Update
    void updateWords(Word...words);

    @Delete
    void deleteWords(Word...words);

    @Query("DELETE FROM WORD")//删除全部
    void deleteAllWords();

    @Query("SELECT * FROM WORD ORDER BY ID DESC")//查询，返回所有的内容，降序，最新记录放在前边
//    List<Word> getAllWords();
    LiveData<List<Word>> getAllWordsLive();
//    LiveData可观察的  运行时，系统会自动将其放在子线程，所以不用自己设置AsnycTask
}

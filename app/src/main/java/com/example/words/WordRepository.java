package com.example.words;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class WordRepository {
//创建一个库，用来存储和读取数据
    private LiveData<List<Word>>allWordsLive;
    private WordDao wordDao;

    LiveData<List<Word>> getAllWordsLive() {
        return allWordsLive;
    }
    LiveData<List<Word>> findWordsWithPattern(String pattern){
        return wordDao.findWordsWithPattern("%"+pattern+"%");//添加通配符,才算模糊匹配
    }
    WordRepository( Context context) {
        WordDataBase wordDataBase = WordDataBase.getDataBase(context.getApplicationContext());
        wordDao = wordDataBase.getWordDao();
        allWordsLive = wordDao.getAllWordsLive();//获取数据
    }
    void insertWords(Word...words){
        new InsertAsyncTask(wordDao).execute(words);//execute()执行
    }
    void updateWords(Word...words){
        new UpdateAsnycTask(wordDao).execute(words);//execute()执行
    }
    void deleteWords(Word...words){
        new DeleteAsnycTask(wordDao).execute(words);//execute()执行
    }
    void deleteAllWords(){
        new DeleteAllAsnycTask(wordDao).execute();//execute()执行
    }



    //在使用数据库时操作使用
    //    必须为static，否者会报警告
    static class InsertAsyncTask extends AsyncTask<Word, Void, Void> {//Void不声明,不使用
        private WordDao wordDao;

        InsertAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {//插入操作放到后台去执行
            wordDao.insertWords(words);
            return null;
        }
/*
        @Override
        protected void onPostExecute(Void aVoid) {//任务完成后呼叫
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Void... values) {//进度发生改变时呼叫
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {//后台任务执行前会进行操作
            super.onPreExecute();
        }*/
    }
    static class UpdateAsnycTask extends AsyncTask<Word,Void,Void>{
        private WordDao wordDao;

        UpdateAsnycTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.updateWords(words);
            return null;
        }
    }
    static class DeleteAsnycTask extends AsyncTask<Word,Void,Void>{
        private WordDao wordDao;

        DeleteAsnycTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.deleteWords(words);
            return null;
        }
    }
    static class DeleteAllAsnycTask extends AsyncTask<Void,Void,Void>{
        private WordDao wordDao;

        public DeleteAllAsnycTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Void...voids) {
            wordDao.deleteAllWords();
            return null;
        }
    }
}

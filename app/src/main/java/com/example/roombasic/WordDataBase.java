package com.example.roombasic;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Word.class},version = 5,exportSchema = false)
public abstract class WordDataBase extends RoomDatabase {
    //抽象方法，不需要我们实现

    //singleton 只允许生一个实例
    //完成singleton设计模式
    private static WordDataBase INSTANCE;
    static  synchronized WordDataBase getDataBase(Context context){
        if (INSTANCE == null){
            //context返回应用程序的根节点
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),WordDataBase.class,"word")
//                    .allowMainThreadQueries()//强制在主线程使用,一般不建议使用
//                    .fallbackToDestructiveMigration()//破坏式的迁移（清空数据库重新创建一个新的）
//                    .addMigrations(MIGRATION_3_4)
                    .build();
            //获取WordDataBase一个对象,需要用到databaseBuilder（）;静态函数获取  呼叫build()；来创建
//        //该数据库不能在主线程使用
        }
        return INSTANCE;
    }
    public abstract WordDao getWordDao();
    //若有多个Entities，则应该写多个Dao
    static final Migration MIGRATION_2_3 = new Migration(2,3) {//添加数据库表的列
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE word ADD COLUMN bar_data INTEGER NOT NULL DEFAULT 1");
        }
    };
    static final Migration MIGRATION_3_4 = new Migration(3,4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE word_temp (id INTEGER PRIMARY KEY NOT NULL,english_word TEXT" +
                    ",chinese_meaning TEXT)");//创建一个新的表
            database.execSQL("INSERT INTO word_temp(id,english_word,chinese_meaning) SELECT" +
                    " id,english_word,chinese_meaning FROM word ");
            //提取word表中的数据，复制到word_temp表中
            database.execSQL("DROP TABLE word");//删除word
            database.execSQL("ALTER TABLE word_temp RENAME TO word ");//修改名字
        }
    };
    static final Migration MIGRATION_4_5 = new Migration(4,5) {//添加数据库表的列
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE word ADD COLUMN chinese_invisible INTEGER NOT NULL DEFAULT 0");
            //没有布尔值
        }
    };
}

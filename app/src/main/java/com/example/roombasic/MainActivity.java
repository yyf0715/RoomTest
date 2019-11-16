package com.example.roombasic;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button buttonInset,buttonClear;
    TextView textView;
    Switch aSwitch;
    WordViewModel wordViewModel;//数据通过ViewModel获取
    RecyclerView recyclerView;
    MyAdapter myAdapter1,myAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wordViewModel = ViewModelProviders.of(this).get(WordViewModel.class);//关联ViewModel
        //先获得ViewModel 再传入Adapter,f否则会传入一个空值
        myAdapter1 = new MyAdapter(false,wordViewModel);
        myAdapter2 = new MyAdapter(true,wordViewModel);

        recyclerView = findViewById(R.id.recyclerView);
        buttonInset = findViewById(R.id.buttonInsert);
        buttonClear = findViewById(R.id.buttonClear);
        textView = findViewById(R.id.textViewNumber);
        aSwitch = findViewById(R.id.switch1);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));//设置item为线性排列
        recyclerView.setAdapter(myAdapter1);

        wordViewModel.getAllWordsLive().observe(this, new Observer<List<Word>>() {
            @Override//数据发生改变时，呼叫该函数
            //监控得数据发生变化时，界面会自动刷新
            public void onChanged(List<Word> words) {
                int temp = myAdapter1.getItemCount();
                myAdapter1.setAllWords(words);
                myAdapter2.setAllWords(words);
                if (temp!=words.size()){
                    myAdapter1.notifyDataSetChanged();//通知刷新视图
                    myAdapter2.notifyDataSetChanged();
                }
            }
        });
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){//开着状态为卡片视图，关闭为常规视图
                    recyclerView.setAdapter(myAdapter2);
                }else{
                    recyclerView.setAdapter(myAdapter1);
                }
            }
        });

        buttonInset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] english = {"hello","World","Android","Google","Studio","Project","DataBase","Recycle","View","String","Value","Integer"};
                String[] chinese = {"你好","世界","安卓","谷歌","工作室","项目","数据库","回收站","视图","字符串","价值","整数类型"};
                for (int i=0;i<english.length;i++){
                    wordViewModel.insertWords(new Word(english[i],chinese[i]));
                }
            }
        });
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wordViewModel.deleteAllWords();

            }
        });
//        buttonUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Word word = new Word("Hi", "嗨");
//                word.setId(68);
//                wordViewModel.updateWords(word);
//
//            }
//        });
//        buttonDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Word word = new Word("Hi", "嗨");
//                word.setId(68);
//                wordViewModel.deleteWords(word);
//
//            }
//        });

    }



}

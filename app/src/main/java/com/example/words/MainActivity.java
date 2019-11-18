package com.example.words;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {
    private  NavController controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        controller = Navigation.findNavController(findViewById(R.id.fragment));
        NavigationUI.setupActionBarWithNavController(this,controller);

    }

    @Override
    public void onBackPressed() {//back键按下时呼叫该函数
        super.onBackPressed();
        controller.navigateUp();//返回上一逻辑页面
    }

    @Override
    public boolean onSupportNavigateUp() {//Navigation标题上的返回按钮
        controller.navigateUp();
        return super.onSupportNavigateUp();
    }

}

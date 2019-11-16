package com.example.roombasic;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerViewAccessibilityDelegate;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
    List<Word> allWords = new ArrayList<>();
    boolean useCardView;//判定是否使用卡片视图
    private WordViewModel wordViewModel;

    public MyAdapter(boolean useCardView ,WordViewModel wordViewModel) {
        this.useCardView = useCardView;
        this.wordViewModel = wordViewModel;
    }

    public void setAllWords(List<Word> allWords) {
        this.allWords = allWords;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {//创建ViewHolder时呼叫
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        //从Layout文件中加载View需要使用 LayoutInflater
        View itemView;
        if (useCardView){
            itemView = layoutInflater.inflate(R.layout.cell_card2,parent,false);
        }else{
            itemView = layoutInflater.inflate(R.layout.cell_normal2,parent,false);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {//ViewHolder与recycle绑定时呼叫的
        final Word word = allWords.get(position);
        holder.textViewNumber.setText(String.valueOf(position+1));//position为item当前位置
        holder.textViewEnglish.setText(word.getWord());
        holder.textViewChinese.setText(word.getChineseMeaning());
        holder.aSwitchChineseinvisible.setOnCheckedChangeListener(null);
        //视图是可回收的，滚动的时候可能驱动监听，可能会出现意料之外的修改
        if (word.isChinese_invisible()){
            holder.textViewChinese.setVisibility(View.GONE);//View.GONE隐藏中文意义,且不占空间
                                                            //View.INVISIBLE隐藏中文含义，但容器依然占据空间
            holder.aSwitchChineseinvisible.setChecked(true);
        }else{
            holder.textViewChinese.setVisibility(View.VISIBLE);
            holder.aSwitchChineseinvisible.setChecked(false);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {//启动一个activity，将其带入浏览器
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://m.youdao.com/dict?le=eng&q="+holder.textViewEnglish.getText());//包装网址
                Intent intent = new Intent(Intent.ACTION_VIEW);//Intent.ACTION_VIEW类型为浏览网站
                intent.setData(uri);//启动Intent需要Context
                holder.itemView.getContext().startActivity(intent);
            }
        });
        holder.aSwitchChineseinvisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    holder.textViewChinese.setVisibility(View.GONE);
                    word.setChinese_invisible(true);
                    wordViewModel.updateWords(word);//更新底层数据，再被LiveData观察
                }else{
                    holder.textViewChinese.setVisibility(View.VISIBLE);
                    word.setChinese_invisible(false);
                    wordViewModel.updateWords(word);
                }
            }
        });
    }

    @Override
    public int getItemCount() {//返回列表总的个数
        return allWords.size();
    }
    //适配器，相当于内容管理器



    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNumber,textViewEnglish,textViewChinese;
        Switch aSwitchChineseinvisible;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNumber = itemView.findViewById(R.id.textViewNumber);
            textViewEnglish = itemView.findViewById(R.id.textViewEnglish);
            textViewChinese = itemView.findViewById(R.id.textViewChinese);
            aSwitchChineseinvisible = itemView.findViewById(R.id.switchChineseinvisible);
        }
    }
}

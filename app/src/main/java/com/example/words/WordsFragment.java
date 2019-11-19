package com.example.words;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WordsFragment extends Fragment {
    private WordViewModel wordViewModel;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter1,myAdapter2;
    private FloatingActionButton floatingActionButton;//悬浮按钮
    private LiveData<List<Word>> filteredWords;//已经匹配的Words
    private static final String VIEW_TYPE_SHP = "view_type_shp";//
    private static final String IS_USING_CARD_VIEW = "is_using_card_view";
    private List<Word> allWords;
    private boolean undoAvtion = false;
    private DividerItemDecoration dividerItemDecoration;//添加边界线

    public WordsFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);//开启工具条
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_words, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        wordViewModel = ViewModelProviders.of(requireActivity()).get(WordViewModel.class);
        recyclerView = requireActivity().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        myAdapter1 = new MyAdapter(false,wordViewModel);
        myAdapter2 = new MyAdapter(true,wordViewModel);
        recyclerView.setItemAnimator(new DefaultItemAnimator(){//动画结束后刷新
            @Override
            public void onAnimationFinished(@NonNull RecyclerView.ViewHolder viewHolder) {
                super.onAnimationFinished(viewHolder);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
                int lastPosition = linearLayoutManager.findLastVisibleItemPosition();
                for (int i= firstPosition;i<lastPosition;i++){
                    MyAdapter.MyViewHolder holder = (MyAdapter.MyViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                    if (holder!=null){
                        holder.textViewNumber.setText(String.valueOf(i+1));
                    }
                }
            }
        });
        SharedPreferences shp = requireActivity().getSharedPreferences(VIEW_TYPE_SHP,Context.MODE_PRIVATE);
        boolean viewType = shp.getBoolean(IS_USING_CARD_VIEW,false);
        dividerItemDecoration = new DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL);//添加边界线
        if (viewType){
            recyclerView.setAdapter(myAdapter2);
        }else{
            recyclerView.setAdapter(myAdapter1);
            recyclerView.addItemDecoration(dividerItemDecoration);//添加边界线
        }
        filteredWords = wordViewModel.getAllWordsLive();//一开始 不过滤
        filteredWords.observe(getViewLifecycleOwner(), new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                int temp = myAdapter1.getItemCount();
                    allWords = words;
                if (temp!=words.size()){//数据发生改变，通知适配器
                    myAdapter1.submitList(words);
                    myAdapter2.submitList(words);
                }
            }
        });
        floatingActionButton = requireActivity().findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController controller = Navigation.findNavController(view);
                controller.navigate(R.id.action_wordsFragment_to_addFragment);

            }
        });
        //滑动删除,需要借助工具ItemTouchHelper 辅助工具
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.START|ItemTouchHelper.END) {
            //允许从左往右滑动，也支持从右往左滑动
            @Override//拖动
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override//滑动
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final Word wordDelete = allWords.get(viewHolder.getAdapterPosition());
                wordViewModel.deleteWords(wordDelete);
                //撤销
                Snackbar.make(requireActivity().findViewById(R.id.wordsFragmentView),"删除了一个词汇",Snackbar.LENGTH_SHORT)
                        .setAction("撤销", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                undoAvtion = true;
                                wordViewModel.insertWords(wordDelete);
                            }
                        })//设置动作
                        .show();
            }
        }).attachToRecyclerView(recyclerView);//生效
    }

    @Override
    public void onResume() {
        super.onResume();
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(),0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//工具条中的item被选择时的处理，因为有多个item，所以需要switch筛选
        switch (item.getItemId()){
            case R.id.clearData://选中清空数据n一个提示框
                AlertDialog.Builder builder =new AlertDialog.Builder(requireActivity());
                builder.setTitle("清空数据")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                wordViewModel.deleteAllWords();
                            }
                        })
                        .setNegativeButton("取消",null)
                        .create()
                        .show();
                break;
            case R.id.switchViewType:
                SharedPreferences shp = requireActivity().getSharedPreferences(VIEW_TYPE_SHP,Context.MODE_PRIVATE);
                boolean viewType = shp.getBoolean(IS_USING_CARD_VIEW,false);//
                SharedPreferences.Editor editor = shp.edit();
                if (viewType){
                    recyclerView.setAdapter(myAdapter1);
                    editor.putBoolean(IS_USING_CARD_VIEW,false);
                    recyclerView.addItemDecoration(dividerItemDecoration);
                }else {
                    recyclerView.setAdapter(myAdapter2);
                    editor.putBoolean(IS_USING_CARD_VIEW,true);
                    recyclerView.removeItemDecoration(dividerItemDecoration);
                }
                editor.apply();



        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //在MainActivity中添加，能够让其一直显示        getMenuInflater().inflate(R.menu.main_menu,menu);//将工具条加载到工具栏
        inflater.inflate(R.menu.main_menu,menu);
        //默认关闭工具条，需要开启    setHasOptionsMenu(true);


        //设置工具条的功能
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setMaxWidth(1000);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //setOnQueryTextListener内容改变时的监听器
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {//内容改变时 赛选结果
//                Log.d("mylog", "onQueryTextChange: "+s);
                String pattern = s.trim();
                filteredWords.removeObservers(getViewLifecycleOwner()); ///...先移出观察，防止出现重复
                filteredWords = wordViewModel.findWordsWithPattern(pattern);
                filteredWords.observe(getViewLifecycleOwner(), new Observer<List<Word>>() {
                    @Override
                    public void onChanged(List<Word> words) {
                        int temp = myAdapter1.getItemCount();
                        allWords = words;
                        if (temp!=words.size()){
                            if (temp<words.size()&&undoAvtion){//小于表示插入  并且不是在撤销上
                                recyclerView.smoothScrollBy(0,-200);//往下滚动200dp
                            }
                            undoAvtion = false;
//                            myAdapter1.notifyDataSetChanged();
//                            myAdapter2.notifyDataSetChanged();
//                            myAdapter1.notifyItemInserted(0);
                            myAdapter1.submitList(words);
                            myAdapter2.submitList(words);
                            //提交的数据会在后台进行差异化比较，根据比对结果来刷新界面
                        }
                    }
                });




                return true;//返回值true 这个事件不会再往下传递
            }
        });

    }
}

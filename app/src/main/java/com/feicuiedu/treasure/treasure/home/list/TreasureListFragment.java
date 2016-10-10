package com.feicuiedu.treasure.treasure.home.list;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feicuiedu.treasure.R;
import com.feicuiedu.treasure.treasure.TreasureRepo;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * RecycleView v7 实现ListView GridView 瀑布流
 */

public class TreasureListFragment extends Fragment {

    private RecyclerView recyclerView;
    private TreasureListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        recyclerView = new RecyclerView(container.getContext());
        // 设置展示的类型 LinearLayoutManager,GridLayoutManager,StaggeredGridLayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        // 添加动画
        recyclerView.setItemAnimator(new SlideInUpAnimator());
        // 设置背景
        recyclerView.setBackgroundResource(R.drawable.screen_bg);
        return recyclerView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 设置适配器，设置数据展示
        adapter = new TreasureListAdapter();
        recyclerView.setAdapter(adapter);
        adapter.addItems(TreasureRepo.getInstance().getTreasure());
    }
}

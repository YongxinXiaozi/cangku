package com.feicuiedu.treasure.treasure.home.list;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.feicuiedu.treasure.components.TreasureView;
import com.feicuiedu.treasure.treasure.Treasure;
import com.feicuiedu.treasure.treasure.home.detail.TreasureDetailActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by 123 on 2016/9/23.
 */
public class TreasureListAdapter extends RecyclerView.Adapter<TreasureListAdapter.MyViewHolder> {

    private List<Treasure> datas = new ArrayList<>();

    public void addItems(Collection<Treasure> items){
        if (items!=null){
            datas.addAll(items);
            notifyItemRangeChanged(0,datas.size());
        }
    }

    // 创建ViewHolder对象
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TreasureView treasureView = new TreasureView(parent.getContext());
        return new MyViewHolder(treasureView);
    }

    // 将数据绑定到ViewHolder上
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Treasure treasure = datas.get(position);
        holder.treasureView.bindTreasure(treasure);
        holder.treasureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TreasureDetailActivity.open(v.getContext(),treasure);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas==null?0:datas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TreasureView treasureView;

        public MyViewHolder(TreasureView itemView) {
            super(itemView);
            this.treasureView = itemView;
        }
    }
}

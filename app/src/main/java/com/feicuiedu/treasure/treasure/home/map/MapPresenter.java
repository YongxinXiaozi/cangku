package com.feicuiedu.treasure.treasure.home.map;

import android.util.Log;
import android.widget.Toast;

import com.feicuiedu.treasure.net.NetClient;
import com.feicuiedu.treasure.treasure.Area;
import com.feicuiedu.treasure.treasure.Treasure;
import com.feicuiedu.treasure.treasure.TreasureApi;
import com.feicuiedu.treasure.treasure.TreasureRepo;
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by 123 on 2016/9/21.
 */
public class MapPresenter extends MvpNullObjectBasePresenter<MapMvpView>{

    private Call<List<Treasure>> treasureCall;
    private Area area;

    public void getTreasure(Area area){

        // 去判断这个区域的宝藏有没有缓存
        if (TreasureRepo.getInstance().isCached(area)){
            return;
        }
        this.area = area;
        TreasureApi treasureApi = NetClient.getInstance().getTreasureApi();
        if (treasureCall!=null){
            treasureCall.cancel();
        }
        treasureCall = treasureApi.getTreasureInArea(area);
        treasureCall.enqueue(callback);

    }

    private Callback<List<Treasure>> callback = new Callback<List<Treasure>>() {

        // 有返回结果时
        @Override
        public void onResponse(Call<List<Treasure>> call, Response<List<Treasure>> response) {
            //
            if (response!=null && response.isSuccessful()){
                List<Treasure> datas = response.body();
                if (datas==null){
                    // 弹出吐司，说明错误信息
                    getView().showMessage("unknown error");
                    return;
                }
                TreasureRepo.getInstance().addTreasure(datas);
                TreasureRepo.getInstance().cache(area);

                // 通知视图展示数据
                getView().setData(datas);
            }
        }

        // 失败的时候
        @Override
        public void onFailure(Call<List<Treasure>> call, Throwable t) {
            getView().showMessage(t.getMessage());
        }
    };
}

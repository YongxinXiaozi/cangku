package com.feicuiedu.treasure.treasure.home.detail;

import com.feicuiedu.treasure.net.NetClient;
import com.feicuiedu.treasure.treasure.TreasureApi;
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by 123 on 2016/9/22.
 */
public class TreasureDetailPresenter extends MvpNullObjectBasePresenter<TreasureDetialView>{
    private Call<List<TreasureDetailResult>> detailCall;
    // 业务类，主要是获取宝藏的具体信息

    // 核心方法：获取宝藏详细信息
    public void getTreasureDetail(TreasureDetail treasureDetail) {
        if (detailCall != null) {
            detailCall.cancel();
        }
        TreasureApi treasureApi = NetClient.getInstance().getTreasureApi();
        detailCall = treasureApi.getTreasureDetail(treasureDetail);
        detailCall.enqueue(callBack);
    }

    private Callback<List<TreasureDetailResult>> callBack = new Callback<List<TreasureDetailResult>>() {

        // 请求返回
        @Override
        public void onResponse(Call<List<TreasureDetailResult>> call, Response<List<TreasureDetailResult>> response) {

            if (response!=null && response.isSuccessful()){
                List<TreasureDetailResult> resultList = response.body();
                if (resultList==null){
                    // 弹出吐司，发生了错误
                    getView().showMessage("unknown error");
                    return;
                }
                // 数据有了，设置给视图来展示
                getView().setData(resultList);
            }
        }

        // 请求失败
        @Override
        public void onFailure(Call<List<TreasureDetailResult>> call, Throwable t) {
            // 弹出吐司
            getView().showMessage(t.getMessage());
        }
    };

}

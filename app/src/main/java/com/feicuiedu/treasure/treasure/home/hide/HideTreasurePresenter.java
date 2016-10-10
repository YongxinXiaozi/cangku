package com.feicuiedu.treasure.treasure.home.hide;

import com.feicuiedu.treasure.net.NetClient;
import com.feicuiedu.treasure.treasure.TreasureApi;
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by 123 on 2016/9/23.
 */
public class HideTreasurePresenter extends MvpNullObjectBasePresenter<HideTreasureView>{
    private Call<HideTreasureResult> resultCall;
    // 数据的上传，根据返回结果与视图交互

    public void hideTreasure(HideTreasure hideTreasure){
        getView().showProgress();
        TreasureApi treasureApi = NetClient.getInstance().getTreasureApi();
        if (resultCall!=null){
            resultCall.cancel();
        }
        resultCall = treasureApi.hideTreasure(hideTreasure);
        resultCall.enqueue(callback);
    }

    private Callback<HideTreasureResult> callback = new Callback<HideTreasureResult>() {

        // 返回
        @Override
        public void onResponse(Call<HideTreasureResult> call, Response<HideTreasureResult> response) {
            getView().hideProgress();
            if (response!=null && response.isSuccessful()){
                HideTreasureResult result = response.body();
                if (result==null){
                    getView().showMessage("unknown error");
                    return;
                }
                /**
                 * 得到结果之后，code值，真正埋藏成功，导航到home页
                 */
                getView().showMessage(result.getMsg());
                if (result.code==1){
                    getView().navigateToHome();
                }
            }
        }

        // 请求失败
        @Override
        public void onFailure(Call<HideTreasureResult> call, Throwable t) {
            getView().hideProgress();
            getView().showMessage(t.getMessage());
        }
    };

}

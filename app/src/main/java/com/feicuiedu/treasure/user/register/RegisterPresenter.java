package com.feicuiedu.treasure.user.register;

import com.feicuiedu.treasure.net.NetClient;
import com.feicuiedu.treasure.user.User;
import com.feicuiedu.treasure.user.UserApi;
import com.feicuiedu.treasure.user.UserPrefs;
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Administrator on 2016/7/12 0012.
 * <p/>
 * 注册相关业务, 怎么和视图结合 ????
 */
public class RegisterPresenter extends MvpNullObjectBasePresenter<RegisterView> {

    private Call<RegisterResult> registerCall;

    // 核心的业务
    public void register(User user){

        getView().showProgress();
        // Retrofit 网络连接
        UserApi userApi = NetClient.getInstance().getUserApi();
        registerCall = userApi.register(user);
        registerCall.enqueue(callback);

    }

    private Callback<RegisterResult> callback = new Callback<RegisterResult>() {

        // 返回的结果
        @Override
        public void onResponse(Call<RegisterResult> call, Response<RegisterResult> response) {
            getView().hideProgress();
            // 返回的结果不为空且成功
            if (response!=null && response.isSuccessful()){
                RegisterResult result = response.body();
                if (result==null){
                    getView().showMessage("unknown error");
                    return;
                }
                // 不为空 判断code
                if (result.getCode()==1){
                    UserPrefs.getInstance().setTokenid(result.getTokenId());
                    getView().showMessage("注册成功");
                    getView().navigateToHome();
                    return;
                }
                getView().showMessage(result.getMsg());
            }
        }

        // 请求失败
        @Override
        public void onFailure(Call<RegisterResult> call, Throwable t) {
            getView().hideProgress();
            getView().showMessage(t.getMessage());
        }
    };
}

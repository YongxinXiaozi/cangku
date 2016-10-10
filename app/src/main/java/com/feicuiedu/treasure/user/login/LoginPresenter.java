package com.feicuiedu.treasure.user.login;

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
 * 登陆视图业务
 */
public class LoginPresenter extends MvpNullObjectBasePresenter<LoginView>{

    private Call<LoginResult> loginCall;

    // 核心业务--进行登录
    public void login(User user){
        getView().showProgress();

        // 网络连接，去登录
        UserApi userApi = NetClient.getInstance().getUserApi();
        loginCall = userApi.login(user);
        loginCall.enqueue(callback);

    }

    private Callback<LoginResult> callback = new Callback<LoginResult>() {

        // 请求返回
        @Override
        public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
            getView().hideProgress();
            if (response.isSuccessful()&& response!=null){
                LoginResult result = response.body();
                if (result==null){
                    getView().showMessage("unknown error");
                    return;
                }
                getView().showMessage(result.getMsg());
                if (result.getCode()==1){
                    UserPrefs.getInstance().setPhoto(NetClient.BASE_URL+result.getIconUrl());
                    UserPrefs.getInstance().setTokenid(result.getTokenId());
                    getView().navigateToHome();
                }
                return;
            }
        }

        // 请求失败
        @Override
        public void onFailure(Call<LoginResult> call, Throwable t) {
            getView().hideProgress();
            getView().showMessage(t.getMessage());
        }
    };
}
package com.feicuiedu.treasure.user.login;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * 登陆业务相关视图
 */
public interface LoginView extends MvpView{

    void showProgress(); // 显示登陆中的Loading视图

    void hideProgress(); // 隐藏登陆中的Loading视图

    void showMessage(String msg); // 显示信息

    void navigateToHome(); // 导航到Home页面
}
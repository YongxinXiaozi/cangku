package com.feicuiedu.treasure.treasure.home.hide;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by 123 on 2016/9/23.
 */
public interface HideTreasureView extends MvpView{

    void showProgress();// 显示进度
    void hideProgress();// 隐藏进度
    void showMessage(String msg);// 显示信息
    void navigateToHome();// 导航到Home页面
}

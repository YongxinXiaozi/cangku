package com.feicuiedu.treasure.treasure.home.map;

import com.feicuiedu.treasure.treasure.Treasure;
import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

/**
 * Created by 123 on 2016/9/21.
 */
public interface MapMvpView extends MvpView{

    void showMessage(String msg);// 显示信息

    void setData(List<Treasure> datas);// 设置数据

}

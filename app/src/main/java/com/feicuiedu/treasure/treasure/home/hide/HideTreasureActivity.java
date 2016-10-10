package com.feicuiedu.treasure.treasure.home.hide;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.baidu.mapapi.model.LatLng;
import com.feicuiedu.treasure.R;
import com.feicuiedu.treasure.commons.ActivityUtils;
import com.feicuiedu.treasure.treasure.TreasureRepo;
import com.feicuiedu.treasure.user.UserPrefs;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HideTreasureActivity extends MvpActivity<HideTreasureView,HideTreasurePresenter> implements HideTreasureView{

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_description)
    EditText etDescription;

    private static final String KEY_TITLE = "key_title";
    private static final String KEY_LOCATION = "key_location";
    private static final String KEY_LATLNG = "key_latlng";
    private static final String KEY_ALTITUDE = "key_altitude";

    private ActivityUtils activityUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_treasure);
    }

    public static void open(Context context, String title, String address, LatLng latLng, double altitude) {
        Intent intent = new Intent(context, HideTreasureActivity.class);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_LOCATION, address);
        intent.putExtra(KEY_LATLNG, latLng);
        intent.putExtra(KEY_ALTITUDE, altitude);
        context.startActivity(intent);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
        activityUtils = new ActivityUtils(this);

        // 显示toolbar信息
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra(KEY_TITLE));
        }
    }

    @NonNull
    @Override
    public HideTreasurePresenter createPresenter() {
        return new HideTreasurePresenter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hide_treasure,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_send:
                // 要去进行数据上传到服务器

                Intent intent = getIntent();
                LatLng latLng = intent.getParcelableExtra(KEY_LATLNG);
                double altitude = intent.getDoubleExtra(KEY_ALTITUDE,0);
                String location = intent.getStringExtra(KEY_LOCATION);
                String title = intent.getStringExtra(KEY_TITLE);
                int tokenId = UserPrefs.getInstance().getTokenid();
                String description = etDescription.getText().toString();

                HideTreasure hideTreasure = new HideTreasure();
                hideTreasure.setAltitude(altitude);
                hideTreasure.setDescription(description);
                hideTreasure.setLatitude(latLng.latitude);
                hideTreasure.setLongitude(latLng.longitude);
                hideTreasure.setTitle(title);
                hideTreasure.setLocation(location);
                hideTreasure.setTokenId(tokenId);

                // 进行业务处理
                getPresenter().hideTreasure(hideTreasure);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private ProgressDialog progressDialog;

    @Override
    public void showProgress() {
        progressDialog = ProgressDialog.show(this,"","宝藏正在上传中，请稍等...");
    }

    @Override
    public void hideProgress() {
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void showMessage(String msg) {
        activityUtils.showToast(msg);
    }

    @Override
    public void navigateToHome() {
        finish();
        // 清除存储的宝藏
        TreasureRepo.getInstance().clear();
    }
}

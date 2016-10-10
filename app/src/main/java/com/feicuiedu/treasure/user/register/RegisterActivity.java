package com.feicuiedu.treasure.user.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.feicuiedu.treasure.MainActivity;
import com.feicuiedu.treasure.R;
import com.feicuiedu.treasure.commons.ActivityUtils;
import com.feicuiedu.treasure.commons.RegexUtils;
import com.feicuiedu.treasure.components.AlertDialogFragment;
import com.feicuiedu.treasure.treasure.home.HomeActivity;
import com.feicuiedu.treasure.user.User;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 注册视图
 * <p>
 * OkHttp库
 * 是我们使用的网络连接框架基栈技术
 */
public class RegisterActivity extends MvpActivity<RegisterView, RegisterPresenter> implements RegisterView {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_Username)
    EditText etUsername;
    @Bind(R.id.et_Password)
    EditText etPassword;
    @Bind(R.id.et_Confirm)
    EditText etConfirm;
    @Bind(R.id.btn_Register)
    Button btnRegister;

    private ActivityUtils activityUtils;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUtils = new ActivityUtils(this);
        setContentView(R.layout.activity_register);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);

        // toolBar 展示
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("寻宝");
        }

        // 去监听输入框有没有输入东西
        etUsername.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);
        etConfirm.addTextChangedListener(textWatcher);

    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            username = etUsername.getText().toString();
            password = etPassword.getText().toString();
            String confirm = etConfirm.getText().toString();
            boolean canRegister = !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)
                    && password.equals(confirm);
            btnRegister.setEnabled(canRegister);// 注意：在布局内注册按钮默认是不可用的
        }
    };

    @NonNull
    @Override
    public RegisterPresenter createPresenter() {
        return new RegisterPresenter();
    }

    @Override
    public void navigateToHome() {
        activityUtils.startActivity(HomeActivity.class);
        finish();

        // 关闭MainActivity，发送一个广播，本地广播
        Intent intent = new Intent(MainActivity.ACTION_ENTER_HOME);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    private ProgressDialog progressDialog;

    @Override
    public void showProgress() {
        progressDialog = ProgressDialog.show(this, "", "注册中,请稍等....");
    }

    @Override
    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showMessage(String msg) {
        activityUtils.showToast(msg);
    }

    @OnClick(R.id.btn_Register)
    public void onClick() {

        // 去执行操作，验证注册的信息
        activityUtils.hideSoftKeyboard();

        // 验证密码是否符合要求
        if (RegexUtils.verifyPassword(password)!=RegexUtils.VERIFY_SUCCESS){
            showPasswordError();
            return;
        }

        // 验证用户名
        if (RegexUtils.verifyUsername(username)!=RegexUtils.VERIFY_SUCCESS){
            showUsernameError();
            return;
        }

        // 如果都符合要求，去执行业务
        getPresenter().register(new User(username,password));

    }



    // 用户名输入错误Dialog
    private void showUsernameError() {
        String msg = getString(R.string.username_rules);
        AlertDialogFragment fragment = AlertDialogFragment.newInstance(R.string.username_error, msg);
        fragment.show(getSupportFragmentManager(), "showUsernameError");
    }

    // 密码输入错误Dialog
    private void showPasswordError() {
        String msg = getString(R.string.password_rules);
        AlertDialogFragment fragment = AlertDialogFragment.newInstance(R.string.password_error, msg);
        fragment.show(getSupportFragmentManager(), "showPasswordError");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}

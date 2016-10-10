package com.feicuiedu.treasure.user.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
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
 * 登陆视图, 纯种视图
 * <p/>
 * 我们的登陆业务， 是不是只要针对LoginView来做就行了
 */
public class LoginActivity extends MvpActivity<LoginView,LoginPresenter> implements LoginView {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_Username)
    EditText etUsername;
    @Bind(R.id.et_Password)
    EditText etPassword;
    @Bind(R.id.btn_Login)
    Button btnLogin;

    private ActivityUtils activityUtils;
    private String userName;
    private String passWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUtils = new ActivityUtils(this);
        setContentView(R.layout.activity_login);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);

        // toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getTitle());
        }
        etPassword.addTextChangedListener(textWatcher);
        etUsername.addTextChangedListener(textWatcher);
    }

    @NonNull
    @Override
    public LoginPresenter createPresenter() {
        return new LoginPresenter();
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
            userName = etUsername.getText().toString();
            passWord = etPassword.getText().toString();
            boolean canLogin = !(TextUtils.isEmpty(userName) || TextUtils.isEmpty(passWord));
            // 默认情况下Login按钮是未激活，不可点的
            btnLogin.setEnabled(canLogin);
        }
    };

    @OnClick(R.id.btn_Login)
    public void onClick() {

        // 去做登陆的业务
        activityUtils.hideSoftKeyboard();

        // 验证密码是否符合要求
        if (RegexUtils.verifyPassword(passWord)!=RegexUtils.VERIFY_SUCCESS){
            showPasswordError();
            return;
        }

        // 验证用户名
        if (RegexUtils.verifyUsername(userName)!=RegexUtils.VERIFY_SUCCESS){
            showUsernameError();
            return;
        }
        // 如果都符合要求，去执行业务
        getPresenter().login(new User(userName,passWord));
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

    private ProgressDialog progressDialog;

    @Override
    public void showProgress() {
        progressDialog = ProgressDialog.show(this,"","登录中，请稍等...");
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
        activityUtils.startActivity(HomeActivity.class);
        finish();

        // 发送广播，关闭MainActivity
        Intent intent = new Intent(MainActivity.ACTION_ENTER_HOME);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
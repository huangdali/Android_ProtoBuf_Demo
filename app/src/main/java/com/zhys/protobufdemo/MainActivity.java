package com.zhys.protobufdemo;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hdl.elog.ELog;
import com.zhys.protobufdemo.http.HttpSend;
import com.zhys.protobufdemo.http.ResultCallbackListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import io.reactivex.disposables.Disposable;
import okhttp3.HttpUrl;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPwd;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    public void onLogin1(View view) {
        progressDialog.show();//显示加载框
        //获取用户输入的用户名和密码
        String username = etUsername.getText().toString().trim();
        String pwd = etPwd.getText().toString().trim();
        //构造LoginRequest对象
        LoginRequestOuterClass.LoginRequest loginRequest = LoginRequestOuterClass.LoginRequest.newBuilder().setUsername(username).setPwd(pwd).build();
        //登陆
        login(loginRequest.toByteArray());
    }
    /**
     * 初始化视图
     */
    private void initView() {
        etUsername = findViewById(R.id.et_username);
        etPwd = findViewById(R.id.et_pwd);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("登陆中，请稍后");
    }

    /**
     * 登录
     *
     * @param view
     */
    public void onLogin(View view) {
        progressDialog.show();
        String username = etUsername.getText().toString().trim();
        String pwd = etPwd.getText().toString().trim();
        //开始请求
        HttpSend.getInstance().login(username,pwd, new ResultCallbackListener<LoginRequestOuterClass.LoginResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
                ELog.e("-----------");
            }

            @Override
            public void onNext(LoginRequestOuterClass.LoginResponse value) {
                ELog.e("登陆结果：code = " + value.getCode() + "\tmsg = " + value.getMsg());
                Toast.makeText(MainActivity.this, value.getMsg(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                ELog.e("e"+e.getMessage());
            }

            @Override
            public void onComplete() {
                progressDialog.dismiss();
                ELog.e("=================");
            }
        });
    }


    /**
     * 开始登录（基于HttpURLConnection）
     *
     * @param data
     */
    public void login(final byte[] data) {
        new Thread() {
            @Override
            public void run() {
                OutputStream os = null;
                try {
                    URL url = new URL("http://192.168.0.162:8080/protobuf//login.action");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    os = conn.getOutputStream();
                    os.write(data);
                    if (conn.getResponseCode() == 200) {
                        //解析结果
                        final LoginRequestOuterClass.LoginResponse loginResponse = LoginRequestOuterClass.LoginResponse.parseFrom(conn.getInputStream());
                        ELog.e("登陆结果：code = " + loginResponse.getCode() + "\tmsg = " + loginResponse.getMsg());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, loginResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (os != null) {
                            os.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }
        }.start();
    }
}

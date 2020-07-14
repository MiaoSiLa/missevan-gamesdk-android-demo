package com.missevan.game.demo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.missevan.game.sdk.MissEvanSdk;
import com.missevan.game.sdk.callbacklistener.CallbackListener;
import com.missevan.game.sdk.callbacklistener.ExitCallbackListener;
import com.missevan.game.sdk.callbacklistener.InitCallbackListener;
import com.missevan.game.sdk.callbacklistener.MissEvanSdkError;
import com.missevan.game.sdk.callbacklistener.OrderCallbackListener;
import com.missevan.game.sdk.utils.LogUtils;
import com.missevan.game.sdk.utils.MD5;
import com.missevan.game.sdk.utils.MissEvanConstants;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "yjnull";
    private Context mContext;
    private EditText payNum;
    private String mUid;

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");

        mContext = this;
        MissEvanSdk gameSdk = MissEvanSdk.initialize(true, MainActivity.this,
                "1", "1", "1", "JqCB4Jun1pWYaoiTbhC2a$0icKv2JSsu", new InitCallbackListener() {
            @Override
            public void onSuccess() {
                Log.d("yjnull", "MissEvan SDK 初始化成功");
                makeToast("MissEvan SDK 初始化成功");
            }

            @Override
            public void onFailed() {

            }
        }, new ExitCallbackListener() {
            @Override
            public void onExit() {
                makeToast("退出游戏");
                finish();
            }
        });

        setContentView(R.layout.activity_main);

        Button tv = findViewById(R.id.missevan_btn_main);
        tv.setText(getResources().getString(R.string.missevansdk_login_title));

        //FrameLayout frameLayout = findViewById(R.id.missevan_root_layout);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MissEvanSdk.getInstance().login(new CallbackListener() {
                    @Override
                    public void onSuccess(Bundle success) {
                        LogUtils.d("MainActivity onSuccess = ");
                        String uid = success.getString("uid");
                        String userName = success.getString("username");
                        String access_token = success.getString("access_token");
                        long expire_times = success.getLong("expire_times");
                        String refresh_token = success.getString("refresh_token");
                        String nickname = success.getString("nickname");
                        makeToast("uid: " + uid + " username: " + userName + " nickname: " + nickname
                                + " access_token: " + access_token
                                + " expire_times: " + expire_times
                                + " refresh_token: " + refresh_token);
                        mUid = uid;
                    }

                    @Override
                    public void onFailed(MissEvanSdkError failed) {
                        LogUtils.d("MainActivity onFailed\nErrorCode : "
                                + failed.getErrorCode() + "\nErrorMessage : "
                                + failed.getErrorMessage());
                        makeToast("onFailed\nErrorCode : "
                                + failed.getErrorCode() + "\nErrorMessage : "
                                + failed.getErrorMessage());
                    }

                    @Override
                    public void onError(MissEvanSdkError error) {
                        // 此处为操作异常时执行，返回值为 MissEvanSdkError 类型变量，其中包含ErrorCode和ErrorMessage
                        LogUtils.d("MainActivity onError\nErrorCode : "
                                + error.getErrorCode() + "\nErrorMessage : "
                                + error.getErrorMessage());
                        makeToast("onError\nErrorCode : " + error.getErrorCode()
                                + "\nErrorMessage : " + error.getErrorMessage());
                    }
                });
            }
        });

        findViewById(R.id.missevan_btn_isLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MissEvanSdk.getInstance().isLogin(new CallbackListener() {
                    @Override
                    public void onSuccess(Bundle success) {
                        LogUtils.d("isLogin onSuccess");
                        boolean logined = success.getBoolean("is_login", false);
                        makeToast("isLogin: " + logined);
                    }

                    @Override
                    public void onFailed(MissEvanSdkError failed) {
                        LogUtils.d("isLogin onFailed\nErrorCode : "
                                + failed.getErrorCode() + "\nErrorMessage : "
                                + failed.getErrorMessage());
                        makeToast("onFailed\nErrorCode : "
                                + failed.getErrorCode() + "\nErrorMessage : "
                                + failed.getErrorMessage());
                    }

                    @Override
                    public void onError(MissEvanSdkError error) {
                        LogUtils.d("isLogin onError\nErrorCode : "
                                + error.getErrorCode() + "\nErrorMessage : "
                                + error.getErrorMessage());
                        makeToast("onError\nErrorCode : " + error.getErrorCode()
                                + "\nErrorMessage : " + error.getErrorMessage());
                    }
                });
            }
        });

        findViewById(R.id.missevan_btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MissEvanSdk.getInstance().logout(null);
                makeToast("注销成功");
            }
        });

        findViewById(R.id.missevan_btn_role).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创角
                MissEvanSdk.getInstance().createRole("1001", "猫耳区服-A", "222", "怪兽", new CallbackListener() {
                    @Override
                    public void onSuccess(Bundle success) {
                        String string = success.getString(MissEvanConstants.TIPS);
                        LogUtils.d(string);
                        makeToast(string);
                    }

                    @Override
                    public void onFailed(MissEvanSdkError failed) {
                        String string = failed.getErrorCode() + ", " + failed.getErrorMessage();
                        LogUtils.e("===============onFAILEd============");
                        LogUtils.d(string);
                        makeToast(string);
                    }

                    @Override
                    public void onError(MissEvanSdkError error) {
                        String string = error.getErrorCode() + ", " + error.getErrorMessage();
                        LogUtils.e("========onERROr===================");
                        LogUtils.d(string);
                        makeToast(string);
                    }
                });
            }
        });

        findViewById(R.id.missevan_btn_notifyzone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 通知区服
                MissEvanSdk.getInstance().notifyZone("1002", "猫耳区服-A", "222", "怪兽", new CallbackListener() {
                    @Override
                    public void onSuccess(Bundle success) {
                        String string = success.getString(MissEvanConstants.TIPS);
                        LogUtils.d(string);
                        makeToast(string);
                    }

                    @Override
                    public void onFailed(MissEvanSdkError failed) {
                        String string = failed.getErrorCode() + ", " + failed.getErrorMessage();
                        LogUtils.e("===============onFAILEd============");
                        LogUtils.d(string);
                        makeToast(string);
                    }

                    @Override
                    public void onError(MissEvanSdkError error) {
                        String string = error.getErrorCode() + ", " + error.getErrorMessage();
                        LogUtils.e("========onERROr===================");
                        LogUtils.d(string);
                        makeToast(string);
                    }
                });
            }
        });

        findViewById(R.id.missevan_btn_certificate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 是否实名认证
                MissEvanSdk.getInstance().isRealNameAuth(new CallbackListener() {
                    @Override
                    public void onSuccess(Bundle success) {
                        LogUtils.d("isRealNameAuth onSuccess");
                        boolean logined = success.getBoolean(MissEvanConstants.IS_REALNAME_AUTH, false);
                        makeToast("是否实名认证: " + logined);
                    }

                    @Override
                    public void onFailed(MissEvanSdkError failed) {

                    }

                    @Override
                    public void onError(MissEvanSdkError error) {

                    }
                });
            }
        });

        findViewById(R.id.missevan_btn_userinfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取用户信息
                MissEvanSdk.getInstance().getUserInfo(new CallbackListener() {
                    @Override
                    public void onSuccess(Bundle arg0) {
                        String uid = arg0.getString("uid");
                        String username = arg0.getString("username");
                        String access_token = arg0.getString("access_token");
                        String expire_times = arg0.getString("expire_times");
                        String refresh_token = arg0.getString("refresh_token");
                        String avatar = arg0.getString("avatar");
                        String s_avatar = arg0.getString("s_avatar");

                        String lastLoginTime = arg0
                                .getString("last_login_time");
                        LogUtils.d("onSuccess\nuid: " + uid + " username: "
                                + username + " access_token: " + access_token
                                + " expire_times: " + expire_times
                                + " refresh_token: " + refresh_token
                                + " lastLoginTime: " + lastLoginTime
                                + " avatar " + avatar + " s_avatar " + s_avatar);
                        makeToast(" uid: " + uid + " username: " + username
                                + " access_token: " + access_token
                                + " expire_times: " + expire_times
                                + " refresh_token: " + refresh_token
                                + " lastLoginTime: " + lastLoginTime
                                + " avatar " + avatar + " s_avatar " + s_avatar);
                    }

                    @Override
                    public void onFailed(MissEvanSdkError arg0) {
                        LogUtils.d("onFailed\nErrorCode : "
                                + arg0.getErrorCode() + "\nErrorMessage : "
                                + arg0.getErrorMessage());
                        makeToast("onFailed\nErrorCode : " + arg0.getErrorCode()
                                + "\nErrorMessage : " + arg0.getErrorMessage());
                    }

                    @Override
                    public void onError(MissEvanSdkError arg0) {
                        LogUtils.d("onError\nErrorCode : "
                                + arg0.getErrorCode() + "\nErrorMessage : "
                                + arg0.getErrorMessage());
                        makeToast("onError\nErrorCode : " + arg0.getErrorCode()
                                + "\nErrorMessage : " + arg0.getErrorMessage());
                    }
                });

            }
        });

        findViewById(R.id.missevan_btn_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MissEvanSdk.getInstance().exit(new ExitCallbackListener() {
                    @Override
                    public void onExit() {
                        makeToast("退出游戏");
                        finish();
                    }
                });
            }
        });

        payNum = findViewById(R.id.missevan_et_pay);
        findViewById(R.id.missevan_btn_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String money = payNum.getText().toString();
                if (TextUtils.isEmpty(money)) {
                    makeToast("金额不能为空");
                    return;
                }
                String out_trade_number = String.valueOf(System.currentTimeMillis());
                int fee = Integer.parseInt(money);
                int gameMoney = (int) ((fee / 100.0) * 100);
                String notifyUrl = "";
                String data = String.valueOf(gameMoney) + String.valueOf(fee) + notifyUrl + out_trade_number;
                //秘钥为服务端secretKey
                String order_sign = sign(data, "YY7J28iu2UOpiJH8IOh89HoHSvORQv5w78HJJYsdfs9s8SH89ju8J");
//                String order_sign = sign(data, "fuckid");
                MissEvanSdk.getInstance().pay(mUid, gameMoney, "金币", "最强商城", out_trade_number, fee, 1,
                        "超人", "hahahaha", notifyUrl, order_sign, new OrderCallbackListener() {
                            @Override
                            public void onSuccess(String out_trade_no, String bs_trade_no) {
                                Log.d("yjnull", "MainActivity -> pay -> " + out_trade_no + ", bs_trade_no = " + bs_trade_no);
                                makeToast("支付成功: " + out_trade_no + ", bs: " + bs_trade_no);
                            }

                            @Override
                            public void onFailed(String out_trade_no, MissEvanSdkError failed) {
                                Log.d("yjnull", "MainActivity -> pay onFailed -> " + failed.getErrorCode() + ", msg = " + failed.getErrorMessage());
                                makeToast(failed.getErrorMessage());
                            }

                            @Override
                            public void onError(String out_trade_no, MissEvanSdkError error) {
                                Log.d("yjnull", "MainActivity -> pay onError -> " + error.getErrorCode() + error.getErrorMessage());
                                makeToast(error.getErrorMessage());
                            }
                        }
                );
            }
        });

        verifyPermissions();
    }

    private void verifyPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                    0x001);
        }

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            ApplicationInfo applicationInfo = info.applicationInfo;
            Class<? extends ApplicationInfo> aClass = applicationInfo.getClass();
            Field networkSecurityConfigRes = aClass.getDeclaredField("networkSecurityConfigRes");
            //networkSecurityConfigRes.setAccessible(true);
            int anInt = networkSecurityConfigRes.getInt(applicationInfo);

            setTitle("猫耳游戏SdkDemo " + (anInt != 0 ? "(抓包YES)" : "(抓包NO)"));
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    public String sign(String data, String secretKey) {
        return MD5.sign(data, secretKey);
    }

    private void makeToast(String result) {
        //mHandler.sendMessage(mHandler.obtainMessage(1, result));
        Toast.makeText(mContext, result, Toast.LENGTH_SHORT)
                .show();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {
                Toast.makeText(mContext, (String) msg.obj, Toast.LENGTH_SHORT)
                        .show();
            }
        }
    };
}

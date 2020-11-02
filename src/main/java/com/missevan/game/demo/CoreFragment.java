package com.missevan.game.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.missevan.game.demo.data.DataSource;
import com.missevan.game.demo.model.Post;
import com.missevan.game.demo.ui.EditAlertDialog;
import com.missevan.game.demo.ui.PostAdapter;
import com.missevan.game.demo.utils.MD5;
import com.missevan.game.sdk.MissEvanSdk;
import com.missevan.game.sdk.callbacklistener.CallbackListener;
import com.missevan.game.sdk.callbacklistener.ExitCallbackListener;
import com.missevan.game.sdk.callbacklistener.InitCallbackListener;
import com.missevan.game.sdk.callbacklistener.MissEvanSdkError;
import com.missevan.game.sdk.callbacklistener.OrderCallbackListener;
import com.missevan.game.sdk.utils.MissEvanConstants;

import java.util.List;

/**
 * Created by yangya on 2019-10-24.
 */
public class CoreFragment extends Fragment implements CommonContract.View {
    private static final String TAG = "CoreFragment";
    public static CoreFragment newInstance(String url) {
        CoreFragment mainFragment = new CoreFragment();
        Bundle bundle = new Bundle();
        bundle.putString("base_url", url);
        mainFragment.setArguments(bundle);
        return mainFragment;
    }

    private MissEvanSdk mSdk;
    private SharedPreferences mSharedPreferences;
    private CommonContract.Presenter mPresenter;

    private View mView;
    private ProgressBar mProgressBar;
    private CoordinatorLayout mLayout;
    private RecyclerView mPostList;
    private PostAdapter mPostAdapter;
    private View mEmptyView;
    private Snackbar mErrorSnackbar;
    private EditAlertDialog mEditAlertDialog;

    private InitCallbackListener mInitCallbackListener = new InitCallbackListener() {
        @Override
        public void onSuccess() {
            Log.d("yjnull", "MissEvan SDK 初始化成功");
            showTips("MissEvan SDK 初始化成功");
        }

        @Override
        public void onFailed() {
            Log.d("yjnull", "MissEvan SDK 初始化失败");
            showTips("MissEvan SDK 初始化失败");
        }
    };

    private ExitCallbackListener mExitCallbackListener = new ExitCallbackListener() {
        @Override
        public void onExit() {
            makeToast("退出游戏");
            getActivity().finish();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSdk = MissEvanSdk.initialize(true, getActivity(),
                "1", "1", "1",
                "JqCB4Jun1pWYaoiTbhC2a$0icKv2JSsu", mInitCallbackListener, mExitCallbackListener);
        Bundle args = getArguments();
        if (args != null) {
            String base_url = args.getString("base_url");
            mSdk.changeBaseUrl(base_url, mInitCallbackListener);
        }
        mSharedPreferences = getActivity().getSharedPreferences("demouser", Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_splash, container, false);
        }
        mProgressBar = mView.findViewById(R.id.progressBar);
        mLayout = mView.findViewById(R.id.layout_coordinator);
        mPostList = mView.findViewById(R.id.post_list);
        mEmptyView = mView.findViewById(R.id.empty_view);

        mPostAdapter = new PostAdapter();
        mPostAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                onLoadRecyclerClick(mPostAdapter.getData(position));
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPostList.setLayoutManager(layoutManager);
        mPostList.setAdapter(mPostAdapter);

        mView.findViewById(R.id.load_posts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideError();
            }
        });

        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mEditAlertDialog != null) {
            mEditAlertDialog.dismiss();
        }
    }

    /** {@link com.missevan.game.demo.data.local.LocalCoreDataSource#getPosts(DataSource.GetPostsCallback)} */
    private void onLoadRecyclerClick(Post post) {
        switch (post.type) {
            case 1:
                mSdk.login(new CallbackListenerAdapter() {
                    @Override
                    public void onSuccess(Bundle success) {
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
                        mSharedPreferences.edit()
                                .clear()
                                .putString(MissEvanConstants.USER_NAME, userName)
                                .putString(MissEvanConstants.UID, uid)
                                .apply();
                    }
                });
                break;
            case 2:
                mSdk.notifyZone("1002", "猫耳区服-A", "222", "怪兽", new CallbackListenerAdapter() {
                    @Override
                    public void onSuccess(Bundle success) {
                        String string = success.getString(MissEvanConstants.TIPS);
                        makeToast(string);
                    }
                });
                break;
            case 3:
                if (mEditAlertDialog == null) {
                    mEditAlertDialog = new EditAlertDialog(getContext())
                            .setTitle("支付")
                            .setEditHint("请输入金额，单位(分)")
                            .setEditTextInputType(InputType.TYPE_CLASS_NUMBER)
                            .setConfirmListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String money = mEditAlertDialog.getText();
                                    if (TextUtils.isEmpty(money)) {
                                        makeToast("金额不能为空");
                                        return;
                                    }

                                    goPay(money);
                                    mEditAlertDialog.dismiss();
                                }
                            })
                            .setCancelListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mEditAlertDialog.dismiss();
                                }
                            });
                }
                mEditAlertDialog.show();
                break;
            case 4:
                mSdk.logout(new CallbackListenerAdapter() {
                    @Override
                    public void onSuccess(Bundle success) {
                        mSharedPreferences.edit().clear().apply();
                        makeToast(success.getString(MissEvanConstants.TIPS, "获取 Bundle Tips 失败"));
                    }
                });
                break;
            case 5:
                mSdk.createRole("1001", "猫耳区服-A", "222", "怪兽", new CallbackListenerAdapter() {
                    @Override
                    public void onSuccess(Bundle success) {
                        String string = success.getString(MissEvanConstants.TIPS);
                        makeToast(string);
                    }
                });
                break;
            case 6:
                mSdk.isLogin(new CallbackListenerAdapter() {
                    @Override
                    public void onSuccess(Bundle success) {
                        boolean logined = success.getBoolean("is_login", false);
                        makeToast("是否登录: " + logined);
                    }
                });
                break;
            case 7:
                mSdk.isRealNameAuth(new CallbackListenerAdapter() {
                    @Override
                    public void onSuccess(Bundle success) {
                        boolean logined = success.getBoolean(MissEvanConstants.IS_REALNAME_AUTH, false);
                        makeToast("是否实名认证: " + logined);
                    }
                });
                break;
            case 8:
                mSdk.getUserInfo(new CallbackListenerAdapter() {
                    @Override
                    public void onSuccess(Bundle success) {
                        String uid = success.getString("uid");
                        String userName = success.getString("username");
                        String access_token = success.getString("access_token");
                        String expire_times = success.getString("expire_times");
                        String refresh_token = success.getString("refresh_token");
                        String avatar = success.getString("avatar");
                        String s_avatar = success.getString("s_avatar");
                        makeToast("uid: " + uid
                                + ", username: " + userName
                                + ", access_token: " + access_token
                                + ", expire_times: " + expire_times
                                + ", refresh_token: " + refresh_token
                                + ", avatar: " + avatar
                                + ", s_avatar: " + s_avatar
                        );
                    }
                });
                break;
            case 9:
                mSdk.exit(new ExitCallbackListener() {
                    @Override
                    public void onExit() {
                        makeToast("退出游戏");
                        getActivity().finish();
                    }
                });
                break;
            default:
                break;
        }


    }

    private void goPay(String money) {
        String uid = mSharedPreferences.getString(MissEvanConstants.UID, "");
        String out_trade_number = String.valueOf(System.currentTimeMillis());
        int fee = Integer.parseInt(money);
        int gameMoney = (int) ((fee / 100.0) * 100);
        String notifyUrl = "";
        String data = String.valueOf(gameMoney) + String.valueOf(fee) + notifyUrl + out_trade_number;
        String order_sign = MD5.sign(data, "YY7J28iu2UOpiJH8IOh89HoHSvORQv5w78HJJYsdfs9s8SH89ju8J");
        mSdk.pay(uid, gameMoney, "金币", "最强商城", out_trade_number, fee, 222,
                "怪兽", "hahahaha", notifyUrl, order_sign, new OrderCallbackListener() {
                    @Override
                    public void onSuccess(String out_trade_no, String bs_trade_no) {
                        makeToast("支付成功 : " + bs_trade_no);
                    }

                    @Override
                    public void onFailed(String out_trade_no, MissEvanSdkError arg0) {
                        makeToast("支付失败 : " + arg0.getErrorCode() + arg0.getErrorMessage());
                    }

                    @Override
                    public void onError(String out_trade_no, MissEvanSdkError arg0) {
                        makeToast("支付错误 : " + arg0.getErrorMessage());
                    }
                });
    }

    // --- mvp -------------------------------------------------------------------------------------
    @Override
    public void setPresenter(CommonContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setLoadingPosts(boolean isLoading) {
        mProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setPosts(List<Post> posts) {
        mPostAdapter.setPosts(posts);
        if (posts == null || posts.size() < 1) {
            mPostList.setVisibility(View.GONE);
        } else {
            mPostList.setVisibility(View.VISIBLE);

        }
    }

    public void showTips(String tips) {
        mErrorSnackbar = Snackbar.make(mLayout, tips, Snackbar.LENGTH_LONG);
        mErrorSnackbar.show();
    }

    public void makeToast(String tips) {
        showError(tips, null);
    }

    @Override
    public void showError(String title, String error) {
        Log.e(TAG, error != null ? error : title);
        mErrorSnackbar = Snackbar.make(mLayout, title, Snackbar.LENGTH_INDEFINITE);
        mErrorSnackbar.show();
    }

    @Override
    public void hideError() {
        if (mErrorSnackbar != null) {
            mErrorSnackbar.dismiss();
        }
    }

    @Override
    public void showNoPostsMessage(boolean showMessage) {
        mEmptyView.setVisibility(showMessage ? View.VISIBLE : View.GONE);
    }

    public abstract class CallbackListenerAdapter implements CallbackListener {

        @Override
        public void onFailed(MissEvanSdkError failed) {
            makeToast("onFailed -> " + failed.getErrorCode() + " : " + failed.getErrorMessage());
        }

        @Override
        public void onError(MissEvanSdkError error) {
            makeToast("onError -> " + error.getErrorCode() + " : " + error.getErrorMessage());
        }
    }
}

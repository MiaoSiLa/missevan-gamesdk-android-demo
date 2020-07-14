package com.missevan.game.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ProgressBar;

import com.missevan.game.demo.model.Post;
import com.missevan.game.demo.ui.EditAlertDialog;
import com.missevan.game.demo.ui.PostAdapter;

import java.util.List;

/**
 * Created by yangya on 2019-10-23.
 */
public class SplashFragment extends Fragment implements CommonContract.View {
    private static final String TAG = SplashFragment.class.getSimpleName();
    private CommonContract.Presenter mPresenter;

    private ProgressBar mProgressBar;
    private CoordinatorLayout mLayout;
    private RecyclerView mPostList;
    private PostAdapter mPostAdapter;
    private View mEmptyView;
    private Snackbar mErrorSnackbar;
    private EditAlertDialog mEditAlertDialog;

    public SplashFragment() {
        // Required empty public constructor
    }

    public static SplashFragment newInstance() {
        return new SplashFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_splash, container, false);
        mProgressBar = layout.findViewById(R.id.progressBar);
        mLayout = layout.findViewById(R.id.layout_coordinator);
        mPostList = layout.findViewById(R.id.post_list);
        mEmptyView = layout.findViewById(R.id.empty_view);

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

        layout.findViewById(R.id.load_posts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideError();
            }
        });
        return layout;
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

    private void onLoadRecyclerClick(Post post) {
        switch (post.type) {
            case 1:
            case 2:
                // 线上
                // UAT
                Intent intent = new Intent(getActivity(), CoreActivity.class);
                intent.putExtra("base_url", post.message);
                startActivity(intent);
                break;
            case 3:
                // 自定义
                initDialog();
                mEditAlertDialog.show();
                break;
            default:
                break;
        }
    }

    private void initDialog() {
        if (mEditAlertDialog == null) {
            mEditAlertDialog = new EditAlertDialog(getContext());
            mEditAlertDialog.setEditTextInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI)
                    .setConfirmListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = mEditAlertDialog.getText();
                    if (!URLUtil.isNetworkUrl(text)) {
                        showError("url is invalid!", "");
                        return;
                    }
                    Intent intent = new Intent(getActivity(), CoreActivity.class);
                    intent.putExtra("base_url", text);
                    startActivity(intent);
                    mEditAlertDialog.dismiss();
                }
            }).setCancelListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditAlertDialog.dismiss();
                }
            });
        }
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
}

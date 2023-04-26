package com.missevan.game.demo;

import com.missevan.game.demo.data.DataSource;
import com.missevan.game.demo.model.Post;

import java.util.List;

/**
 * Created by yangya on 2019-10-23.
 */
public class CommonPresenter implements CommonContract.Presenter {
    private final DataSource mData;
    private final CommonContract.View mView;

    public CommonPresenter(DataSource mData, CommonContract.View mView) {
        this.mData = mData;
        this.mView = mView;
    }

    @Override
    public void start() {
        loadPosts();
    }

    @Override
    public void loadPosts() {
        mView.setLoadingPosts(true);

        mData.getPosts(new DataSource.GetPostsCallback() {
            @Override
            public void onPostsLoaded(List<Post> posts) {
                mView.setLoadingPosts(false);
                mView.hideError();
                if (posts != null && posts.size() > 0) {
                    mView.showNoPostsMessage(false);
                    mView.setPosts(posts);
                } else {
                    mView.showNoPostsMessage(true);
                }
            }

            @Override
            public void onPostsNotAvailable(String error, Throwable cause) {
                mView.setLoadingPosts(false);
                String fullError = error;
                if (cause != null) {
                    // Append the exception to the error
                    StringBuilder buffer = new StringBuilder();
                    buffer.append(error);
                    buffer.append('\n');
                    buffer.append(cause.toString());
                    fullError = buffer.toString();
                }
                mView.showError(error, fullError);
                mView.showNoPostsMessage(true);
                mView.setPosts(null);

            }
        });
    }

    @Override
    public void onLoadPostImageError(String title, Exception e) {
        String fullError = title;
        if (e != null) {
            // Append the exception to the error
            StringBuilder buffer = new StringBuilder();
            buffer.append(title);
            buffer.append('\n');
            buffer.append(e.toString());
            fullError = buffer.toString();
        }
        mView.showError(title, fullError);
    }
}

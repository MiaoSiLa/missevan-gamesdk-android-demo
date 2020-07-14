package com.missevan.game.demo.data;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.missevan.game.demo.model.Post;

import java.util.List;

public interface DataSource {

    interface GetPostsCallback {
        void onPostsLoaded(List<Post> posts);

        void onPostsNotAvailable(String error, Throwable cause);
    }

    interface GetImageCallback {
        void onImageLoaded(Bitmap image);

        void onImageNotAvailable(String error);
    }


    void getPosts(@NonNull GetPostsCallback getPostsCallback);

    void getPostImage(@NonNull String url, @NonNull GetImageCallback getImageCallback);
}

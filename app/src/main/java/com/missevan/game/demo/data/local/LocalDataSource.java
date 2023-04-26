package com.missevan.game.demo.data.local;

import androidx.annotation.NonNull;

import com.missevan.game.demo.data.DataSource;
import com.missevan.game.demo.model.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangya on 2019-10-23.
 */
public class LocalDataSource implements DataSource {
    public static String DEFAULT_BASE_URL = "https://gamesdk.missevan.com/";
    public static String DEFAULT_UAT_BASE_URL = "https://gamesdk.uat.missevan.com/";

    @Override
    public void getPosts(@NonNull GetPostsCallback getPostsCallback) {
        List<Post> result = new ArrayList<>();
        result.add(new Post("线上", DEFAULT_BASE_URL, "", 1));
        result.add(new Post("自定义", "url 需满足如下格式 (http://xx.xx.xx/)", "", 3));
        getPostsCallback.onPostsLoaded(result);
    }

    @Override
    public void getPostImage(@NonNull String url, @NonNull GetImageCallback getImageCallback) {

    }
}

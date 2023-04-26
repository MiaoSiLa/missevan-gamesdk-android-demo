package com.missevan.game.demo.data.local;

import androidx.annotation.NonNull;

import com.missevan.game.demo.data.DataSource;
import com.missevan.game.demo.model.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangya on 2019-10-23.
 */
public class LocalCoreDataSource implements DataSource {
    @Override
    public void getPosts(@NonNull GetPostsCallback getPostsCallback) {
        List<Post> result = new ArrayList<>();
        result.add(new Post("登录", "", "", 1));
        result.add(new Post("通知区服", "", "", 2));
        result.add(new Post("支付", "", "", 3));
        result.add(new Post("注销登录", "", "", 4));
        result.add(new Post("创建角色", "", "", 5));
        result.add(new Post("是否登录", "", "", 6));
        result.add(new Post("是否实名认证", "", "", 7));
        result.add(new Post("获取用户信息", "", "", 8));
        result.add(new Post("退出游戏", "", "", 9));
        getPostsCallback.onPostsLoaded(result);
    }

    @Override
    public void getPostImage(@NonNull String url, @NonNull GetImageCallback getImageCallback) {

    }
}

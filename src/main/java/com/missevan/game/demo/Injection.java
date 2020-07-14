package com.missevan.game.demo;

import android.content.Context;
import android.support.annotation.NonNull;

import com.missevan.game.demo.data.DataSource;
import com.missevan.game.demo.data.local.LocalCoreDataSource;
import com.missevan.game.demo.data.local.LocalDataSource;

public class Injection {

    public static DataSource provideDataSource(@NonNull Context context) {
        return new LocalDataSource();
    }

    public static DataSource provideCoreDataSource(@NonNull Context context) {
        return new LocalCoreDataSource();
    }

}

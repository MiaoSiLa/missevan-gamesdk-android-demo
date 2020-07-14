package com.missevan.game.demo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.missevan.game.demo.data.DataSource;

/**
 * Created by yangya on 2019-10-23.
 */
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SplashFragment splashFragment =
                (SplashFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (splashFragment == null) {
            splashFragment = SplashFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.contentFrame, splashFragment)
                    .commit();
        }

        DataSource data = Injection.provideDataSource(this);
        CommonContract.Presenter presenter = new CommonPresenter(data, splashFragment);
        splashFragment.setPresenter(presenter);

        verifyPermissions();
    }

    private void verifyPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                    0x001);
        }
    }

}

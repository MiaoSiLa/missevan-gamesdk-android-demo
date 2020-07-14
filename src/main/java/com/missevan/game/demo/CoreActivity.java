package com.missevan.game.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.missevan.game.demo.data.DataSource;

/**
 * Created by yangya on 2019-10-24.
 */
public class CoreActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String base_url = getIntent().getStringExtra("base_url");
        setTitle(base_url);

        CoreFragment coreFragment = (CoreFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (coreFragment == null) {
            coreFragment = CoreFragment.newInstance(base_url);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.contentFrame, coreFragment)
                    .commit();
        }

        DataSource data = Injection.provideCoreDataSource(this);
        CommonContract.Presenter presenter = new CommonPresenter(data, coreFragment);
        coreFragment.setPresenter(presenter);
    }
}

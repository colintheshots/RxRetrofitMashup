package com.colintheshots.rxretrofitmashup;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.colintheshots.rxretrofitmashup.adapters.GistFilesAdapter;
import com.colintheshots.rxretrofitmashup.models.GistDetail;
import com.colintheshots.rxretrofitmashup.network.GitHubNetworkService;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends Activity
        implements GitHubNetworkService.GitHubCallback, ServiceConnection {
    @InjectView(R.id.listView)
    ListView mListView;

    private GitHubNetworkService mService;
    private boolean mBound;
    private String mGistVisible = "none";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }

    @Override
    protected void onDestroy() {
        getApplicationContext().unbindService(this);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, GitHubNetworkService.class);
        getApplicationContext().bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(this);
            mBound = false;
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        GitHubNetworkService.GitHubBinder binder = (GitHubNetworkService.GitHubBinder) iBinder;
        mService = binder.getService();
        if (GitHubNetworkService.GITHUB_PERSONAL_ACCESS_TOKEN.equals("XXX")) {
            Toast.makeText(getApplicationContext(), "GitHub Personal Access Token is Unset!", Toast.LENGTH_LONG).show();
        }
        mService.setCallback(this);
        mService.getGists();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mService.unsetCallback();
        mService = null;
    }

    @Override
    public void displayFiles(List<GistDetail> gistDetailList) {
        if (mListView!=null) {
            mListView.setAdapter(new GistFilesAdapter(MainActivity.this, gistDetailList));
        }
    }
}
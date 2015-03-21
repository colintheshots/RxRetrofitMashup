package com.colintheshots.rxretrofitmashup.network;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.colintheshots.rxretrofitmashup.models.Gist;
import com.colintheshots.rxretrofitmashup.models.GistDetail;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.GroupedObservable;

/**
 * Provides access to GitHub REST API through a service.
 *
 * While this service isn't necessary in this simple example
 * and a bound service is a poor choice, one may use a service
 * to cache returned data and provide it to any activity.
 *
 * If you choose to use an activity for your observables,
 * be sure to look at
 * AndroidObservable.bindActivity(Activity activity, Observable<T> source)
 *
 * From the code comments for this function:
 * "This helper will schedule the given sequence to be observed on the main
 * UI thread and ensure that no notifications will be forwarded to the
 * activity in case it is scheduled to finish. You should unsubscribe from
 * the returned Observable in onDestroy at the latest, in order to not leak
 * the activity or an inner subscriber."
 *
 * Created by colin.lee on 10/10/14.
 */
public class GitHubNetworkService extends Service {

    /** The GitHub REST API Endpoint */
    public final static String GITHUB_BASE_URL = "https://api.github.com";

    /** Set this variable to your GitHub personal access token */
    public final static String GITHUB_PERSONAL_ACCESS_TOKEN = "XXX";

    private GitHubClient mGitHubClient;
    private IBinder mBinder = new GitHubBinder();
    private GitHubCallback mCallback;

    /**
     * Retrofit interface to GitHub API methods
     */
    public interface GitHubClient {
        @GET("/gists")
        Observable<List<Gist>> gists();

        @GET("/gists/{id}")
        Observable<GistDetail> gist(@Path("id") String id);
    }

    /**
     * Callback interface to the activity
     */
    public interface GitHubCallback {
        void displayFiles(final List<GistDetail> gistDetailList);
    }

    public class GitHubBinder extends Binder {
        public GitHubNetworkService getService() {
            return GitHubNetworkService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mGitHubClient == null) {
            mGitHubClient = new RestAdapter.Builder()
                    .setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public void intercept(RequestFacade request) {
                            request.addHeader("Authorization", "token " + GITHUB_PERSONAL_ACCESS_TOKEN);
                        }
                    })
                    .setEndpoint(GITHUB_BASE_URL)
                    .setLogLevel(RestAdapter.LogLevel.HEADERS).build()
                    .create(GitHubClient.class);
        }
    }

    /**
     * Sets the callback for binding the service to the current activity.
     * @param c
     */
    public void setCallback(GitHubCallback c) {
        mCallback = c;
    }

    /**
     * Unsets the callback when an activity goes away to allow garbage collection and prevent callbacks from firing.
     */
    public void unsetCallback() {
        mCallback = null;
    }

    /**
     * Calls the GitHub REST API to access the list of gists on your account and calls the callback method to display them
     */
    public void getGists() {

        mGitHubClient.gists()
        .flatMap(Observable::from)
        .take(2) // take the first two gists
        .cache() // only request the list of gists once and then save it
        .groupBy(Gist::getId)
        .flatMap(stringGistGroupedObservable -> mGitHubClient.gist(stringGistGroupedObservable.getKey()))
        .timeout(5000, TimeUnit.MILLISECONDS)
        .retry(1) // retry once on errors
        .toList() // this will block, which we want
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(mCallback::displayFiles, Throwable::printStackTrace);
    }

}

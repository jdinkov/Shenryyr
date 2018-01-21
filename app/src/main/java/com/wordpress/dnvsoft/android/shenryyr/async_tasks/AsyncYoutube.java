package com.wordpress.dnvsoft.android.shenryyr.async_tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.wordpress.dnvsoft.android.shenryyr.R;
import com.wordpress.dnvsoft.android.shenryyr.models.YouTubeResult;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

abstract class AsyncYoutube extends AsyncTask<Void, String, YouTubeResult> {

    String accountEmail;
    protected YouTubeResult result;
    protected YouTube youtube;
    private TaskCompleted callback;
    private static WeakReference<Context> context;

    private static final int REQUEST_AUTHORIZATION = 1234;

    AsyncYoutube(Context c, TaskCompleted callback) {
        context = new WeakReference<>(c);
        this.callback = callback;
        result = new YouTubeResult();

        HttpRequestInitializer initializer;
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getAppContext(),
                Collections.singleton(YouTubeScopes.YOUTUBE))
                .setBackOff(new ExponentialBackOff());

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getAppContext());
        if (account != null) {
            accountEmail = account.getEmail();
            credential.setSelectedAccountName(accountEmail);
            initializer = credential;
        } else {
            initializer = new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest hr) throws IOException {
                    hr.getHeaders().set("X-Android-Package", getAppContext().getPackageName());
                    hr.getHeaders().set("X-Android-Cert", "2C091E9EA9C501D11C73A7F27C113E47EFACB3FD");
                }
            };
        }

        youtube = new YouTube.Builder(AndroidHttp.newCompatibleTransport(), JacksonFactory.getDefaultInstance(), initializer)
                .setApplicationName(getAppContext().getString(R.string.app_name))
                .build();
    }

    private static Context getAppContext() {
        return context.get();
    }

    @Override
    protected YouTubeResult doInBackground(Void... params) {
        try {
            result = DoItInBackground();
        } catch (UserRecoverableAuthIOException e) {
            startActivityForResult((Activity) getAppContext(), e.getIntent(), REQUEST_AUTHORIZATION, null);
        } catch (GoogleJsonResponseException ignored) {
            String message = null;
            if (ignored != null) {
                message = ignored.getStatusMessage();
            }
            publishProgress(message);
        } catch (IOException e) {
            String message = null;
            if (e != null) {
                message = e.getClass().getSimpleName();
            }
            publishProgress(message);
        }
        return result;
    }

    abstract YouTubeResult DoItInBackground() throws IOException;

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Toast.makeText(getAppContext(), values[0], Toast.LENGTH_LONG).show();
        result.setCanceled(true);
    }

    @Override
    protected void onPostExecute(YouTubeResult result) {
        callback.onTaskComplete(result);
    }
}

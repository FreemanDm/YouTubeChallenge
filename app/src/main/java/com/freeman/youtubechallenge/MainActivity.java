package com.freeman.youtubechallenge;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.freeman.youtubechallenge.data.AbstractExpandableDataProvider;
import com.freeman.youtubechallenge.data.ExpandableDataProviderFragment;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String FRAGMENT_TAG_DATA_PROVIDER = "data provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";
    public static ArrayList<PlaylistItem> playlist;
    public static WebView webView;
    public static RelativeLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.web_view);
        content = (RelativeLayout) findViewById(R.id.container);

        if (savedInstanceState == null) {
            new GetJsonData().execute();
        }

    }

    public AbstractExpandableDataProvider getDataProvider() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_DATA_PROVIDER);
        return ((ExpandableDataProviderFragment) fragment).getDataProvider();
    }

    class GetJsonData extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            Request request = new Request.Builder()
                    .url("http://www.razor-tech.co.il/hiring/youtube-api.json")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = (JsonObject) jsonParser.parse(response.body().string());
                    JsonArray jsonArray = jsonObject.getAsJsonArray("Playlists");

                    Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<PlaylistItem>>() {}.getType();
                    playlist = gson.fromJson(jsonArray, listType);
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                getSupportFragmentManager().beginTransaction()
                        .add(new ExpandableDataProviderFragment(), FRAGMENT_TAG_DATA_PROVIDER)
                        .commit();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new ExpandableFragment(), FRAGMENT_LIST_VIEW)
                        .commit();

            }
        }
    }

    public class VideoItem {
        public String Title,link,thumb;
    }

    public class PlaylistItem {
        public String ListTitle;
        public ArrayList<VideoItem> ListItems;
    }

    @Override
    public void onBackPressed() {
        if (webView.getVisibility() == View.VISIBLE) {
            webView.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
        }
        else
            super.onBackPressed();
    }
}

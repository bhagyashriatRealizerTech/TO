package com.techynotion.newsplanet.asyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.techynotion.newsplanet.OnTaskCompleted;
import com.techynotion.newsplanet.OnTaskCompletedDb;
import com.techynotion.newsplanet.Singlton;
import com.techynotion.newsplanet.Utils;
import com.techynotion.newsplanet.backend.DatabaseQuries;
import com.techynotion.newsplanet.model.NewsListModel;
import com.techynotion.newsplanet.model.TaskComplete;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class NewsListDatabaseAsyncTask extends AsyncTask<Void, Void,List <NewsListModel>>
{
    ProgressDialog dialog;
    StringBuilder resultLogin;
    NewsListModel newsListModel;
    Context myContext;
    private OnTaskCompletedDb callback;
    String[] categoryArray;
    List<NewsListModel> listmodel = new ArrayList<>();
    DatabaseQuries db;

    public NewsListDatabaseAsyncTask(String[] categoryArray, Context myContext, OnTaskCompletedDb cb)
    {
        this.categoryArray = categoryArray;
        this.myContext = myContext;
        this.callback = cb;
        this.db = new DatabaseQuries(myContext);
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected List<NewsListModel> doInBackground(Void... params) {

        for(int i=0;i<categoryArray.length;i++){
            List<NewsListModel> temp = new ArrayList<>();
            temp = db.getAllNews(categoryArray[i]);
            listmodel.addAll(temp);
        }
        return listmodel;
    }

    @Override
    protected void onPostExecute(List<NewsListModel> listmodel) {
        super.onPostExecute(listmodel);
         callback.onTaskCompleted("",listmodel);

    }

}

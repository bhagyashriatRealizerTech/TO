package com.techynotion.newsplanet.asyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.techynotion.newsplanet.OnTaskCompleted;
import com.techynotion.newsplanet.Utils;
import com.techynotion.newsplanet.model.RegistrationModel;
import com.techynotion.newsplanet.model.TaskComplete;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class NewsDetailAsyncTaskGet extends AsyncTask<Void, Void,StringBuilder>
{
    ProgressDialog dialog;
    StringBuilder resultLogin;
    String newsiId;
    Context myContext;
    private OnTaskCompleted callback;

    public NewsDetailAsyncTaskGet(String newsiId, Context myContext, OnTaskCompleted cb)
    {
        this.newsiId = newsiId;
        this.myContext = myContext;
        this.callback = cb;
    }

    @Override
    protected void onPreExecute() {
        dialog= ProgressDialog.show(myContext, "", "Please wait loading Comment..");
    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultLogin = new StringBuilder();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(myContext);
        String regId = preferences.getString("RegId", "");

        String registerUrl= Utils.NewsURL+"GetNewsDetails/"+newsiId+"/"+regId;
        //String my= Config.URL+"TeacherLogin/"+ uName + "/" +password;
        Log.d("URL", registerUrl);
        HttpGet httpget = new HttpGet(registerUrl);
        HttpClient client = new DefaultHttpClient();

        HttpResponse response = null;
        try{
            response = client.execute(httpget);
        StatusLine statusLine = response.getStatusLine();

            int statusCode = statusLine.getStatusCode();
            if(statusCode == 200)
            {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while((line=reader.readLine()) != null)
                {
                    resultLogin.append(line);
                }
            }
            else
            {
               // Log.e("Error", "Failed to Login");
            }
        }
        catch(ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            client.getConnectionManager().closeExpiredConnections();
            client.getConnectionManager().shutdown();
        }
        return resultLogin;
    }

    @Override
    protected void onPostExecute(StringBuilder stringBuilder) {
        super.onPostExecute(stringBuilder);
        dialog.dismiss();
        //Pass here result of async task
         TaskComplete complete = new TaskComplete();
         complete.setTitle("NewsDetail");
         callback.onTaskCompleted(stringBuilder.toString(),complete);

    }

}

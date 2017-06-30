package com.techynotion.newsplanet.asyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.techynotion.newsplanet.OnTaskCompleted;
import com.techynotion.newsplanet.Utils;
import com.techynotion.newsplanet.model.RegistrationModel;

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

public class RegistrationAsyncTaskPost extends AsyncTask<Void, Void,StringBuilder>
{
    ProgressDialog dialog;
    StringBuilder resultLogin;
    RegistrationModel registrationModel;
    Context myContext;
    private OnTaskCompleted callback;

    public RegistrationAsyncTaskPost(RegistrationModel registrationModel, Context myContext, OnTaskCompleted cb)
    {
        this.registrationModel = registrationModel;
        this.myContext = myContext;
        this.callback = cb;
    }

    @Override
    protected void onPreExecute() {
       // super.onPreExecute();
     // dialog= ProgressDialog.show(myContext, "", "Authenticating credentials...");

    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultLogin = new StringBuilder();

        String registerUrl= Utils.URL+"RegisterUser";
        Log.d("URL", registerUrl);
        HttpPost httpPost = new HttpPost(registerUrl);
        HttpClient client = new DefaultHttpClient();
        try
        {
            String json = "";
            StringEntity se = null;

            JSONObject jobj = new JSONObject();
            try {
                jobj.put("UserId", registrationModel.getUserId());
                jobj.put("Password", registrationModel.getPassword());
                jobj.put("FirstName", registrationModel.getFirstName());
                jobj.put("LastName",registrationModel.getLastName());
                jobj.put("EmailId", registrationModel.getEmailId());
                json = jobj.toString();
               /* jobj.put("Password", registrationModel.getPassword());
                jobj.put("FirstName", registrationModel.getFirstName());
                jobj.put("LastName",registrationModel.getLastName());*/

            } catch (JSONException e) {
                e.printStackTrace();
            }


            se = new StringEntity(json);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setEntity(se);

            HttpResponse response = client.execute(httpPost);
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
       // dialog.dismiss();
        //Pass here result of async task
         callback.onTaskCompleted(stringBuilder.toString(),null);

    }

}

package com.techynotion.newsplanet;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.techynotion.newsplanet.adapter.CommentListAdapter;
import com.techynotion.newsplanet.asyncTask.AddCommentAsyncTask;
import com.techynotion.newsplanet.asyncTask.NewsDetailAsyncTaskGet;
import com.techynotion.newsplanet.backend.DatabaseQuries;
import com.techynotion.newsplanet.model.CommentModel;
import com.techynotion.newsplanet.model.NewsListModel;
import com.techynotion.newsplanet.model.TaskComplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Dell on 1/21/2017.
 */
public class CommentActivity extends AppCompatActivity implements OnTaskCompleted  {

    EditText coment;
    TextView send;
    ListView comntList;
    List<CommentModel> commentList = new ArrayList<>();
    CommentListAdapter adapter;
    DatabaseQuries db;

    String newsID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_layout);

        getSupportActionBar().setTitle(Utils.actionBarTitle("Comments", CommentActivity.this));
        getSupportActionBar().show();

        coment = (EditText) findViewById(R.id.edtcomnt);
        send = (TextView) findViewById(R.id.btnSendText);
        comntList = (ListView) findViewById(R.id.lstcomnt);
        db = new DatabaseQuries(CommentActivity.this);
        newsID = getIntent().getExtras().getString("NewsID");


        NewsDetailAsyncTaskGet newsDetailAsyncTaskGet = new NewsDetailAsyncTaskGet(newsID,CommentActivity.this,CommentActivity.this);
        newsDetailAsyncTaskGet.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        initiateView();
    }

    public void initiateView(){
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(CommentActivity.this);
        String uname = sharedpreferences.getString("UserName","");
        if(uname.equalsIgnoreCase("Guest User")){
            coment.setVisibility(View.GONE);
            send.setVisibility(View.GONE);

        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(coment.getText().toString().trim().length()>0) {
                    if (Utils.isConnectingToInternet(CommentActivity.this)) {

                        AddCommentAsyncTask addCommentAsyncTask = new AddCommentAsyncTask(newsID,coment.getText().toString().trim(),CommentActivity.this,CommentActivity.this);
                        addCommentAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                    else {
                        Utils.alertDialog(CommentActivity.this, "Network Error", "You are not connected to internet");
                    }
                }
                else {
                    Utils.alertDialog(CommentActivity.this, "Please enter comment", "Comment");
                }
            }
        });
    }

    @Override
    public void onTaskCompleted(String s, TaskComplete object) {

        String json = s;
        if(object.getTitle().equalsIgnoreCase("NewsDetail")){
            JSONObject obj = null;
            try {
                obj = new JSONObject(s);
                NewsListModel newsListModel = new NewsListModel();
                newsListModel.setCategoryId(obj.getString("CategoryId"));
                newsListModel.setCreatedTs(obj.getString("CreatedTs"));
                newsListModel.setNewsById(obj.getString("NewsById"));
                newsListModel.setNewsDescription(obj.getString("NewsDescription"));
                newsListModel.setNewsId(obj.getString("NewsId"));
                newsListModel.setNewsPhotoUrl(obj.getString("NewsPhotoUrl"));
                newsListModel.setNewsTitle(obj.getString("NewsTitle"));
                newsListModel.setLikeCount(obj.getString("LikeCount"));
                newsListModel.setDisLikeCount(obj.getString("DisLikeCount"));
                newsListModel.setSelfLike(obj.getBoolean("selfLike"));
                newsListModel.setSelfDisLike(obj.getBoolean("selfDisLike"));

                JSONArray jsonArray = obj.getJSONArray("commentsList");
                for(int i=0;i<jsonArray.length();i++){
                    CommentModel commentModel = new CommentModel();
                    JSONObject jobj = jsonArray.getJSONObject(i);
                    commentModel.setUsername(jobj.getString("CommentByUserName"));
                    commentModel.setComment(jobj.getString("CommentText"));
                    commentModel.setProfile(jobj.getString("CommentByThumbnailUrl"));
                    commentModel.setTimeStamp(jobj.getString("TimeStamp"));

                    commentList.add(commentModel);
                }

                db.updateNews(newsListModel);
            if(commentList.size() > 0) {
                adapter = new CommentListAdapter(CommentActivity.this, commentList);
                comntList.setAdapter(adapter);
            }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        else if(object.getTitle().equalsIgnoreCase("Comment")){
            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(CommentActivity.this);
            String uname = sharedpreferences.getString("UserName", "");
            String url = sharedpreferences.getString("ProfilePicUrl","");
            CommentModel commentModel = new CommentModel();
            commentModel.setUsername(uname);
            commentModel.setComment(coment.getText().toString());
            commentModel.setProfile(url);
            commentModel.setTimeStamp(android.text.format.DateFormat.format("yyyy-MM-dd",new Date()).toString());
            commentList.add(commentModel);

            coment.setText("");
            if(commentList.size() > 0) {
                adapter = new CommentListAdapter(CommentActivity.this, commentList);
                comntList.setAdapter(adapter);
            }

        }
    }
}

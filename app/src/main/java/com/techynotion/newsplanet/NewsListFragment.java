package com.techynotion.newsplanet;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.techynotion.newsplanet.adapter.NewsListAdapter;
import com.techynotion.newsplanet.asyncTask.NewsListAsyncTaskPost;
import com.techynotion.newsplanet.asyncTask.NewsListDatabaseAsyncTask;
import com.techynotion.newsplanet.backend.DatabaseQuries;
import com.techynotion.newsplanet.model.NewsListModel;
import com.techynotion.newsplanet.model.RegistrationModel;
import com.techynotion.newsplanet.model.TaskComplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Dell on 11/19/2016.
 */
public class NewsListFragment extends Fragment implements OnTaskCompletedDb,OnTaskCompleted {

    List<NewsListModel> listmodel = new ArrayList<>();
    ListView lisview;
    ProgressWheel loading;
    MessageResultReceiver resultReceiver;
    DatabaseQuries db;
    private TextView favourite,actionBarTitle;
    private boolean isFav;
    String title;
    NewsListAdapter newsListAdapter;
    int pos,top;
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));

        final View rootView = inflater.inflate(R.layout.news_layout, container, false);

        lisview  = (ListView)rootView.findViewById(R.id.newslist);
        loading = (ProgressWheel)rootView.findViewById(R.id.loading);
        Singlton.setNewsList(loading);
        listmodel = new ArrayList<>();
        title = getArguments().getString("Category");
        db = new DatabaseQuries(getActivity());
        isFav = db.getFav(Utils.getCategory(title).toString());
        pos = 0;
        top = 0;

        ActionBar actionBar = ((DrawerActivity) getActivity()).getSupportActionBar();
        favourite = (TextView)actionBar.getCustomView().findViewById(R.id.txt_fav);
        actionBarTitle = (TextView)actionBar.getCustomView().findViewById(R.id.txtTitle);
        actionBarTitle.setText(title);

        Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/fontawesome.ttf");
        favourite.setTextColor(getResources().getColor(R.color.white));
      //  favourite.setText("Hello");
        favourite.setText(getResources().getString(R.string.favorite));
        favourite.setTypeface(face);

        if(title.equalsIgnoreCase("AllNews") || title.equalsIgnoreCase("Favorites") ){
            favourite.setVisibility(View.VISIBLE);
            favourite.setTextColor(getResources().getColor(R.color.colorPrimary));
            favourite.setEnabled(false);
        }
        else {

            favourite.setVisibility(View.VISIBLE);
            favourite.setEnabled(true);
            if (isFav) {
                favourite.setTextColor(getResources().getColor(R.color.red));

            } else {
                favourite.setTextColor(getResources().getColor(R.color.white));

            }
        }

        actionBar.show();


        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isFav){
                    isFav = false;
                    favourite.setTextColor(getResources().getColor(R.color.white));
                    db.deleteFav(Utils.getCategory(title).toString());
                }else {
                    isFav = true;
                    favourite.setTextColor(getResources().getColor(R.color.red));
                    db.insertFav(Utils.getCategory(title).toString());
                }

            }
        });

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String tempState = sharedpreferences.getString("FetchNewsList","");
        if(tempState.equalsIgnoreCase("false")) {

            if (title.equalsIgnoreCase("Favorites")) {

                String catArr[] = db.getFav();
                if (catArr != null) {
                    if (catArr.length > 0) {
                        NewsListDatabaseAsyncTask newsListDatabaseAsyncTask = new NewsListDatabaseAsyncTask(catArr, getActivity(), NewsListFragment.this);
                        newsListDatabaseAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }
            } else {
                String catArr[] = new String[1];
                catArr[0] = Utils.getCategory(title);
                if (catArr.length > 0) {
                    NewsListDatabaseAsyncTask newsListDatabaseAsyncTask = new NewsListDatabaseAsyncTask(catArr, getActivity(), NewsListFragment.this);
                    newsListDatabaseAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        }

        resultReceiver = new MessageResultReceiver(null);
        Singlton obj = Singlton.getInstance();
        obj.setResultReceiver(resultReceiver);

        lisview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                View v = lisview.getChildAt(0);
                top = (v == null) ? 0 : (v.getTop() - lisview.getPaddingTop());
                pos = lisview.getFirstVisiblePosition();

                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("Title", listmodel.get(position).getNewsTitle());
                intent.putExtra("Desc", listmodel.get(position).getNewsDescription());
                intent.putExtra("CategoryId", listmodel.get(position).getCategoryId());
                intent.putExtra("NewsId", listmodel.get(position).getNewsId());
                intent.putExtra("NewsbyId", listmodel.get(position).getNewsById());
                intent.putExtra("Like", listmodel.get(position).getLikes());
                intent.putExtra("Comments", listmodel.get(position).getComments());
                intent.putExtra("NewsPic", listmodel.get(position).getNewsPhotoUrl());
                intent.putExtra("Islike", listmodel.get(position).isSelfLike());
                intent.putExtra("IsDislike", listmodel.get(position).isSelfDisLike());
                intent.putExtra("LikeCount", listmodel.get(position).getLikeCount());
                intent.putExtra("DislikeCount", listmodel.get(position).getDisLikeCount());

                startActivity(intent);
            }
        });

        return rootView;

    }



    @Override
    public void onTaskCompleted(String s, List<NewsListModel> listmodel1) {

        loading.setVisibility(View.GONE);


        listmodel = listmodel1;
        if(listmodel.size() > 0){
            lisview.setVisibility(View.VISIBLE);
            Collections.reverse(listmodel);
            newsListAdapter = new NewsListAdapter(getActivity(),listmodel);

            lisview.setAdapter(newsListAdapter);
            lisview.setSelectionFromTop(pos,top);


        }
        else {
            lisview.setVisibility(View.GONE);
        }
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String version = sharedpreferences.getString("AppVersion","");
        String appversion = BuildConfig.VERSION_NAME;

            if(appversion.equalsIgnoreCase(version)){

            }
            else  {

                Utils.updateAlertDialog(getActivity(),"MahaEarth Update",
                        "In order to continue, you must update MahaEarth now.This should only take a few moments.");
            }
    }


    public void changePage(Bundle b){

        loading.setVisibility(View.VISIBLE);
        title = b.getString("Category");

        actionBarTitle.setText(title);

    }

    @Override
    public void onTaskCompleted(String s, TaskComplete object) {
         if(object.getTitle().equalsIgnoreCase("Newslist")){

           // db.deleteAllData();
             SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
             SharedPreferences.Editor edit = sharedpreferences.edit();
             String version = null;
            try {
                JSONArray rootArr = new JSONArray(s);
                for(int i=0;i<rootArr.length();i++){
                    JSONObject obj = rootArr.getJSONObject(i);
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

                    db.insertNews(newsListModel);
                    version = obj.getString("AppLink");
                }
                String appLink = "https://play.google.com/store/apps/details?id=" + getActivity().getPackageName()+"&hl=en";

                if(version != null)
                    edit.putString("Version",version);
                    edit.putString("Link",appLink);
                    edit.apply();

                if (title.equalsIgnoreCase("Favorites")) {

                    String catArr[] = db.getFav();
                    if (catArr != null) {
                        if (catArr.length > 0) {
                            NewsListDatabaseAsyncTask newsListDatabaseAsyncTask = new NewsListDatabaseAsyncTask(catArr, getActivity(), NewsListFragment.this);
                            newsListDatabaseAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                        else {
                            loading.setVisibility(View.GONE);
                        }
                    }
                } else {
                    String catArr[] = new String[1];
                    catArr[0] = Utils.getCategory(title);
                    if (catArr.length > 0) {
                        NewsListDatabaseAsyncTask newsListDatabaseAsyncTask = new NewsListDatabaseAsyncTask(catArr, getActivity(), NewsListFragment.this);
                        newsListDatabaseAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
                loading.setVisibility(View.GONE);

            }
        }
    }

    class UpdateUI implements Runnable {
        String update;
        Bundle data;
        public UpdateUI(String update,Bundle data) {

            this.update = update;
            this.data = data;
        }

        public void run() {
            if(update.equals("UpdateList")) {
                changePage(data);
                }

            }
        }


    @Override
    public void onResume() {

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor edit = sharedpreferences.edit();
        String tempState = sharedpreferences.getString("FetchNewsList","");
        if(tempState.equalsIgnoreCase("true")){
            NewsListModel newsListModel = new NewsListModel();

            newsListModel.setSearchText("");
            newsListModel.setFromdate(db.getLastNewsTime());

            if (Utils.isConnectingToInternet(getActivity())) {
                loading.setVisibility(View.VISIBLE);
                NewsListAsyncTaskPost newsListAsyncTaskPost = new NewsListAsyncTaskPost(newsListModel, getActivity(), NewsListFragment.this);
                newsListAsyncTaskPost.execute();
            } else {
                loading.setVisibility(View.GONE);
                Utils.alertDialog(getActivity(), "Network Error", "You are not connected to internet");
            }

        }
        edit.putString("FetchNewsList", "true");
        edit.commit();

        super.onResume();
    }

    @SuppressLint("ParcelCreator")
    class MessageResultReceiver extends ResultReceiver{
        public MessageResultReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if(resultCode == 100){
                getActivity().runOnUiThread(new UpdateUI("UpdateList",resultData));

            }
        }
    }

}

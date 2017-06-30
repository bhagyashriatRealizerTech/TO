package com.techynotion.newsplanet;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.techynotion.newsplanet.asyncTask.LikeDislikeAsyncTaskPost;
import com.techynotion.newsplanet.backend.DatabaseQuries;
import com.techynotion.newsplanet.model.LikeDislikeModel;
import com.techynotion.newsplanet.model.NewsListModel;
import com.techynotion.newsplanet.model.TaskComplete;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Dell on 1/13/2017.
 */
public class ParallaxToolbarScrollViewActivity extends AppCompatActivity implements ObservableScrollViewCallbacks, OnTaskCompleted{


    private View mToolbarView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;

    TextView heading,newsdetail,likecount,dislikecount,comment,fav;
    ImageView newspic,like,dislike;
    AVLoadingIndicatorView avLoadingIndicatorView;
    LinearLayout rootlayout;
    boolean isLike,isDislike,isFav = false;
    int likC,dislikeC;
    String catID,newsID;
    DatabaseQuries db;
    FrameLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(ParallaxToolbarScrollViewActivity.this));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.news_detail_layout);

        //setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().hide();


        mToolbarView = findViewById(R.id.toolbar);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.colorPrimary)));

        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);

        db = new DatabaseQuries(ParallaxToolbarScrollViewActivity.this);
        initiateView();


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(R.color.colorPrimary);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));

        ViewHelper.setTranslationY(newspic, scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }


    public void initiateView()
    {
        heading = (TextView) findViewById(R.id.txt_news_heading);
        newsdetail = (TextView) findViewById(R.id.txt_newsdetail);
        newspic = (ImageView) findViewById(R.id.iv_newspic);
        newspic.setVisibility(View.GONE);
        likecount = (TextView) findViewById(R.id.txtLikecount);
        dislikecount = (TextView) findViewById(R.id.disLikecount);
        like = (ImageView) findViewById(R.id.like);
        //fav = (TextView) findViewById(R.id.fav);
        dislike = (ImageView) findViewById(R.id.dislike);
        comment = (TextView) findViewById(R.id.txtComment);
        rootlayout = (LinearLayout) findViewById(R.id.likeframe);
        avLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.avi);
        avLoadingIndicatorView.setVisibility(View.VISIBLE);
        Bundle b = getIntent().getExtras();


        Typeface face= Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/fontawesome.ttf");
        // fav.setTypeface(face);
        Typeface face1= Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/font.ttf");
        //heading.setTypeface(face1);
       // newsdetail.setTypeface(face1);


        isLike = b.getBoolean("Islike");
        isDislike = b.getBoolean("IsDislike");
        catID = b.getString("CategoryId");
        newsID = b.getString("NewsId");

        heading.setText(b.getString("Title"));
        String urlString = b.getString("NewsPic");
        likC = Integer.valueOf(b.getString("LikeCount"));
        dislikeC = Integer.valueOf(b.getString("DislikeCount"));

        setLikeUnlike();
        likecount.setText("" + likC);
        dislikecount.setText(""+dislikeC);
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<urlString.length();i++)
        {
            char c='\\';
            if (urlString.charAt(i) =='\\')
            {
                urlString.replace("\"","");
                sb.append("/");
            }
            else
            {
                sb.append(urlString.charAt(i));
            }
        }

        String newURL=sb.toString();

        if(newURL.trim().length() > 0) {
            if (!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                new GetImages(newURL, newspic,avLoadingIndicatorView, null, null, newURL.split("/")[newURL.split("/").length - 1]).execute(AsyncTask.THREAD_POOL_EXECUTOR, newURL);
            else {
                File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length - 1]);
                if (image != null) {
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                    avLoadingIndicatorView.setVisibility(View.GONE);
                    newspic.setVisibility(View.VISIBLE);
                    newspic.setImageBitmap(bitmap);
                }
            }
        }else {

        }


        newsdetail.setText(b.getString("Desc"));



        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(ParallaxToolbarScrollViewActivity.this);
                String uname = sharedpreferences.getString("UserName","");
                if(uname.equalsIgnoreCase("Guest User")){
                    Utils.alertDialog(ParallaxToolbarScrollViewActivity.this, "Please login to like News", "Like");
                }
                else {
                    if (isLike) {
                        Utils.alertDialog(ParallaxToolbarScrollViewActivity.this, "Like", "You have been already liked this News");

                    } else {
                        isLike = true;
                        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                R.drawable.liketeal);
                        like.setImageBitmap(icon);

                        likC = likC + 1;
                        likecount.setText(String.valueOf(likC));

                        NewsListModel news = db.getNews(newsID);
                        news.setLikeCount(String.valueOf(likC));
                        news.setSelfLike(true);
                        db.updateNews(news);
                        LikeDislikeModel likeDislikeModel = new LikeDislikeModel();
                        likeDislikeModel.setLikeDislike("1");
                        likeDislikeModel.setNewsId(newsID);
                        likeDislikeModel.setTimeStamp(android.text.format.DateFormat.format("yyyy-mm-dd",new Date()).toString());
                        if (Utils.isConnectingToInternet(ParallaxToolbarScrollViewActivity.this)) {

                            LikeDislikeAsyncTaskPost likeDislikeAsyncTaskPost = new LikeDislikeAsyncTaskPost(likeDislikeModel, ParallaxToolbarScrollViewActivity.this, ParallaxToolbarScrollViewActivity.this);
                            likeDislikeAsyncTaskPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            Utils.alertDialog(ParallaxToolbarScrollViewActivity.this, "Network Error", "You are not connected to internet");
                        }

                    }
                }

              }


        });

        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(ParallaxToolbarScrollViewActivity.this);
                String uname = sharedpreferences.getString("UserName","");
                if(uname.equalsIgnoreCase("Guest User")){
                    Utils.alertDialog(ParallaxToolbarScrollViewActivity.this, "Please login to dislike News", "Dislike");
                }
                else {

                    if (isDislike) {

                        Utils.alertDialog(ParallaxToolbarScrollViewActivity.this, "Like", "You have been already disliked this News");

                    } else {


                        final Typeface face = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/font.ttf");

                        LayoutInflater inflater = getLayoutInflater();
                        View dialoglayout = inflater.inflate(R.layout.addcomment, null);
                        Button submit = (Button) dialoglayout.findViewById(R.id.btn_submit);
                        Button cancel = (Button) dialoglayout.findViewById(R.id.btn_cancel);
                        final EditText comment = (EditText) dialoglayout.findViewById(R.id.edtcomment);
                        submit.setTypeface(face);
                        cancel.setTypeface(face);


                        final AlertDialog.Builder builder = new AlertDialog.Builder(ParallaxToolbarScrollViewActivity.this);
                        builder.setView(dialoglayout);

                        final AlertDialog alertDialog = builder.create();

                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isDislike = true;
                                Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                        R.drawable.liketeal);
                                dislike.setImageBitmap(icon);

                                dislikeC = dislikeC + 1;
                                dislikecount.setText(String.valueOf(dislikeC));

                                NewsListModel news = db.getNews(newsID);
                                news.setDisLikeCount(String.valueOf(dislikeC));
                                news.setSelfDisLike(true);
                                db.updateNews(news);
                                LikeDislikeModel likeDislikeModel = new LikeDislikeModel();
                                likeDislikeModel.setLikeDislike("0");
                                likeDislikeModel.setNewsId(newsID);
                                likeDislikeModel.setTimeStamp(new Date().toString());
                                if (Utils.isConnectingToInternet(ParallaxToolbarScrollViewActivity.this)) {

                                    LikeDislikeAsyncTaskPost likeDislikeAsyncTaskPost = new LikeDislikeAsyncTaskPost(likeDislikeModel, ParallaxToolbarScrollViewActivity.this, ParallaxToolbarScrollViewActivity.this);
                                    likeDislikeAsyncTaskPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                } else {
                                    Utils.alertDialog(ParallaxToolbarScrollViewActivity.this, "Network Error", "You are not connected to internet");
                                }

                            }
                        });

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });

                        alertDialog.show();

                    }
                }
            }
        });

        root = (FrameLayout) findViewById(R.id.rootview);

        newsdetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                rootlayout.setVisibility(View.VISIBLE);
                Thread timerThread = new Thread(){
                    public void run(){
                        try{
                            sleep(3000);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }finally{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rootlayout.setVisibility(View.GONE);
                                }
                            });

                        }
                    }
                };
                timerThread.start();
                return false;
            }
        });



        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Utils.isConnectingToInternet(ParallaxToolbarScrollViewActivity.this)) {
                    Intent intent = new Intent(ParallaxToolbarScrollViewActivity.this, CommentActivity.class);
                    intent.putExtra("NewsID", newsID);
                    startActivity(intent);
                }
                else {

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

                db.updateNews(newsListModel);
                isLike = newsListModel.selfLike;
                isDislike = newsListModel.isSelfDisLike();

                setLikeUnlike();
                likecount.setText(String.valueOf(newsListModel.getLikeCount()));
                dislikecount.setText(String.valueOf(newsListModel.getDisLikeCount()));


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        else if(object.getTitle().equalsIgnoreCase("LikeDislike")) {
            if (json != null) {
                if (!TextUtils.isEmpty(json)) {
                    try {
                        JSONObject rootObj = new JSONObject(json);
                        isLike = rootObj.getBoolean("IsLike");
                        isDislike = rootObj.getBoolean("IsDislike");
                        NewsListModel newsListModel = db.getNews(newsID);
                        newsListModel.setSelfLike(isLike);
                        newsListModel.setSelfDisLike(isDislike);
                        db.updateNews(newsListModel);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public void setLikeUnlike(){
        if(isLike){
            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.liketeal);
            like.setImageBitmap(icon);
        }
        else {
            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.likegrey);
            like.setImageBitmap(icon);
        }

        if(isDislike){
            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.dislikeorange);
            dislike.setImageBitmap(icon);
        }
        else {
            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.dislikegrey);
            dislike.setImageBitmap(icon);
        }

    }
}

package com.techynotion.newsplanet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
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
import java.util.Date;

/**
 * Created by Dell on 1/13/2017.
 */
public class NewsDetailActivity extends AppCompatActivity implements ObservableScrollViewCallbacks, OnTaskCompleted{


    private View mToolbarView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;
    private FloatingActionMenu menuNews;
    private FloatingActionButton likeButton,dislikeButton,commentButton,shareButton;
    TextView heading,newsdetail;
    ImageView newspic;
    AVLoadingIndicatorView avLoadingIndicatorView;
    LinearLayout rootlayout;
    boolean isLike,isDislike = false;
    int likC,dislikeC;
    String catID,newsID;
    DatabaseQuries db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(NewsDetailActivity.this));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.news_detail_layout1);
        getSupportActionBar().hide();


        mToolbarView = findViewById(R.id.toolbar);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.colorPrimary)));

        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);

        db = new DatabaseQuries(NewsDetailActivity.this);
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
        rootlayout = (LinearLayout) findViewById(R.id.likeframe);
        avLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.avi);
        avLoadingIndicatorView.setVisibility(View.VISIBLE);
        menuNews = (FloatingActionMenu) findViewById(R.id.menu_news) ;
        likeButton = (FloatingActionButton) findViewById(R.id.fab_like);
        dislikeButton = (FloatingActionButton) findViewById(R.id.fab_dislike);
        commentButton = (FloatingActionButton) findViewById(R.id.fab_comment);
        shareButton = (FloatingActionButton) findViewById(R.id.fab_share);

        Bundle b = getIntent().getExtras();

        isLike = b.getBoolean("Islike");
        isDislike = b.getBoolean("IsDislike");
        catID = b.getString("CategoryId");
        newsID = b.getString("NewsId");

        heading.setText(b.getString("Title"));
        String urlString = b.getString("NewsPic");
        likC = Integer.valueOf(b.getString("LikeCount"));
        dislikeC = Integer.valueOf(b.getString("DislikeCount"));

        setLikeUnlike();
        likeButton.setLabelText("" + likC);
        dislikeButton.setLabelText(""+dislikeC);

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

        menuNews.open(true);

        menuNews.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if(opened){
                }
                else {

                }
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(NewsDetailActivity.this);
                String uname = sharedpreferences.getString("UserName","");
                if(uname.equalsIgnoreCase("Guest User")){
                    Utils.alertDialog(NewsDetailActivity.this, "Please login to like News", "Like");
                }
                else {
                    if (isLike) {
                        Utils.alertDialog(NewsDetailActivity.this, "Like", "You have been already liked this News");

                    } else {
                        isLike = true;
                        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                R.mipmap.like);
                        likeButton.setImageBitmap(icon);

                        likC = likC + 1;
                        likeButton.setLabelText(String.valueOf(likC));

                        NewsListModel news = db.getNews(newsID);
                        news.setLikeCount(String.valueOf(likC));
                        news.setSelfLike(true);
                        db.updateNews(news);
                        LikeDislikeModel likeDislikeModel = new LikeDislikeModel();
                        likeDislikeModel.setLikeDislike("1");
                        likeDislikeModel.setNewsId(newsID);
                        likeDislikeModel.setTimeStamp(android.text.format.DateFormat.format("yyyy-mm-dd",new Date()).toString());
                        if (Utils.isConnectingToInternet(NewsDetailActivity.this)) {

                            LikeDislikeAsyncTaskPost likeDislikeAsyncTaskPost = new LikeDislikeAsyncTaskPost(likeDislikeModel, NewsDetailActivity.this, NewsDetailActivity.this);
                            likeDislikeAsyncTaskPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            Utils.alertDialog(NewsDetailActivity.this, "Network Error", "You are not connected to internet");
                        }

                    }
                }
            }
        });


        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(NewsDetailActivity.this);
                String uname = sharedpreferences.getString("UserName","");
                if(uname.equalsIgnoreCase("Guest User")){
                    Utils.alertDialog(NewsDetailActivity.this, "Please login to dislike News", "Dislike");
                }
                else {

                    if (isDislike) {

                        Utils.alertDialog(NewsDetailActivity.this, "Like", "You have been already disliked this News");

                    } else {


                        final Typeface face = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/font.ttf");

                        LayoutInflater inflater = getLayoutInflater();
                        View dialoglayout = inflater.inflate(R.layout.addcomment, null);
                        Button submit = (Button) dialoglayout.findViewById(R.id.btn_submit);
                        Button cancel = (Button) dialoglayout.findViewById(R.id.btn_cancel);
                        final EditText comment = (EditText) dialoglayout.findViewById(R.id.edtcomment);
                        submit.setTypeface(face);
                        cancel.setTypeface(face);


                        final AlertDialog.Builder builder = new AlertDialog.Builder(NewsDetailActivity.this);
                        builder.setView(dialoglayout);

                        final AlertDialog alertDialog = builder.create();

                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                    isDislike = true;
                                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                            R.mipmap.dislike);
                                    dislikeButton.setImageBitmap(icon);

                                    dislikeC = dislikeC + 1;
                                    dislikeButton.setLabelText(String.valueOf(dislikeC));

                                    NewsListModel news = db.getNews(newsID);
                                    news.setDisLikeCount(String.valueOf(dislikeC));
                                    news.setSelfDisLike(true);
                                    db.updateNews(news);
                                    LikeDislikeModel likeDislikeModel = new LikeDislikeModel();
                                    likeDislikeModel.setLikeDislike("0");
                                    likeDislikeModel.setNewsId(newsID);
                                    likeDislikeModel.setTimeStamp(new Date().toString());
                                    if (Utils.isConnectingToInternet(NewsDetailActivity.this)) {

                                        LikeDislikeAsyncTaskPost likeDislikeAsyncTaskPost = new LikeDislikeAsyncTaskPost(likeDislikeModel, NewsDetailActivity.this, NewsDetailActivity.this);
                                        likeDislikeAsyncTaskPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                        alertDialog.dismiss();
                                    } else {
                                        alertDialog.dismiss();
                                        Utils.alertDialog(NewsDetailActivity.this, "Network Error", "You are not connected to internet");
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


        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utils.isConnectingToInternet(NewsDetailActivity.this)) {
                    Intent intent = new Intent(NewsDetailActivity.this, CommentActivity.class);
                    intent.putExtra("NewsID", newsID);
                    startActivity(intent);
                }
                else {
                    Utils.alertDialog(NewsDetailActivity.this, "Network Error", "You are not connected to internet");
                }
            }
        });


        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(NewsDetailActivity.this);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, heading.getText().toString() + "\n To View this News, Please Download App from Play Store:\n"+sharedpreferences.getString("AppLink",""));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
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
                isLike = newsListModel.isSelfLike();
                isDislike = newsListModel.isSelfDisLike();

                setLikeUnlike();
                likeButton.setLabelText(newsListModel.getLikeCount());
                dislikeButton.setLabelText(String.valueOf(newsListModel.getDisLikeCount()));


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
                    R.mipmap.like);
           likeButton.setImageBitmap(icon);
        }
        else {
            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.likegrey);
            likeButton.setImageBitmap(icon);
        }

        if(isDislike){
            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.dislike);
            dislikeButton.setImageBitmap(icon);
        }
        else {
            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.dislikegrey);
            dislikeButton.setImageBitmap(icon);
        }

    }
}

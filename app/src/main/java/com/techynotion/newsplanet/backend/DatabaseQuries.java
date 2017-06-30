package com.techynotion.newsplanet.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.techynotion.newsplanet.model.NewsListModel;

import java.util.ArrayList;

/**
 * Created by Dell on 1/7/2017.
 */
public class DatabaseQuries {

    SQLiteDatabase db;
    Context context;
    String scode;

    public DatabaseQuries(Context context) {

        this.context = context;
        SQLiteOpenHelper myHelper = SqliteHelper.getInstance(context);
        this.db = myHelper.getWritableDatabase();
    }

    // Insert News
    public long insertNews(NewsListModel news) {
        if(isNewsDelete()) {
            deleteNews();
        }
        ContentValues conV = new ContentValues();
        conV.put("newsID", news.getNewsId());
        conV.put("createTS", news.getCreatedTs());
        conV.put("categoryID", news.getCategoryId());
        conV.put("newsPhotoUrl", news.getNewsPhotoUrl());
        conV.put("newsTitle", news.getNewsTitle());
        conV.put("newsById", news.getNewsById());
        conV.put("newsDescription", news.getNewsDescription());
        conV.put("likecount", news.getLikeCount());
        conV.put("dislikecount", news.getDisLikeCount());
        conV.put("selfLike", news.isSelfLike());
        conV.put("selfdisklike", news.isSelfDisLike());
        long newRowInserted = db.insert("Newslist", null, conV);

        return newRowInserted;
    }

    // Update News
    public long updateNews(NewsListModel news) {
        //deleteTable();
        ContentValues conV = new ContentValues();
        conV.put("newsID", news.getNewsId());
        conV.put("createTS", news.getCreatedTs());
        conV.put("categoryID", news.getCategoryId());
        conV.put("newsPhotoUrl", news.getNewsPhotoUrl());
        conV.put("newsTitle", news.getNewsTitle());
        conV.put("newsDescription", news.getNewsDescription());
        conV.put("newsById", news.getNewsById());
        conV.put("likecount", news.getLikeCount());
        conV.put("dislikecount", news.getDisLikeCount());
        conV.put("selfLike", news.isSelfLike());
        conV.put("selfdisklike", news.isSelfDisLike());
        long newRowInserted = db.update("Newslist", conV, "newsID='" + news.getNewsId() + "'", null);

        return newRowInserted;
    }
    // Select All News
    public String getLastNewsTime() {
        Cursor c;

            c = db.rawQuery("SELECT * FROM Newslist ORDER BY createTS Desc LIMIT 1", null);

       String result = "";

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    result = c.getString(c.getColumnIndex("createTS"));
                    cnt = cnt + 1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    // Select All News
    public ArrayList<NewsListModel> getAllNews(String category) {
        Cursor c;
        if (category.equalsIgnoreCase("0")) {
            c = db.rawQuery("SELECT * FROM Newslist ORDER BY createTS Desc", null);
        } else {
            c = db.rawQuery("SELECT * FROM Newslist WHERE categoryID='" + category + "' ORDER BY createTS Desc", null);
        }
        ArrayList<NewsListModel> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    String newsId = c.getString(c.getColumnIndex("newsID"));
                    String createTS = c.getString(c.getColumnIndex("createTS"));
                    String categoryID = c.getString(c.getColumnIndex("categoryID"));
                    String newsPhotoUrl = c.getString(c.getColumnIndex("newsPhotoUrl"));
                    String newsTitle = c.getString(c.getColumnIndex("newsTitle"));
                    String newsDesc = c.getString(c.getColumnIndex("newsDescription"));
                    String likecount = c.getString(c.getColumnIndex("likecount"));
                    String dislikecount = c.getString(c.getColumnIndex("dislikecount"));
                    boolean selflike = c.getInt(c.getColumnIndex("selfLike")) > 0;
                    boolean selfdislike = c.getInt(c.getColumnIndex("selfdisklike")) > 0;
                    String newsbyId = c.getString(c.getColumnIndex("newsById"));

                    NewsListModel news = new NewsListModel();
                    news.setNewsId(newsId);
                    news.setNewsById(newsbyId);
                    news.setCategoryId(categoryID);
                    news.setNewsTitle(newsTitle);
                    news.setNewsDescription(newsDesc);
                    news.setNewsPhotoUrl(newsPhotoUrl);
                    news.setLikeCount(likecount);
                    news.setDisLikeCount(dislikecount);
                    news.setSelfDisLike(selfdislike);
                    news.setSelfLike(selflike);
                    news.setCreatedTs(createTS);

                    result.add(news);
                    cnt = cnt + 1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    public NewsListModel getNews(String newsid) {
        Cursor c = db.rawQuery("SELECT * FROM Newslist WHERE newsID='" + newsid + "'", null);
        NewsListModel result = new NewsListModel();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    String newsId = c.getString(c.getColumnIndex("newsID"));
                    String createTS = c.getString(c.getColumnIndex("createTS"));
                    String categoryID = c.getString(c.getColumnIndex("categoryID"));
                    String newsPhotoUrl = c.getString(c.getColumnIndex("newsPhotoUrl"));
                    String newsTitle = c.getString(c.getColumnIndex("newsTitle"));
                    String newsDesc = c.getString(c.getColumnIndex("newsDescription"));
                    String likecount = c.getString(c.getColumnIndex("likecount"));
                    String dislikecount = c.getString(c.getColumnIndex("dislikecount"));
                    boolean selflike = c.getInt(c.getColumnIndex("selfLike")) > 0;
                    boolean selfdislike = c.getInt(c.getColumnIndex("selfdisklike")) > 0;
                    String newsbyId = c.getString(c.getColumnIndex("newsById"));

                    NewsListModel news = new NewsListModel();
                    news.setNewsId(newsId);
                    news.setNewsById(newsbyId);
                    news.setCategoryId(categoryID);
                    news.setNewsTitle(newsTitle);
                    news.setNewsDescription(newsDesc);
                    news.setNewsPhotoUrl(newsPhotoUrl);
                    news.setLikeCount(likecount);
                    news.setDisLikeCount(dislikecount);
                    news.setSelfDisLike(selfdislike);
                    news.setSelfLike(selflike);
                    news.setCreatedTs(createTS);

                    result = news;
                    cnt = cnt + 1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    // Insert Favourite
    public long insertFav(String ID) {
        //deleteTable();
        ContentValues conV = new ContentValues();
        conV.put("favId", ID);

        long newRowInserted = db.insert("Favourite", null, conV);

        return newRowInserted;
    }

    public long deleteFav(String ID) {
        long deleterow = db.delete("Favourite", "favId='" + ID + "'", null);
        return deleterow;
    }

    public long deleteNews() {

        Cursor c;

        c = db.rawQuery("SELECT newsID FROM Newslist ORDER BY createTS ASC LIMIT 1", null);

        String result = "";

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    result = c.getString(c.getColumnIndex("newsID"));
                    cnt = cnt + 1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();

        long deleterow = db.delete("Newslist", "newsID='" + result + "'", null);
        return deleterow;
    }

    public boolean isNewsDelete() {

        Cursor c;

        c = db.rawQuery("SELECT newsID FROM Newslist ORDER BY createTS DESC", null);

        boolean result = false;

        if (c != null) {
            if(c.getCount()> 49){
                result = true;
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();

        return result;
    }

    public String[] getFav() {
        Cursor c = db.rawQuery("SELECT * FROM Favourite", null);
        String[] result;
        result = new String[c.getCount()];

        int cnt = 0;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    String fav = c.getString(c.getColumnIndex("favId"));
                    result[cnt] = fav;
                    cnt = cnt + 1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    public boolean getFav(String favId) {
        boolean fav = false;
        Cursor c = db.rawQuery("SELECT * FROM Favourite WHERE favId='" + favId + "'", null);

        int cnt = 0;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    fav = true;
                    cnt = cnt + 1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return fav;
    }

    public void deleteAllData() {
        long deleterow = 0;

        db.delete("Newslist", null, null);
        db.delete("Favourite", null, null);


    }

}

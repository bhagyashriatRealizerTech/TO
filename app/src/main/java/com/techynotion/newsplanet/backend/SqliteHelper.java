package com.techynotion.newsplanet.backend;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.techynotion.newsplanet.ExceptionHandler;


/**
 * Created by Win on 12/21/2015.
 */
public class SqliteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "NewsApp";
    private static final int DATABASE_VERSION =1;
    static Context mycontext;
    private static SqliteHelper mInstance = null;
    private static final String NewsList ="CREATE TABLE Newslist(newsID TEXT,createTS TEXT, categoryID TEXT,newsPhotoUrl TEXT," +
            "newsTitle TEXT, newsById TEXT,likecount TEXT, dislikecount TEXT, selfLike, boolean, selfdisklike boolean,newsDescription TEXT)";
    private static final String Favourite = "CREATE TABLE Favourite(favId TEXT)";
    private SqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(context));
        this.mycontext = context;
    }

    public static SqliteHelper getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new SqliteHelper(ctx.getApplicationContext());
        }
        mycontext = ctx;
        return mInstance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(NewsList);
        db.execSQL(Favourite);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

        db.execSQL("DROP TABLE if exists " + "NewsList");
        db.execSQL("DROP TABLE if exists " + "Favourite");
        onCreate(db);
    }
}

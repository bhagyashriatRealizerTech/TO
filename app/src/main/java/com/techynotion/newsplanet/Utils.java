package com.techynotion.newsplanet;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateFormat;

import com.techynotion.newsplanet.model.NewsListModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Dell on 12/17/2016.
 */
public class Utils {


    public static final String URL = "http://216.117.135.53/newsapp/api/Account/";
    public static final String NewsURL = "http://216.117.135.53/newsapp/api/News/";

/*    public static final String URL = "http://newsapp.mahaearth.com/api/Account/";
    public static final String NewsURL = "http://newsapp.mahaearth.com/api/News/";*/


    /**
     * @param context
     * @param title
     * @param message
     */
    public static void alertDialog(final Context context, String title, String message) {
        AlertDialog.Builder adbdialog;
        adbdialog = new AlertDialog.Builder(context);
        adbdialog.setTitle(title);
        adbdialog.setMessage(message);
        adbdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adbdialog.show();

    }

    public static void updateAlertDialog(final Context context, String title, String message) {
        AlertDialog.Builder adbdialog;
        adbdialog = new AlertDialog.Builder(context);
        adbdialog.setTitle(title);
        adbdialog.setMessage(message);
        adbdialog.setCancelable(false);
        adbdialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                context.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName()+"&hl=en")));

                dialog.dismiss();
            }
        });
        adbdialog.show();

    }

    public static boolean isConnectingToInternet(Context context){

        ConnectivityManager connectivity =
                (ConnectivityManager) context.getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null)
            {
                    if (info.getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
            }

        }
        return false;
    }



    /**
     * @param title to set
     * @return title SpannableString
     */
    public static SpannableString actionBarTitle(String title,Context context) {
        SpannableString s = new SpannableString(title);
        Typeface face= Typeface.createFromAsset(context.getAssets(), "fonts/font.ttf");
        s.setSpan(new CustomTypefaceSpan("", face), 0, s.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return s;
    }


    public static String getCategory(String cat){
        String catId = "0";
       if(cat.equalsIgnoreCase("AllNews"))
           catId = "0";
        else if(cat.equalsIgnoreCase("Global"))
            catId = "1";
       else if(cat.equalsIgnoreCase("Politics"))
           catId ="2";
       else if(cat.equalsIgnoreCase("Entertainment"))
           catId = "3";
       else if(cat.equalsIgnoreCase("Sports"))
           catId = "4";
       else if(cat.equalsIgnoreCase("LifeStyle"))
           catId = "5";
       else if(cat.equalsIgnoreCase("Gadgets"))
           catId = "6";

        return catId;
    }

    public static String getCommentDate(String inputDate){
        String outdate = "";
        String temp = inputDate.split("T")[0];
        SimpleDateFormat informat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outformat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date ipDate = informat.parse(temp);
            outdate = outformat.format(ipDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return outdate;
    }

}



package com.techynotion.newsplanet;


import android.content.Context;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;

import com.techynotion.newsplanet.model.NewsListModel;

import java.util.ArrayList;


/**
 * Created by Bhagyashri on 4/2/2016.
 */
public class Singlton {

    private static Singlton _instance;
    public static ResultReceiver resultReceiver;
    public static Context context;
    public static NewsListFragment fragment;
    public static ProgressWheel newsList = null;
    public static ArrayList<NewsListModel> newsmodelList = new ArrayList<>();
    public static ArrayList<String> fav = new ArrayList<>();

    private Singlton() {


    }

    public static Singlton getInstance() {
        if (_instance == null) {
            _instance = new Singlton();
        }
        return _instance;
    }

    public static ProgressWheel getNewsList() {
        return newsList;
    }

    public static void setNewsList(ProgressWheel newsList) {
        Singlton.newsList = newsList;
    }

    public static ResultReceiver getResultReceiver() {
        return resultReceiver;
    }

    public static void setResultReceiver(ResultReceiver resultReceiver) {
        Singlton.resultReceiver = resultReceiver;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        Singlton.context = context;
    }

    public static Fragment getFragment() {
        return fragment;
    }

    public static void setFragment(NewsListFragment fragment) {
        Singlton.fragment = fragment;
    }

    public static ArrayList<NewsListModel> getNewsmodelList() {
        return newsmodelList;
    }

    public static void setNewsmodelList(ArrayList<NewsListModel> newsmodelList) {
        Singlton.newsmodelList = newsmodelList;
    }

    public static ArrayList<String> getFav() {
        return fav;
    }

    public static void setFav(String favCat) {
        Singlton.fav.add(favCat);
    }
}


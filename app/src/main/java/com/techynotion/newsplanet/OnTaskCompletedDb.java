package com.techynotion.newsplanet;

import com.techynotion.newsplanet.model.NewsListModel;
import com.techynotion.newsplanet.model.TaskComplete;

import java.util.List;

/**
 * Created by shree on 11/21/2015.
 */
public interface OnTaskCompletedDb {

    void onTaskCompleted(String s, List<NewsListModel> listmodel);
}

package com.techynotion.newsplanet;

import com.techynotion.newsplanet.model.TaskComplete;

/**
 * Created by shree on 11/21/2015.
 */
public interface OnTaskCompleted {

    void onTaskCompleted(String s, TaskComplete object);
}

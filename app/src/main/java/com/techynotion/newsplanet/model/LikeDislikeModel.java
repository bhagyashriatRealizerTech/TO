package com.techynotion.newsplanet.model;

/**
 * Created by Dell on 1/8/2017.
 */
public class LikeDislikeModel {
    private String userRegistrationId;
    private String newsId;
    private String TimeStamp;
    private String LikeDislike;

    public String getUserRegistrationId() {
        return userRegistrationId;
    }

    public void setUserRegistrationId(String userRegistrationId) {
        this.userRegistrationId = userRegistrationId;
    }

    public String getLikeDislike() {
        return LikeDislike;
    }

    public void setLikeDislike(String likeDislike) {
        LikeDislike = likeDislike;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }
}

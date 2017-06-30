package com.techynotion.newsplanet.model;

import android.graphics.Bitmap;

/**
 * Created by Dell on 11/19/2016.
 */
public class NewsListModel {

    public String searchText;
    public String fromdate;
    public String NewsId;
    public String CategoryId;
    public String NewsPhotoUrl;
    public String NewsTitle;
    public String NewsDescription;
    public String CreatedTs;
    public String NewsById;
    public String Comments;
    public String Likes;
    public String LikeCount;
    public String DisLikeCount;
    public boolean selfLike;
    public boolean selfDisLike;





    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getFromdate() {
        return fromdate;
    }

    public void setFromdate(String fromdate) {
        this.fromdate = fromdate;
    }

    public String getNewsId() {
        return NewsId;
    }

    public void setNewsId(String newsId) {
        NewsId = newsId;
    }

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }

    public String getNewsPhotoUrl() {
        return NewsPhotoUrl;
    }

    public void setNewsPhotoUrl(String newsPhotoUrl) {
        NewsPhotoUrl = newsPhotoUrl;
    }

    public String getNewsTitle() {
        return NewsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        NewsTitle = newsTitle;
    }

    public String getNewsDescription() {
        return NewsDescription;
    }

    public void setNewsDescription(String newsDescription) {
        NewsDescription = newsDescription;
    }

    public String getCreatedTs() {
        return CreatedTs;
    }

    public void setCreatedTs(String createdTs) {
        CreatedTs = createdTs;
    }

    public String getNewsById() {
        return NewsById;
    }

    public void setNewsById(String newsById) {
        NewsById = newsById;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public String getLikes() {
        return Likes;
    }

    public void setLikes(String likes) {
        Likes = likes;
    }

    public String getLikeCount() {
        return LikeCount;
    }

    public void setLikeCount(String likeCount) {
        LikeCount = likeCount;
    }

    public String getDisLikeCount() {
        return DisLikeCount;
    }

    public void setDisLikeCount(String disLikeCount) {
        DisLikeCount = disLikeCount;
    }

    public boolean isSelfLike() {
        return selfLike;
    }

    public void setSelfLike(boolean selfLike) {
        this.selfLike = selfLike;
    }

    public boolean isSelfDisLike() {
        return selfDisLike;
    }

    public void setSelfDisLike(boolean selfDisLike) {
        this.selfDisLike = selfDisLike;
    }
}

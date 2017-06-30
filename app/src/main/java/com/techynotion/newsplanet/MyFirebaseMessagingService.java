package com.techynotion.newsplanet;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.techynotion.newsplanet.backend.DatabaseQuries;
import com.techynotion.newsplanet.model.NewsListModel;

/**
 * Created by Dell on 1/7/2017.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    DatabaseQuries db;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        db = new DatabaseQuries(MyFirebaseMessagingService.this);
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification());

        NewsListModel newsListModel = new NewsListModel();

        newsListModel.setCategoryId(remoteMessage.getData().get("CategoryId"));
        newsListModel.setNewsId(remoteMessage.getData().get("NewsId"));
        newsListModel.setNewsPhotoUrl(remoteMessage.getData().get("NewsPhotoUrl"));
        newsListModel.setNewsDescription(remoteMessage.getData().get("NewsDescription"));
        newsListModel.setCreatedTs(remoteMessage.getData().get("CreatedTs"));
        newsListModel.setNewsTitle(remoteMessage.getData().get("NewsTitle"));
        newsListModel.setDisLikeCount("0");
        newsListModel.setLikeCount("0");
        newsListModel.setSelfDisLike(false);
        newsListModel.setSelfDisLike(false);
        db.insertNews(newsListModel);
        //Calling method to generate notification
        //if(db.getFav(newsListModel.getCategoryId())){
            sendNotification(remoteMessage.getNotification().getBody());
       // }

    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("MahaEarth")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}

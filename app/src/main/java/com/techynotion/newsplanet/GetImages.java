package com.techynotion.newsplanet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Bhagyashri on 9/12/2016.
 */
public class GetImages extends AsyncTask<Object, Object, Object> {
    private String requestUrl, imagename_;
    private ImageView view;
    private TextView tview;
    private String username;
    private Bitmap bitmap ;
    private FileOutputStream fos;
    AVLoadingIndicatorView load;
    public GetImages(String requestUrl, ImageView view,AVLoadingIndicatorView load,TextView tview,String username ,String _imagename_) {
        this.requestUrl = requestUrl;
        this.view = view;
        this.imagename_ = _imagename_ ;
        this.tview = tview;
        this.username = username;
        this.load = load;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();


    }

    @Override
    protected Object doInBackground(Object... objects) {


        try {
            URL url = new URL(requestUrl);
            URLConnection conn = url.openConnection();
            bitmap = BitmapFactory.decodeStream(conn.getInputStream());
        } catch (Exception ex) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {

        if(load != null){
            load.setVisibility(View.GONE);
        }

        if(bitmap != null) {
            if (!ImageStorage.checkifImageExists(imagename_)) {
                view.setVisibility(View.VISIBLE);
                view.setImageBitmap(bitmap);
                ImageStorage.saveToSdCard(bitmap, imagename_);
            }
        }
        else if(tview != null && username != null)
        {
           /* String name[] = username.trim().split(" ");
            char fchar  = name[0].toUpperCase().charAt(0);
            char lchar  = name[0].toUpperCase().charAt(0);
            for(int i =0;i<name.length;i++)
            {
                if(!name[i].equals("") && i==0)
                    fchar = name[i].toUpperCase().charAt(0);
                else if(!name.equals("") && i==(name.length-1))
                    lchar = name[i].toUpperCase().charAt(0);

            }*/
            tview.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);
            tview.setText(username);
        }
        else
        {
           // view.setImageResource(R.drawable.chat_icon);
        }
    }
}


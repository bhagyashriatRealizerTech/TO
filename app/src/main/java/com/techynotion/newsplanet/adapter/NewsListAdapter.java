package com.techynotion.newsplanet.adapter;

/**
 * Created by Dell on 11/19/2016.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.techynotion.newsplanet.GetImages;
import com.techynotion.newsplanet.ImageStorage;
import com.techynotion.newsplanet.MyAnimation;
import com.techynotion.newsplanet.R;
import com.techynotion.newsplanet.model.NewsListModel;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.util.List;

public class NewsListAdapter extends BaseAdapter  {


    private LayoutInflater newsinflator;
    View convertview;
    public Context context;
    public List<NewsListModel>newsListModels;

    public NewsListAdapter(Context context,List<NewsListModel> newslist)
    {
        this.context = context;
        newsListModels = newslist;
        newsinflator = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return newsListModels.size();
    }

    @Override
    public Object getItem(int position) {
        return newsListModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return newsListModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;


        convertview = convertView;

        if (convertView == null) {
            convertView = newsinflator.inflate(R.layout.news_list_layout, null);

            holder = new ViewHolder();
            holder.headline = (TextView) convertView.findViewById(R.id.txt_headline);
            holder.newsPic = (ImageView) convertView.findViewById(R.id.iv_newspic);
            holder.newsPic.setVisibility(View.VISIBLE);
            holder.loadingview = (AVLoadingIndicatorView) convertView.findViewById(R.id.avi);
            holder.loadingview.setVisibility(View.VISIBLE);



            Typeface face1= Typeface.createFromAsset(context.getAssets(), "fonts/font.ttf");
            //holder.headline.setTypeface(face1);

            String urlString = newsListModels.get(position).getNewsPhotoUrl();
            if(urlString == null){
                urlString = "";
            }
            StringBuilder sb=new StringBuilder();
            for(int i=0;i<urlString.length();i++)
            {
                char c='\\';
                if (urlString.charAt(i) =='\\')
                {
                    urlString.replace("\"","");
                    sb.append("/");
                }
                else
                {
                    sb.append(urlString.charAt(i));
                }
            }

            String newURL=sb.toString();


            if(newURL.trim().length() > 0) {
                if (!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                    new GetImages(newURL, holder.newsPic, holder.loadingview,null, null, newURL.split("/")[newURL.split("/").length - 1]).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,newURL);
                else {
                    File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length - 1]);
                    if (image != null) {
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                        holder.loadingview.setVisibility(View.GONE);
                        holder.newsPic.setVisibility(View.VISIBLE);
                        holder.newsPic.setImageBitmap(bitmap);
                    }
                }
            }else {
                holder.loadingview.setVisibility(View.GONE);
            }

            convertView.setTag(holder);
        }

        else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.headline.setText(newsListModels.get(position).getNewsTitle());
        //holder.newsPic.setImageBitmap(newsListModels.get(position).getPic());
      /*  Picasso.with(context)
                .load(newsListModels.get(position).getNewspicUrl())
                .into(holder.newsPic);*/

        return convertView;
    }

    static class ViewHolder
    {
        TextView headline;
        ImageView newsPic;
        AVLoadingIndicatorView loadingview;
    }
}

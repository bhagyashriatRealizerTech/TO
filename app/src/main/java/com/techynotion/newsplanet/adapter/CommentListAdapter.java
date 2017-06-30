package com.techynotion.newsplanet.adapter;

/**
 * Created by Dell on 11/19/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.techynotion.newsplanet.GetImages;
import com.techynotion.newsplanet.ImageStorage;
import com.techynotion.newsplanet.R;
import com.techynotion.newsplanet.Utils;
import com.techynotion.newsplanet.model.CommentModel;
import com.techynotion.newsplanet.model.NewsListModel;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.util.List;

public class CommentListAdapter extends BaseAdapter  {


    private LayoutInflater newsinflator;
    View convertview;
    public Context context;
    public List<CommentModel>commentListModels;

    public CommentListAdapter(Context context, List<CommentModel> commentlist)
    {
        this.context = context;
        commentListModels = commentlist;
        newsinflator = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return commentListModels.size();
    }

    @Override
    public Object getItem(int position) {
        return commentListModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return commentListModels.size();
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
            convertView = newsinflator.inflate(R.layout.comment_list_layout, null);

            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.userpic = (ImageView) convertView.findViewById(R.id.img_user_image);
            holder.comnt = (TextView) convertView.findViewById(R.id.comment);
            holder.userin = (TextView) convertView.findViewById(R.id.txtinitial);
            holder.comntdate = (TextView) convertView.findViewById(R.id.txtdate);



            Typeface face1= Typeface.createFromAsset(context.getAssets(), "fonts/font.ttf");

            holder.comnt.setText(commentListModels.get(position).getComment());
            holder.name.setText(commentListModels.get(position).getUsername());
            holder.comntdate.setText(Utils.getCommentDate(commentListModels.get(position).getTimeStamp()));
            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String initials = sharedpreferences.getString("UserInitials", "");


            String urlString = commentListModels.get(position).getProfile();
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
                    new GetImages(newURL, holder.userpic, null,null, null, newURL.split("/")[newURL.split("/").length - 1]).execute(AsyncTask.THREAD_POOL_EXECUTOR, newURL);
                else {
                    File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length - 1]);
                    if (image != null) {
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                        holder.userpic.setVisibility(View.VISIBLE);
                        holder.userin.setVisibility(View.GONE);
                        holder.userpic.setImageBitmap(bitmap);
                    }
                }
            }else {
                holder.userpic.setVisibility(View.GONE);
                holder.userin.setVisibility(View.VISIBLE);
                holder.userin.setText(initials);
            }

            convertView.setTag(holder);
        }

        else {
            holder = (ViewHolder) convertView.getTag();
        }



        return convertView;
    }

    static class ViewHolder
    {
        TextView name,comnt,comntdate,userin;
        ImageView userpic;
    }
}

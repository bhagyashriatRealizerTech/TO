package com.techynotion.newsplanet;



import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.techynotion.newsplanet.adapter.NewsListAdapter;
import com.techynotion.newsplanet.asyncTask.NewsListAsyncTaskPost;
import com.techynotion.newsplanet.backend.DatabaseQuries;
import com.techynotion.newsplanet.model.NewsListModel;
import com.techynotion.newsplanet.model.TaskComplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 11/19/2016.
 */
public class FavFragment extends Fragment implements OnTaskCompleted {

    List<NewsListModel> listmodel;
    ListView lisview;
    ProgressWheel loading;
    DatabaseQuries db;
    String title;
    MenuItem fav;
    private boolean isFav;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));

        final View rootView = inflater.inflate(R.layout.favnews_layout, container, false);

       // setHasOptionsMenu(true);

      /*  lisview  = (ListView)rootView.findViewById(R.id.newslist);
        loading = (ProgressWheel)rootView.findViewById(R.id.loading);
        Singlton.setNewsList(loading);
        listmodel = new ArrayList<>();
        db = new DatabaseQuries(getActivity());

        title = getArguments().getString("Category");

        if(title.equalsIgnoreCase("Favorites")){

            String catArr[] = new String[Singlton.getFav().size()];
            for(int i=0;i<Singlton.getFav().size();i++){
                catArr[i] = Singlton.getFav().get(i);
                listmodel = Utils.getNewsByCategory(catArr);
            }
        }
        else {
            String catArr[] = new String[1];
            catArr[0] = Utils.getCategory(title);
            listmodel = Utils.getNewsByCategory(catArr);
        }
        if(listmodel.size() > 0){
            NewsListAdapter adapter = new NewsListAdapter(getActivity(),listmodel);
            lisview.setAdapter(adapter);

        }


        lisview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), ParallaxToolbarScrollViewActivity.class);
                intent.putExtra("Title", listmodel.get(position).getNewsTitle());
                intent.putExtra("Desc", listmodel.get(position).getNewsDescription());
                intent.putExtra("CategoryId", listmodel.get(position).getCategoryId());
                intent.putExtra("NewsId", listmodel.get(position).getNewsId());
                intent.putExtra("NewsbyId", listmodel.get(position).getNewsById());
                intent.putExtra("Like", listmodel.get(position).getLikes());
                intent.putExtra("Comments", listmodel.get(position).getComments());
                intent.putExtra("NewsPic", listmodel.get(position).getNewsPhotoUrl());
                intent.putExtra("Islike", listmodel.get(position).isSelfLike());
                intent.putExtra("IsDislike", listmodel.get(position).isSelfDisLike());
                intent.putExtra("LikeCount", listmodel.get(position).getLikeCount());
                intent.putExtra("DislikeCount", listmodel.get(position).getDisLikeCount());

                startActivity(intent);
            }
        });*/


        return rootView;

    }

    @Override
    public void onTaskCompleted(String s, TaskComplete object) {

    }


}

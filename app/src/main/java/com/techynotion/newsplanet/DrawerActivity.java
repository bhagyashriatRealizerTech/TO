package com.techynotion.newsplanet;



import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.techynotion.newsplanet.backend.DatabaseQuries;

import java.io.File;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    TextView userName,userInitials;
    ImageView userImage;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    private static MenuItem fav;
    private TextView favourite;
    private boolean isFav;
    String title;
    DatabaseQuries db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(DrawerActivity.this));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setCustomView(R.layout.action_bar_view);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);

        userName = (TextView) header.findViewById(R.id.txt_user_name);
        userImage = (ImageView) header.findViewById(R.id.img_user_image);
        userInitials = (TextView) header.findViewById(R.id.tv_userinitial);

        navigationView.setNavigationItemSelectedListener(this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(preferences.getString("UserName", "").equalsIgnoreCase("Guest User")){
            // get menu from navigationView
            Menu menu = navigationView.getMenu();

            // find MenuItem you want to change
            MenuItem nav_logout = menu.findItem(R.id.nav_logout);

            // set new title to the MenuItem
            nav_logout.setTitle("Login");
        }


        userName.setText(preferences.getString("UserName", ""));
        String urlString = preferences.getString("ProfilePicUrl", "");
        String initials = preferences.getString("UserInitials", "");

        Log.d("Image URL", urlString);

        if (urlString.equals("") || urlString.equalsIgnoreCase("null") || urlString.equalsIgnoreCase(null)) {

            userImage.setVisibility(View.GONE);
            userInitials.setVisibility(View.VISIBLE);
            userInitials.setText(initials);
        }
        else {

            userImage.setVisibility(View.VISIBLE);
            userInitials.setVisibility(View.GONE);
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
         /*   Picasso.with(DrawerActivity.this)
                    .load(newURL)
                    .into(userImage);*/

           if(!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length-1]))
                new GetImages(newURL,userImage,null,userInitials,initials,newURL.split("/")[newURL.split("/").length-1]).execute(AsyncTask.THREAD_POOL_EXECUTOR,newURL);
            else
            {
                File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length-1]);
                if(image != null) {
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                    userImage.setImageBitmap(bitmap);
                }
            }

        }

         /* Display First Fragment at Launch*/
        navigationView.setCheckedItem(R.id.nav_allnews);
        Fragment frag = new MainFragmentViewPager();
        Bundle bundle = new Bundle();
        bundle.putString("Category","AllNews");
        frag.setArguments(bundle);
        if (frag != null)
        {
            mFragmentManager = getSupportFragmentManager();
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.framelayout, frag).commit();

        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       /* getMenuInflater().inflate(R.menu.menu_fav, menu);//Menu Resource, Menu
        fav = menu.findItem(R.id.action_fav);
        Typeface face= Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fav.setIcon(0);
        favourite = new TextView(DrawerActivity.this);
        favourite.setLayoutParams(layoutParams);
        favourite.setTextColor(getResources().getColor(R.color.white));
        favourite.setText(getResources().getString(R.string.favorite));
        favourite.setPadding(30, 0, 30, 0);
        favourite.setTextSize(20);
        favourite.setTypeface(face);
        MenuItemCompat.setActionView(fav, favourite);

        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isFav){
                    favourite.setTextColor(getResources().getColor(R.color.white));
                    isFav = false;
                    db.insertFav(Utils.getCategory(title).toString());
                }
                else {
                    favourite.setTextColor(getResources().getColor(R.color.red));
                    isFav = true;
                    db.deleteFav(Utils.getCategory(title).toString());
                }
            }
        });*/
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
      /*  int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if(favourite != null)
            favourite.setVisibility(View.VISIBLE);

        if (id == R.id.nav_allnews) {

            fragment = new MainFragmentViewPager();
            Bundle bundle = new Bundle();
            bundle.putString("Category", "AllNews");
            fragment.setArguments(bundle);
            if(favourite != null)
                favourite.setVisibility(View.GONE);
            // Handle the camera action
        } else if (id == R.id.nav_global) {
            title = "Global";
            fragment = new MainFragmentViewPager();
            Bundle bundle = new Bundle();
            bundle.putString("Category", "Global");
            fragment.setArguments(bundle);
        } else if (id == R.id.nav_politics) {
            title = "Politics";
            fragment = new MainFragmentViewPager();
            Bundle bundle = new Bundle();
            bundle.putString("Category","Politics");
            fragment.setArguments(bundle);

        } else if (id == R.id.nav_entertainment) {
            title = "Entertainment";
            fragment = new MainFragmentViewPager();
            Bundle bundle = new Bundle();
            bundle.putString("Category","Entertainment");
            fragment.setArguments(bundle);

        } else if (id == R.id.nav_sports) {
            title = "Sports";
            fragment = new MainFragmentViewPager();
            Bundle bundle = new Bundle();
            bundle.putString("Category","Sports");
            fragment.setArguments(bundle);

        } else if (id == R.id.nav_lifestyle) {
            title = "LifeStyle";
            fragment = new MainFragmentViewPager();
            Bundle bundle = new Bundle();
            bundle.putString("Category","Lifestyle");
            fragment.setArguments(bundle);
        }
        else if (id == R.id.nav_gadgets) {
            title = "Gadgets";
            fragment = new MainFragmentViewPager();
            Bundle bundle = new Bundle();
            bundle.putString("Category","Gadgets");
            fragment.setArguments(bundle);
        }else if (id == R.id.nav_motivation) {
            title = "Motivation";
            fragment = new MainFragmentViewPager();
            Bundle bundle = new Bundle();
            bundle.putString("Category","Motivation");
            fragment.setArguments(bundle);
        }
        else if (id == R.id.nav_share) {
            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(DrawerActivity.this);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Download TalkOut from PlayStore to get Updated News.\n"+sharedpreferences.getString("AppLink",""));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

        } else if (id == R.id.nav_favorites) {
            title = "Favorites";
            if(favourite != null)
                favourite.setVisibility(View.GONE);
            fragment = new MainFragmentViewPager();
            Bundle bundle = new Bundle();
            bundle.putString("Category","Favorites");
            fragment.setArguments(bundle);
        } else if (id == R.id.nav_logout) {
            logOut();
        }


        if (fragment != null) {
           // FragmentManager fragmentManager = getSupportFragmentManager();
            mFragmentManager.beginTransaction()
                    .replace(R.id.framelayout, fragment).commit();

        }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logOut()
    {
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sharedpreferences.edit();
        edit.putString("Login", "false");
        edit.commit();

        Intent intent = new Intent(DrawerActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if(Singlton.getNewsList() != null && Singlton.getNewsList().isShown())
        {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // condition to lock the screen at the time of refreshing
        if(Singlton.getNewsList() != null && Singlton.getNewsList().isShown()){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

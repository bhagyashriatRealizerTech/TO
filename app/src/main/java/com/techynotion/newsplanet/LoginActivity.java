package com.techynotion.newsplanet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.techynotion.newsplanet.asyncTask.LoginAsyncTaskGet;
import com.techynotion.newsplanet.asyncTask.NewsListAsyncTaskPost;
import com.techynotion.newsplanet.asyncTask.RegistrationAsyncTaskPost;
import com.techynotion.newsplanet.backend.DatabaseQuries;
import com.techynotion.newsplanet.model.NewsListModel;
import com.techynotion.newsplanet.model.RegistrationModel;
import com.techynotion.newsplanet.model.TaskComplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Callable;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by Dell on 12/17/2016.
 */
public class LoginActivity extends AppCompatActivity implements OnTaskCompleted {

    Button btn_login;
    TextView tv_forgotPwd,tv_skip,tv_title,tv_signup;
    EditText edt_username,edt_password;
    ProgressWheel loading;
    private GradientBackgroundPainter gradientBackgroundPainter;
    DatabaseQuries db;
    CallbackManager mFacebookCallbackManager;
    LoginButton mFacebookSignInButton;

    SignInButton mGoogleSignInButton;
    private static final int RC_SIGN_IN = 9001;
    GoogleApiClient mGoogleApiClient;
    String fromWhere ="";
    String bday,email,name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(LoginActivity.this));
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        mFacebookCallbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.login);


        mFacebookSignInButton = (LoginButton)findViewById(R.id.facebook_sign_in_button);
        mFacebookSignInButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
        mFacebookSignInButton.registerCallback(mFacebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v("LoginActivity", response.toString());

                                        // Application code
                                        try {
                                             email = object.getString("email");
                                             name = object.getString("name");
                                            bday ="05/09/1990";
                                            fromWhere = "fb";
                                            LoginManager.getInstance().logOut();
                                            login(email,"newsapp");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        handleSignInResult(null);
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(LoginActivity.class.getCanonicalName(), error.getMessage());
                        handleSignInResult(null);
                    }
                }
        );

        mGoogleSignInButton = (SignInButton)findViewById(R.id.google_sign_in_button);
        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        initiateView();

        db = new DatabaseQuries(LoginActivity.this);

        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }

    }
    private void signInWithGoogle() {
        if(mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void handleSignInResult(Callable<Void> callable) {}

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {android.Manifest.permission.GET_ACCOUNTS,
                        android.Manifest.permission.RECEIVE_SMS,
                        android.Manifest.permission.READ_SMS,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.WAKE_LOCK,
                        android.Manifest.permission.RECEIVE_BOOT_COMPLETED,
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.ACCESS_NETWORK_STATE,
                        android.Manifest.permission.READ_PHONE_STATE,
                        android.Manifest.permission.VIBRATE,
                }, 101);

    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void login(String username,String password){
        RegistrationModel registrationModel = new RegistrationModel();
        registrationModel.setUserId(username.trim());
        registrationModel.setPassword(password.trim());

        if(Utils.isConnectingToInternet(LoginActivity.this)) {
            loading.setVisibility(View.VISIBLE);
            btn_login.setBackgroundColor(getResources().getColor(R.color.white));
            LoginAsyncTaskGet loginAsyncTaskGet = new LoginAsyncTaskGet(registrationModel, LoginActivity.this, LoginActivity.this);
            loginAsyncTaskGet.execute();
        }
        else {
            Utils.alertDialog(LoginActivity.this,"Network Error","You are not connected to internet");
        }
    }

    public void signUp(){

        String fname,lname;
        if(name.split(" ").length >1){
            fname = name.split(" ")[0];
            lname = name.split(" ")[1];
        }
        else {
            fname = name;
            lname = "";
        }
        RegistrationModel registrationModel = new RegistrationModel();
        registrationModel.setUserId(email);
        registrationModel.setPassword("newsapp");
        registrationModel.setFirstName(fname);
        registrationModel.setLastName(lname);
        registrationModel.setMobileNo("");
        registrationModel.setEmailId(email);
        registrationModel.setUserType("9");
        registrationModel.setThumbnail("");
        registrationModel.setGender("news_detail_layout");

        if(Utils.isConnectingToInternet(LoginActivity.this)) {
            loading.setVisibility(View.VISIBLE);
            RegistrationAsyncTaskPost registrationAsyncTaskPost = new RegistrationAsyncTaskPost(registrationModel, LoginActivity.this, LoginActivity.this);
            registrationAsyncTaskPost.execute();
        }
        else {
            Utils.alertDialog(LoginActivity.this,"Network Error","You are not connected to internet");
        }
    }


    public void initiateView()
    {
        btn_login = (Button)findViewById(R.id.login);
        tv_forgotPwd = (TextView)findViewById(R.id.txtforgotpwd);
        tv_title = (TextView)findViewById(R.id.txtTitle);
        tv_skip = (TextView)findViewById(R.id.txt_skiplogin);
        edt_username = (EditText)findViewById(R.id.username);
        edt_password = (EditText)findViewById(R.id.password);
        loading = (ProgressWheel)findViewById(R.id.loading);
        tv_signup= (TextView) findViewById(R.id.txtsignup);

        Typeface face= Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/motionpic.ttf");

        tv_skip.setTextColor(getResources().getColor(R.color.white));
        tv_signup.setTextColor(getResources().getColor(R.color.white));
        tv_forgotPwd.setTextColor(getResources().getColor(R.color.white));

        tv_title.setTypeface(face);

        View backgroundImage = findViewById(R.id.root_view);

        final int[] drawables = new int[3];
        drawables[0] = R.drawable.gradient1;
        drawables[1] = R.drawable.gradient2;
        drawables[2] = R.drawable.gradient3;

        gradientBackgroundPainter = new GradientBackgroundPainter(backgroundImage, drawables);
        gradientBackgroundPainter.start();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromWhere = "loginclick";
                if (TextUtils.isEmpty(edt_username.getText().toString().trim())) {
                    Utils.alertDialog(LoginActivity.this, "Login", "Please Enter User Name");
                } else if (TextUtils.isEmpty(edt_password.getText().toString().trim())) {
                    Utils.alertDialog(LoginActivity.this, "Login", "Please Enter Password");
                } else {

                    loading.setVisibility(View.VISIBLE);
                    RegistrationModel registrationModel = new RegistrationModel();
                    registrationModel.setUserId(edt_username.getText().toString().trim());
                    registrationModel.setPassword(edt_password.getText().toString().trim());

                    if(Utils.isConnectingToInternet(LoginActivity.this)) {
                        loading.setVisibility(View.VISIBLE);
                        btn_login.setBackgroundColor(getResources().getColor(R.color.white));
                        LoginAsyncTaskGet loginAsyncTaskGet = new LoginAsyncTaskGet(registrationModel, LoginActivity.this, LoginActivity.this);
                        loginAsyncTaskGet.execute();
                    }
                    else {
                            Utils.alertDialog(LoginActivity.this,"Network Error","You are not connected to internet");
                    }

                }
            }
        });



        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor edit = sharedpreferences.edit();
                edit.putString("UserName","Guest User");
                edit.putString("UserInitials","GU");
                edit.putString("ProfilePicUrl","null");
                edit.putString("RegId",UUID.fromString("00000000-0000-0000-0000-000000000000").toString());
                edit.commit();

                NewsListModel newsListModel = new NewsListModel();

                newsListModel.setSearchText("");
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                // newsListModel.setFromdate(dateFormat.format(date));
                newsListModel.setFromdate("2016-12-25");

                if (Utils.isConnectingToInternet(LoginActivity.this)) {
                    loading.setVisibility(View.VISIBLE);
                    NewsListAsyncTaskPost newsListAsyncTaskPost = new NewsListAsyncTaskPost(newsListModel, LoginActivity.this, LoginActivity.this);
                    newsListAsyncTaskPost.execute();
                } else {
                    loading.setVisibility(View.GONE);
                    btn_login.setBackgroundResource(R.drawable.buttonselector);
                    Utils.alertDialog(LoginActivity.this, "Network Error", "You are not connected to internet");

                }

            }
        });

        tv_forgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override protected void onDestroy() {
        super.onDestroy();
        gradientBackgroundPainter.stop();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if(loading != null  && loading.isShown())
        {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // condition to lock the screen at the time of refreshing
        if ((loading != null && loading.isShown())) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onTaskCompleted(String s, TaskComplete object) {

        if(object != null){
        if (object.getTitle().equalsIgnoreCase("Login")) {

            loading.setVisibility(View.GONE);

            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
            SharedPreferences.Editor edit = sharedpreferences.edit();

            String json = s;
            if (json != null) {
                if (!TextUtils.isEmpty(json)) {
                    try {
                        JSONObject rootObj = new JSONObject(json);
                        if (rootObj.getBoolean("Success") == true) {

                            RegistrationModel registrationModel = new RegistrationModel();
                            String username = rootObj.getString("FirstName") + " " + rootObj.getString("LastName");
                            edit.putString("UserName", username);
                            edit.putString("ProfilePicUrl", rootObj.getString("Thumbnail"));
                            edit.putString("UserInitials", rootObj.getString("Initials"));
                            edit.putString("Login", "true");
                            edit.putString("RegId", rootObj.getString("UserRegistrationId"));
                            edit.putString("FetchNewsList", "false");
                            edit.commit();

                            registrationModel.setFirstName(rootObj.getString("FirstName"));
                            registrationModel.setLastName(rootObj.getString("LastName"));
                            registrationModel.setUserUUId(rootObj.getString("UserRegistrationId"));
                            registrationModel.setThumbnail(rootObj.getString("Thumbnail"));
                            registrationModel.setUserInitials(rootObj.getString("Initials"));
                            registrationModel.setMobileNo(rootObj.getString("MobileNo"));

                            NewsListModel newsListModel = new NewsListModel();

                            newsListModel.setSearchText("");
                            Date date = new Date();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

                            // newsListModel.setFromdate(dateFormat.format(date));
                            newsListModel.setFromdate("2016-12-25");

                            if (Utils.isConnectingToInternet(LoginActivity.this)) {
                                loading.setVisibility(View.VISIBLE);
                                NewsListAsyncTaskPost newsListAsyncTaskPost = new NewsListAsyncTaskPost(newsListModel, LoginActivity.this, LoginActivity.this);
                                newsListAsyncTaskPost.execute();
                            } else {
                                loading.setVisibility(View.GONE);
                                btn_login.setBackgroundResource(R.drawable.buttonselector);
                                Utils.alertDialog(LoginActivity.this, "Network Error", "You are not connected to internet");
                            }

                        } else {
                            if (fromWhere.equalsIgnoreCase("fb") || fromWhere.equalsIgnoreCase("google")) {
                                signUp();
                            } else {
                                Utils.alertDialog(LoginActivity.this, "Login Error", "Invalid Credentials");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        loading.setVisibility(View.GONE);
                        btn_login.setBackgroundResource(R.drawable.buttonselector);
                    }

                }
            }


        } else if (object.getTitle().equalsIgnoreCase("Newslist")) {

            db.deleteAllData();

            try {
                JSONArray rootArr = new JSONArray(s);
                for (int i = 0; i < rootArr.length(); i++) {
                    JSONObject obj = rootArr.getJSONObject(i);
                    NewsListModel newsListModel = new NewsListModel();
                    newsListModel.setCategoryId(obj.getString("CategoryId"));
                    newsListModel.setCreatedTs(obj.getString("CreatedTs"));
                    newsListModel.setNewsById(obj.getString("NewsById"));
                    newsListModel.setNewsDescription(obj.getString("NewsDescription"));
                    newsListModel.setNewsId(obj.getString("NewsId"));
                    newsListModel.setNewsPhotoUrl(obj.getString("NewsPhotoUrl"));
                    newsListModel.setNewsTitle(obj.getString("NewsTitle"));
                    newsListModel.setLikeCount(obj.getString("LikeCount"));
                    newsListModel.setDisLikeCount(obj.getString("DisLikeCount"));
                    newsListModel.setSelfLike(obj.getBoolean("selfLike"));
                    newsListModel.setSelfDisLike(obj.getBoolean("selfDisLike"));

                    db.insertNews(newsListModel);

                }

                String token = FirebaseInstanceId.getInstance().getToken();
                FirebaseMessaging.getInstance().subscribeToTopic("NewsApp");


                loading.setVisibility(View.GONE);
                btn_login.setBackgroundResource(R.drawable.buttonselector);
                Intent intent = new Intent(LoginActivity.this, DrawerActivity.class);
                startActivity(intent);

                finish();

            } catch (JSONException e) {
                e.printStackTrace();
                loading.setVisibility(View.GONE);
                btn_login.setBackgroundResource(R.drawable.buttonselector);
            }
        }
    }
        else {
            loading.setVisibility(View.GONE);
            String json = s;
            if(json != null ) {
                if(!TextUtils.isEmpty(json)) {
                    try {
                        JSONObject rootObj = new JSONObject(json);
                        if(rootObj.getBoolean("Success") == true){
                            login(email,"newsapp");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            email = result.getSignInAccount().getEmail().toString();
            bday = "09/05/1990";
            name = result.getSignInAccount().getDisplayName().toString();
            fromWhere = "google";

            //Toast.makeText(this, result.getSignInAccount().getDisplayName().toString(), Toast.LENGTH_SHORT).show();
            // Toast.makeText(this, result.getSignInAccount().getEmail().toString(), Toast.LENGTH_SHORT).show();
            if(result.isSuccess()) {
                final GoogleApiClient client = mGoogleApiClient;
                login(email,"newsapp");
                //handleSignInResult(...)
            } else {
                //handleSignInResult(...);
            }
        } else {
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);

            // Handle other values for requestCode
        }

    }
}

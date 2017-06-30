package com.techynotion.newsplanet;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.techynotion.newsplanet.asyncTask.RegistrationAsyncTaskPost;
import com.techynotion.newsplanet.model.RegistrationModel;
import com.techynotion.newsplanet.model.TaskComplete;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Dell on 12/17/2016.
 */
public class RegistrationActivity extends AppCompatActivity implements OnTaskCompleted {

    EditText edt_fname,edt_lname,edt_emailid,edt_userid,edt_password,edt_confirmpassword,edt_mobileno;
    Button btn_register;
    TextView tv_tile;
    ProgressWheel loading;
    private GradientBackgroundPainter gradientBackgroundPainter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        initiateView();
    }

    public void initiateView()
    {

        edt_fname = (EditText)findViewById(R.id.firstname);
        edt_lname = (EditText)findViewById(R.id.lastname);
        edt_emailid = (EditText)findViewById(R.id.emailid);
        edt_mobileno = (EditText)findViewById(R.id.mobileno);
        edt_userid = (EditText)findViewById(R.id.userid);
        edt_password = (EditText)findViewById(R.id.password);
        edt_confirmpassword = (EditText)findViewById(R.id.confirmpassword);
        loading = (ProgressWheel)findViewById(R.id.loading);
        btn_register = (Button)findViewById(R.id.register);
        tv_tile = (TextView)findViewById(R.id.registrationtitle);

        Typeface face= Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/motionpic.ttf");
        tv_tile.setTypeface(face);
        tv_tile.setTextColor(getResources().getColor(R.color.white));

        View backgroundImage = findViewById(R.id.mainLayout);
        final int[] drawables = new int[3];
        drawables[0] = R.drawable.gradient1;
        drawables[1] = R.drawable.gradient2;
        drawables[2] = R.drawable.gradient3;

        gradientBackgroundPainter = new GradientBackgroundPainter(backgroundImage, drawables);
        gradientBackgroundPainter.start();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(edt_fname.getText().toString().trim())){

                    Utils.alertDialog(RegistrationActivity.this,"Registration","Please Enter First Name");
                }
                else if(TextUtils.isEmpty(edt_lname.getText().toString().trim())){

                    Utils.alertDialog(RegistrationActivity.this,"Registration","Please Enter Last Name");
                }
                else if(TextUtils.isEmpty(edt_userid.getText().toString().trim())){

                    Utils.alertDialog(RegistrationActivity.this,"Registration","Please Enter User Name");
                }
                else if(TextUtils.isEmpty(edt_emailid.getText().toString().trim())){

                    Utils.alertDialog(RegistrationActivity.this,"Registration","Please Enter Email Id");
                }
                else if(TextUtils.isEmpty(edt_mobileno.getText().toString().trim())){

                    Utils.alertDialog(RegistrationActivity.this,"Registration","Please Enter First NameMobile Number");

                }
                else if(TextUtils.isEmpty(edt_password.getText().toString().trim())){

                    Utils.alertDialog(RegistrationActivity.this,"Registration","Please Enter Password");
                }
                else if(TextUtils.isEmpty(edt_confirmpassword.getText().toString().trim())){

                    Utils.alertDialog(RegistrationActivity.this,"Registration","Please Confirm Password");

                }
                else {
                    if(edt_password.getText().toString().trim().equals(edt_confirmpassword.getText().toString().trim()))
                    {
                        loading.setVisibility(View.VISIBLE);
                        btn_register.setBackgroundColor(getResources().getColor(R.color.white));
                        RegistrationModel registrationModel = new RegistrationModel();
                        registrationModel.setUserId(edt_userid.getText().toString().trim());
                        registrationModel.setPassword(edt_password.getText().toString().trim());
                        registrationModel.setFirstName(edt_fname.getText().toString().trim());
                        registrationModel.setLastName(edt_lname.getText().toString().trim());
                        registrationModel.setMobileNo(edt_mobileno.getText().toString().trim());
                        registrationModel.setEmailId(edt_emailid.getText().toString().trim());
                        registrationModel.setUserType("9");
                        registrationModel.setThumbnail("");
                        registrationModel.setGender("news_detail_layout");

                        if(Utils.isConnectingToInternet(RegistrationActivity.this)) {
                            RegistrationAsyncTaskPost registrationAsyncTaskPost = new RegistrationAsyncTaskPost(registrationModel, RegistrationActivity.this, RegistrationActivity.this);
                            registrationAsyncTaskPost.execute();
                        }
                        else {
                            Utils.alertDialog(RegistrationActivity.this,"Network Error","You are not connected to internet");
                        }
                    }
                    else {

                        Utils.alertDialog(RegistrationActivity.this,"Registration","Password and Confirm Password mismatch");

                    }
                }
            }
        });

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

        loading.setVisibility(View.GONE);
        btn_register.setBackgroundResource(R.drawable.buttonselector);
        String json = s;
        if(json != null ) {
            if(!TextUtils.isEmpty(json)) {
                try {
                    JSONObject rootObj = new JSONObject(json);
                    if(rootObj.getBoolean("Success") == true){

                        AlertDialog.Builder adbdialog;
                        adbdialog = new AlertDialog.Builder(RegistrationActivity.this);
                        adbdialog.setTitle("Registration");
                        adbdialog.setMessage("Congratulations Registered Successfully");
                        //adbdialog.setIcon(android.R.drawable.ic_dialog_info);
                        adbdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                        adbdialog.show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }



    }
    @Override protected void onDestroy() {
        super.onDestroy();
        gradientBackgroundPainter.stop();
    }

}

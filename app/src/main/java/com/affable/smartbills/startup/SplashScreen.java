package com.affable.smartbills.startup;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.affable.smartbills.MainActivity;
import com.affable.smartbills.R;
import com.affable.smartbills.utils.Constant;
import com.example.flatdialoglibrary.dialog.FlatDialog;

import java.util.Calendar;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

   /* private LottieAnimationView animationView;
    private TextView textView;
*/
    int day,month , year ;
   Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //check which activity to launch
        SharedPreferences preferences = getSharedPreferences(Constant.PREF_REMEMBER, Context.MODE_PRIVATE);
        boolean isFirstLaunch = preferences.getBoolean(Constant.PREF_REMEMBER_FIRST_LAUNCH, true);
        boolean isLogInSaved = preferences.getBoolean(Constant.PREF_KEY_REMEMBER_LOGIN, false);


        Calendar calendar = Calendar.getInstance();
         year = calendar.get(Calendar.YEAR);

        if(year < 2023){

            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent;

                    if (isFirstLaunch) {
                        intent = new Intent(SplashScreen.this, IntroSlider.class);
                    } else {
                        if (isLogInSaved) {
                            intent = new Intent(SplashScreen.this, MainActivity.class);
                        } else {
                            intent = new Intent(SplashScreen.this, LoginActivity.class);
                        }
                    }

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
            }, 3000);


        }else {

            final FlatDialog flatDialog = new FlatDialog(SplashScreen.this);
            flatDialog.setTitle("Free Limit out")
                    .setSubtitle("Your free version limit is out. \n please connect to us for the paid version. \n thanks")

                    .setFirstButtonText("OK")

                    .withFirstButtonListner(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            flatDialog.dismiss();

                            finish();
                        }
                    })
                    .show();


        }





    /*    animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                textView.animate().alpha(1).setDuration(1500);
            }

            @Override
            public void onAnimationEnd(Animator animation) {



            @Override
            public void onAnimationCancel(Animator animation) {
                //type your code here
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                //type your code here
            }
        });


    }*/
    }


}
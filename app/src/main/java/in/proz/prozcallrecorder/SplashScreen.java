package in.proz.prozcallrecorder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import in.proz.prozcallrecorder.Retrofit.CommonClass;


public class SplashScreen extends AppCompatActivity {
    private static long back_pressed;
    CommonClass commonClass =new CommonClass();
    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            finishAndRemoveTask();
            System.exit(0);
            moveTaskToBack(true);
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //  commonClass.putSharedPref(getApplicationContext(),"user_id",null);

                if(commonClass.isNetworkAvailable(SplashScreen.this)) {
                    if (!TextUtils.isEmpty(commonClass.getSharedPref(getApplicationContext(), "token"))) {
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(intent);
                         finish();
                    } else {
                        Intent intent = new Intent(SplashScreen.this, LoginScreen.class);
                        startActivity(intent);
                         finish();
                    }
                }else{
                     Toast.makeText(getApplicationContext(),"No Internet available",Toast.LENGTH_SHORT).show();
                }

            }
        }, 3000);
    }
}

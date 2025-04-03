package in.proz.prozcallrecorder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tuyenmonkey.mkloader.MKLoader;

import java.io.IOException;


import in.proz.prozcallrecorder.MainActivity;
import in.proz.prozcallrecorder.R;
import in.proz.prozcallrecorder.Retrofit.APIInterface;
import in.proz.prozcallrecorder.Retrofit.ApiClient;
import in.proz.prozcallrecorder.Retrofit.CommonClass;
import in.proz.prozcallrecorder.Retrofit.CommonPojo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginScreen extends AppCompatActivity {
    EditText mobile_number,password;
    CommonClass commonClass =new CommonClass();
    TextView login;
    MKLoader loader;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        initview();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mobile_number.getText().toString())){
                    mobile_number.setError("Enter Username");
                }else if(TextUtils.isEmpty(password.getText().toString())){
                    password.setError("Enter Password");
                }else{
                    loader.setVisibility(View.VISIBLE);
                    APIInterface apiInterface = ApiClient.getApiClient().create(APIInterface.class);
                    Call<CommonPojo> call = apiInterface.getLogin(mobile_number.getText().toString(),password.getText().toString());
                    Log.d("getURL"," url "+call.request().url());
                    call.enqueue(new Callback<CommonPojo>() {
                        @Override
                        public void onResponse(Call<CommonPojo> call, Response<CommonPojo> response) {
                            loader.setVisibility(View.GONE);
                            Log.d("getURL"," code "+response.code());
                            if(response.isSuccessful()){
                                if(response.code()==200){
                                    Log.d("getURL"," success "+response.body().getStatus());
                                    if(response.body().getStatus().equals("success")){
                                        Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_LONG).show();
                                        commonClass.putSharedPref(getApplicationContext(),"token",response.body().getToken_type()+" "+response.body().getBearer_token());
                                        Intent intent =new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(getApplicationContext(),response.body().getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    try {
                                        String errorBody = response.errorBody().string();
                                        Toast.makeText(getApplicationContext(),errorBody,Toast.LENGTH_SHORT).show();
                                     } catch (IOException e) {
                                     }
                                }
                            }else{
                                try {
                                    String errorBody = response.errorBody().string();
                                    Toast.makeText(getApplicationContext(),errorBody,Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<CommonPojo> call, Throwable t) {
                            loader.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void initview() {
        mobile_number = findViewById(R.id.mobile_number);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        loader = findViewById(R.id.loader);
    }
}

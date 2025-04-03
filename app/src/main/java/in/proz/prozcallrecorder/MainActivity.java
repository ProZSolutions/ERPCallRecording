package in.proz.prozcallrecorder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.Manifest;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tuyenmonkey.mkloader.MKLoader;

import java.util.ArrayList;
import java.util.List;

import in.proz.prozcallrecorder.ADapter.CallAdapter;
import in.proz.prozcallrecorder.Modal.CallListModal;
import in.proz.prozcallrecorder.Modal.CallMainModal;
import in.proz.prozcallrecorder.Retrofit.APIInterface;
import in.proz.prozcallrecorder.Retrofit.ApiClient;
import in.proz.prozcallrecorder.Retrofit.CommonClass;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE1 = 1;
    private static final int MANAGE_STORAGE_PERMISSION_CODE = 101;
    String Tag="MobileCallRecording";
    String Page ="MainActivity";

    private static final int REQUEST_CODE_SET_DEFAULT_DIALER = 123;
    CommonClass commonClass =new CommonClass();
    MKLoader loader;
    ImageView back_arrow;
    RecyclerView recyclerView;
    TextView no_data;
    CallAdapter callAdapter;
    List<CallListModal> callListModalList =new ArrayList<>();
    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;
    int pageNo =1;
    GridLayoutManager manager;
    ImageView logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         checkAndRequestAccessibilityService();
        startService(new Intent(MainActivity.this, CallRecorderService.class));
        requestPermissions(new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_NUMBERS,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_CONTACTS}, 5);

        // Request permissions
        String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE
              };
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE1);
        checkAndRequestPermissions();
        int currentApiLevel = android.os.Build.VERSION.SDK_INT;
        Log.d("PROZCall", "Current API Level: " + currentApiLevel);

        // getRecordingFile (MainActivity.this,"sample.mp3");
        initView();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if(isScrolling && (currentItems + scrollOutItems == totalItems))
                {
                    isScrolling = false;
                    pageNo+=1;
                    getList();
                }
            }
        });

    }
    private boolean hasManageStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();  // Correct way to check All Files Access
        }
        return true;  // For Android 10 and below, assume granted
    }

    public void checkAndRequestAccessibilityService() {
        if (!AccessibilityHelper.isAccessibilityServiceEnabled(this, CallAccessibilityService.class)) {
            Toast.makeText(this, "Please enable Call Accessibility Service.", Toast.LENGTH_SHORT).show();

            // Open Accessibility Settings
            try {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:"+getPackageName()));
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Enable Accessibility Service");
                builder.setMessage("Go to: Settings → Accessibility → Proz Call Recorder → Enable Call Accessibility Service.");
                builder.setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                });
                builder.show();

                Log.d("ACCESSIBILITY_TEST"," coe to rlse "+e.getMessage());
                Toast.makeText(this, "Unable to open Accessibility Settings", Toast.LENGTH_SHORT).show();
            }


        } else {
         }
    }
    private boolean hasBasicStoragePermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    public void initView(){
        logout= findViewById(R.id.logout);
        loader = findViewById(R.id.loader);
        back_arrow = findViewById(R.id.back_arrow);
        recyclerView = findViewById(R.id.recyclerView);
        no_data = findViewById(R.id.no_data);
        manager=new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(manager);


        callAdapter = new CallAdapter(MainActivity.this,
                callListModalList, getSupportFragmentManager());
        recyclerView.setAdapter(callAdapter);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(MainActivity.this);
                alert.setMessage("Would you like to logout?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        commonClass.putSharedPref(getApplicationContext(),"token",null);
                        Intent intent =new Intent(getApplicationContext(), LoginScreen.class);
                        startActivity(intent);
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.create();
                alert.show();
            }
        });


        getList();
    }

    private void getList() {
        loader.setVisibility(View.VISIBLE);
        APIInterface apiInterface = ApiClient.getTokenRetrofit(commonClass.getSharedPref(getApplicationContext(), "token"))
                .create(APIInterface.class);

        Call<CallMainModal> call = apiInterface.getCallTrackList(
                "",
                commonClass.getSharedPref(getApplicationContext(), "device_id"),
                pageNo
        );

        Log.d("getURL", " list " + call.request().url());

        call.enqueue(new Callback<CallMainModal>() {
            @Override
            public void onResponse(Call<CallMainModal> call, Response<CallMainModal> response) {
                loader.setVisibility(View.GONE);
                Log.d("getURL", " code " + response.code());

                if (response.isSuccessful() && response.code() == 200) {
                    List<CallListModal> callMainModal = response.body().getData().getCallDetailsList();
                    if(callMainModal!=null){
                        if(callMainModal.size()!=0){
                            no_data.setVisibility(View.GONE);
                            if (pageNo == 1) {
                                callListModalList.clear();
                            }
                            callListModalList.addAll(callMainModal);
                            callAdapter.notifyDataSetChanged();
                        }else{
                            handleEmptyData();
                        }
                    }else{
                        handleEmptyData();

                    }


                } else {
                    handleEmptyData();
                }
            }

            @Override
            public void onFailure(Call<CallMainModal> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Log.d("getURL", " rrr " + t.getMessage());
                handleEmptyData();
            }
        });
    }


    private void handleEmptyData() {
        if (pageNo == 1) {
            callListModalList.clear();
            no_data.setVisibility(View.VISIBLE);
        }
        callAdapter.notifyDataSetChanged();
    }

    private void promptUserToSetDefaultDialer() {
        TelecomManager telecomManager = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
        if (telecomManager != null && !getPackageName().equals(telecomManager.getDefaultDialerPackage())) {
            Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
            intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());
            startActivityForResult(intent, REQUEST_CODE_SET_DEFAULT_DIALER);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SET_DEFAULT_DIALER) {
            if (resultCode == Activity.RESULT_OK) {
                // The user has set your app as the default dialer
                Log.d(Tag, Page + " App is now the default dialer");
            } else {
                // The user declined to set your app as the default dialer
                Log.d(Tag, Page + " App was not set as the default dialer");
            }
        }
    }
    private void getDeviceMobileNumber() {
        Log.d(Tag,Page+" device mobile number called ");

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        if (checkSelfPermission(Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED) {
            String deviceNumber = telephonyManager.getLine1Number();
            Log.d(Tag,Page+" device numner "+deviceNumber);


            if (deviceNumber != null && !deviceNumber.isEmpty()) {
                commonClass.putSharedPref(getApplicationContext(),"device_no",deviceNumber);
                //   Toast.makeText(this, "Device Number: " + deviceNumber, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Unable to retrieve mobile number", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
        }
    }
  private void requestManageStoragePermission() {
      try {
          Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
          intent.setData(Uri.parse("package:" + getPackageName()));
          startActivity(intent);
      } catch (Exception e) {
          // If above intent fails, open general All Files Access settings
          Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
          startActivity(intent);
      }
  }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {  // Android 11+ (API 30+)
            if (  !hasManageStoragePermission()) {
                requestManageStoragePermission();
            } else {
             }
        } else { // Android 10 and below
            String[] permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            if (!hasBasicStoragePermissions()) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
            } else {
                Toast.makeText(this, "Storage permissions already granted!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(Tag,Page+" request code "+requestCode);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean recordAudioGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean phoneStateGranted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (recordAudioGranted && phoneStateGranted) {

                } else {
                     Toast.makeText(this, "Permissions required for call recording!", Toast.LENGTH_LONG).show();
                    finish(); // Close the app if permissions are denied
                }
            }
        }else if (requestCode == 5) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            Log.d(Tag,Page+" all granded "+allGranted);


            if (allGranted) {
                Log.d(Tag,Page+" all permission granded ");

                getDeviceMobileNumber();
                promptUserToSetDefaultDialer();

               // startService(new Intent(this, CallRecordingService.class));
            } else {
                requestPermissions(new String[]{
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_PHONE_NUMBERS,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.READ_CONTACTS
                }, 1);
                Toast.makeText(this, "Permissions Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

}
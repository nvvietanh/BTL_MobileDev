package com.example.appdocbao.Activity;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.appdocbao.BroadcastReceiver.Internet;
import com.example.appdocbao.Fragment.GiftFragment;
import com.example.appdocbao.Fragment.NewsFragment;
import com.example.appdocbao.Fragment.ProfileFragment;
import com.example.appdocbao.Fragment.UserFragment;
import com.example.appdocbao.R;
import com.example.appdocbao.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DrawerLayout drawerLayout;
    private Internet internetBroadcastReceiver;
    private boolean isReceiverRegistered = false;

    static Map cloudinaryConfig = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // config Cloudinary
        cloudinaryConfig.put("cloud_name", "ngvvanh261");
        cloudinaryConfig.put("api_key","581333286277713");
        cloudinaryConfig.put("api_secret","pY2f1rWksmZWfpEomvVhKbB5Sjo");

        // Kiểm tra trạng thái chế độ tối đã được lưu trong SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        boolean nightMode = sharedPreferences.getBoolean("night", false);

        // Áp dụng chế độ tối nếu được bật
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        internetBroadcastReceiver = new Internet();

        // Đặt NewsFragment là fragment mặc định khi mở app
        replaceFragement(new NewsFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.news) {
                // Gọi giao diện danh sách tin tức
                replaceFragement(new NewsFragment());
            } else if (itemId == R.id.gif) {
                // Gọi fragment đổi thưởng
                replaceFragement(new GiftFragment());
            } else if (itemId == R.id.trend) {
                // Gọi fragment tin tức được yêu thích
            } else if (itemId == R.id.profile) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
                if (user != null || googleSignInAccount != null) {
                    replaceFragement(new UserFragment());
                }else{
                    replaceFragement(new ProfileFragment());
                }
            }
            return true;
        });
    }
    public void hideBottomNavigationView() {
        binding.bottomNavigationView.setVisibility(View.GONE);
    }

    public void showBottomNavigationView() {
        binding.bottomNavigationView.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Đăng ký BroadcastReceiver khi Activity được hiển thị
        if (!isReceiverRegistered) {
            registerReceiver(internetBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            isReceiverRegistered = true;
        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        // Hủy đăng ký BroadcastReceiver khi Activity không còn hiển thị
//        if (isReceiverRegistered) {
//            unregisterReceiver(internetBroadcastReceiver);
//            isReceiverRegistered = false;
//        }
//    }
    private void replaceFragement(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public static Map getCloudinaryConfig() {
        return cloudinaryConfig;
    }

}
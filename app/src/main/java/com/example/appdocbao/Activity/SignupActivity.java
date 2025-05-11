package com.example.appdocbao.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.UploadCallback;
import com.example.appdocbao.Model.User;
import com.example.appdocbao.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import com.example.appdocbao.Activity.MainActivity;

//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "Upload ###";
    private EditText signupName, signupPhoneNumber, signupUsername, signupPassword;
    private TextView txtlogin;
    private Button btnregis;
    private FirebaseAuth mAuth;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    Uri uri;
    String imageUrl;
    ImageView imageUserUpload, backButton;
    boolean isUploadImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();

        imageUserUpload = (ImageView) findViewById(R.id.imageUserUpload);
        signupName = (EditText) findViewById(R.id.signupName);
        signupPhoneNumber = (EditText) findViewById(R.id.signupPhoneNumber);
        signupUsername = (EditText) findViewById(R.id.signupUsername);
        signupPassword = (EditText) findViewById(R.id.signupPassword);
        btnregis = (Button) findViewById(R.id.signupBtn);
        txtlogin = (TextView) findViewById(R.id.textSignUp);
        backButton = (ImageView) findViewById(R.id.backButton);

        MediaManager.init(this, MainActivity.getCloudinaryConfig());

        txtlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(it);
            }
        });
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            isUploadImage = true;
                            Intent data = result.getData();
                            uri = data.getData();
                            imageUserUpload.setImageURI(uri);
                        }else{
                            Toast.makeText(SignupActivity.this, "No Image Upload", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        imageUserUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });
        btnregis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isUploadImage && validateInforUser()) {
                    onClickPushData();
                    register();
                }else{
                    Toast.makeText(SignupActivity.this,"Vui lòng chọn ảnh !",Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, MainActivity.class));
            }
        });
    }

    private void onClickPushData() {
//        if (uri != null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
            builder.setCancelable(false);
            builder.setView(R.layout.progress_layout);
            AlertDialog dialog = builder.create();
            dialog.show();

            MediaManager.get().upload(uri).callback(new UploadCallback() {
                @Override
                public void onStart(String requestId) {
                    Log.d(TAG, "onStart: "+"started");
                }

                @Override
                public void onProgress(String requestId, long bytes, long totalBytes) {
                    Log.d(TAG, "onStart: "+"uploading");
                }

                @Override
                public void onSuccess(String requestId, Map resultData) {
                    Log.d(TAG, "onStart: "+"usuccess");
                    // txt.setText(resultData.get("url").toString());
                    Toast.makeText(SignupActivity.this, "Tải ảnh lên thành công", Toast.LENGTH_SHORT).show();
                    imageUrl = resultData.get("url").toString();
                    uploadData();
                    dialog.dismiss();
                    // Toast.makeText(SignupActivity.this, "", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String requestId, ErrorInfo error) {
                    Log.d(TAG, "onStart: "+error);
                    dialog.dismiss();
                    Toast.makeText(SignupActivity.this, "Lỗi tải ảnh: " + error.getDescription(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onReschedule(String requestId, ErrorInfo error) {
                    Log.d(TAG, "onStart: "+error);
                    
                }
            }).dispatch();

//        } else {
//            Toast.makeText(this, "Không thể tạo đối tượng StorageReference với giá trị URI null", Toast.LENGTH_SHORT).show();
////            String defaultImageUri = "https://firebasestorage.googleapis.com/v0/b/appdocbao-75d78.appspot.com/o/user-profile-icon.png?alt=media&token=c41f08e0-0f6f-413d-bc33-4a1f3f638dd9";
////            imageUrl = defaultImageUri;
////            uploadData();
//        }
    }

    private void uploadToCloudinary(Uri imageUri) {
        if (imageUri == null) {
            Toast.makeText(this, "Không tìm thấy ảnh để tải lên!", Toast.LENGTH_SHORT).show();
            return;
        }

        // String uploadUrl = "https://api.cloudinary.com/v1_1/ngvvanh261/image/upload";
        // String uploadPreset = "ngvvanh261";

         AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
         builder.setCancelable(false);
         builder.setView(R.layout.progress_layout);
         AlertDialog dialog = builder.create();
         dialog.show();

         try {
        //     // Mở kết nối
        //     URL url = new URL(uploadUrl);
        //     HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //     connection.setRequestMethod("POST");
        //     connection.setDoOutput(true);
        //     connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=*****");

        //     // Gửi dữ liệu
        //     DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        //     outputStream.writeBytes("--*****\r\n");
        //     outputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + imageUri.getLastPathSegment() + "\"\r\n");
        //     outputStream.writeBytes("\r\n");
        //     InputStream inputStream = getContentResolver().openInputStream(imageUri);
        //     byte[] buffer = new byte[1024];
        //     int bytesRead;
        //     while ((bytesRead = inputStream.read(buffer)) != -1) {
        //         outputStream.write(buffer, 0, bytesRead);
        //     }
        //     outputStream.writeBytes("\r\n");
        //     outputStream.writeBytes("--*****\r\n");
        //     outputStream.writeBytes("Content-Disposition: form-data; name=\"upload_preset\"\r\n");
        //     outputStream.writeBytes("\r\n");
        //     outputStream.writeBytes(uploadPreset + "\r\n");
        //     outputStream.writeBytes("--*****--\r\n");

        //     outputStream.flush();
        //     outputStream.close();

        //     // Đọc phản hồi từ Cloudinary
        //     InputStream responseStream = connection.getInputStream();
        //     BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));
        //     StringBuilder response = new StringBuilder();
        //     String line;
        //     while ((line = reader.readLine()) != null) {
        //         response.append(line);
        //     }
        //     reader.close();

        //     Log.d("CloudinaryResponse", response.toString()); // In phản hồi ra log

        //     // Phân tích JSON để lấy URL ảnh
        //     JSONObject jsonResponse = new JSONObject(response.toString());
        //     imageUrl = jsonResponse.getString("secure_url");
        //     uploadData(); // Tiếp tục lưu thông tin người dùng vào Firebase
        //     dialog.dismiss();

        MediaManager.get().upload(imageUri).callback(new UploadCallback() {
                @Override
                public void onStart(String requestId) {
                    Log.d(TAG, "onStart: "+"started");
                }

                @Override
                public void onProgress(String requestId, long bytes, long totalBytes) {
                    Log.d(TAG, "onStart: "+"uploading");
                }

                @Override
                public void onSuccess(String requestId, Map resultData) {
                    Log.d(TAG, "onStart: "+"usuccess");
                    // txt.setText(resultData.get("url").toString());
                    imageUrl = resultData.get("url").toString();
                }

                @Override
                public void onError(String requestId, ErrorInfo error) {
                    Log.d(TAG, "onStart: "+error);
                }

                @Override
                public void onReschedule(String requestId, ErrorInfo error) {
                    Log.d(TAG, "onStart: "+error);
                }
            }).dispatch();

        } catch (Exception e) {
            e.printStackTrace(); // In chi tiết lỗi ra log
            Toast.makeText(SignupActivity.this, "Lỗi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }

    private void uploadData(){
        String name = signupName.getText().toString();
        String phone = signupPhoneNumber.getText().toString();
        String email = signupUsername.getText().toString();

//        Toast.makeText(this, "Name: "+name+" Phone: "+phone+" Email: "+email+" Image: "+imageUrl, Toast.LENGTH_LONG).show();
        Log.d("TAG", "uploadData: Name: "+name+" Phone: "+phone+" Email: "+email+" Image: "+imageUrl);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

//        Log.d("TAG", "uploadData: "+ database.toString());

        FirebaseUser userFirebase = firebaseAuth.getCurrentUser();
        String id = userFirebase.getUid();

        Log.d("TAG", "uploadData: Uid="+ id);
        User user = new User(id, name,email,0,phone,imageUrl);
//        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        myRef.child(id).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignupActivity.this, "User Info Saved",Toast.LENGTH_SHORT).show();
                            Log.d("TAG", "uploadData:onComplete: User Info Saved");
                            finish();
                        }
                        Log.d("TAG", "uploadData:onComplete: "+task.isSuccessful());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignupActivity.this, e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "uploadData:onFailure: "+e.getMessage().toString());
                    }
                });
    }

    private void register(){
        String email, password;
        email = signupUsername.getText().toString();
        password = signupPassword.getText().toString();
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();
                    Intent it = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(it);
                }else{
                    Toast.makeText(getApplicationContext(),"Tạo tài khoản thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateInforUser(){
        String email, password, name, phone;
        name = signupName.getText().toString();
        phone = signupPhoneNumber.getText().toString();
        email = signupUsername.getText().toString();
        password = signupPassword.getText().toString();
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Vui lòng nhập họ và tên.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Vui lòng nhập số điện thoại.", Toast.LENGTH_SHORT).show();
            return false;
        }else if (!isValidPhoneNumber(phone)) {
            Toast.makeText(this, "Số điện thoại không hợp lệ.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Vui lòng nhập email.", Toast.LENGTH_SHORT).show();
            return false;
        }else if (!isValidEmail(email)) {
            Toast.makeText(this, "Email không hợp lệ.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Vui lòng nhập password.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private boolean isValidPhoneNumber(String phoneNumber) {
        // Định dạng số điện thoại: bắt đầu bằng số 84|03|05|07|08|09 và sau đó là 8 cs
        String regex = "^(84|0[35789])\\d{8}$";
        return phoneNumber.matches(regex);
    }
    private boolean isValidEmail(String email) {
        // Định dạng email hợp lệ
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(regex);
    }
}
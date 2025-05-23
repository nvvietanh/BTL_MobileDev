package com.example.appdocbao.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.appdocbao.Adapter.RecentlyReadAdapter;
import com.example.appdocbao.Model.Article;
import com.example.appdocbao.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecentlyReadActivity extends AppCompatActivity {
    private RecyclerView rcvRecentlyRead;
    private RecentlyReadAdapter recentlyReadAdapter;
    private ArrayList<Article> listArticle;
    DatabaseReference recentlyReadRef;
    ImageView deleteRecentlyRead;
    ImageView backMain;
    LinearLayout empty;
    ScrollView mainView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recently_read);
        rcvRecentlyRead = findViewById(R.id.rcvRecentlyRead);
        deleteRecentlyRead = findViewById(R.id.deleteRecentlyRead);
        backMain = findViewById(R.id.backMain);
        empty = findViewById(R.id.empty);
        mainView = findViewById(R.id.mainView);
//        progressIndicator = findViewById(R.id.progress_bar);
        setupRecycleView();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
//        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        listArticle = new ArrayList<>();
        recentlyReadAdapter= new RecentlyReadAdapter(this,listArticle);
        rcvRecentlyRead.setAdapter(recentlyReadAdapter);

        FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        final String id_User = mAuth!=null ? mAuth.getUid() : (googleSignInAccount != null ? googleSignInAccount.getId() : "" );

        recentlyReadRef = FirebaseDatabase.getInstance().getReference("recently_read/"+id_User);
//        dialog.show();
        recentlyReadRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> articleIds = new ArrayList<>();
                Map<String, Long> articleTimeMap = new HashMap<>(); // Lưu trữ thời gian đọc của mỗi bài báo

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String articleId = childSnapshot.getKey();
                    long timestamp = childSnapshot.getValue(Long.class);
                    articleIds.add(articleId);
                    articleTimeMap.put(articleId, timestamp);
                }

                // Truy cập vào nút "articles" và lấy thông tin của từng bài báo
                DatabaseReference articlesRef = FirebaseDatabase.getInstance().getReference("articles");
                articlesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            String articleId = childSnapshot.getKey();
                            if (articleIds.contains(articleId)) {
                                Article article = childSnapshot.getValue(Article.class);
                                if (article != null) {
                                    listArticle.add(article);
                                }
                            }
                        }

                        // Sắp xếp danh sách bài báo theo thứ tự giảm dần của thời gian đọc
                        Collections.sort(listArticle, new Comparator<Article>() {
                            @Override
                            public int compare(Article article1, Article article2) {
                                Long timestamp1 = articleTimeMap.get(article1.getId());
                                Long timestamp2 = articleTimeMap.get(article2.getId());
                                if (timestamp1 != null && timestamp2 != null) {
                                    return Long.compare(timestamp2, timestamp1);
                                } else if (timestamp1 != null) {
                                    return -1; // timestamp2 is null, consider timestamp1 smaller
                                } else if (timestamp2 != null) {
                                    return 1; // timestamp1 is null, consider timestamp2 smaller
                                } else {
                                    return 0; // both timestamps are null, consider them equal
                                }
                            }
                        });
                        //thông báo cho Adapter biết rằng dữ liệu đầu vào đã thay đổi và cần cập nhật giao diện tương ứng
                        recentlyReadAdapter.notifyDataSetChanged();
                        changeView(listArticle);
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý lỗi nếu có
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });
        deleteRecentlyRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RecentlyReadActivity.this);
                builder.setTitle("Xóa bài báo đọc gần đây");
                builder.setMessage("Bạn có chắc chắn muốn xóa bài báo đọc gần đây không?");
                builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Xử lý logic xóa danh sách Recently Read

                        Task<Void> recentlyReadRef =  FirebaseDatabase.getInstance().getReference("recently_read/"+id_User)
                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Xóa dữ liệu thành công
                                            Toast.makeText(RecentlyReadActivity.this, "Đã xóa bài báo đọc gần đây", Toast.LENGTH_SHORT).show();
                                            recreate();
                                        } else {
                                            // Xóa dữ liệu thất bại
                                            Toast.makeText(RecentlyReadActivity.this, "Lỗi khi xóa dữ liệu", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        backMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    void setupRecycleView(){
        rcvRecentlyRead.setLayoutManager(new LinearLayoutManager(this));
        rcvRecentlyRead.setHasFixedSize(true);
        recentlyReadAdapter = new RecentlyReadAdapter(this,listArticle);
        rcvRecentlyRead.setAdapter(recentlyReadAdapter);
    }
    private void changeView (ArrayList<Article> listArticle){
        if(listArticle.isEmpty()){
            empty.setVisibility(View.VISIBLE);
            mainView.setVisibility(View.GONE);
        }else{
            empty.setVisibility(View.GONE);
            mainView.setVisibility(View.VISIBLE);
        }
    }
}
package com.example.appdocbao.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdocbao.Adapter.RecyclerArticleAdapter;
import com.example.appdocbao.Model.Article;
import com.example.appdocbao.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NewsFragment extends Fragment {

    LinearProgressIndicator progressIndicator;
    private RecyclerView rcvArticle;
    private RecyclerArticleAdapter articleAdapter;

    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;

    private ArrayList<Article> listArticle;
    private DrawerLayout drawerLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rcvArticle = view.findViewById(R.id.recycleview_items);
        listArticle = new ArrayList<>();




        setupRecycleView();
        Toolbar toolbar =(Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        drawerLayout = view.findViewById(R.id.drawerlayout);
        NavigationView navigationView = view.findViewById(R.id.navigation_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.close_nav, R.string.open_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.news){
                    drawerLayout.closeDrawer(GravityCompat.START);
                    replaceFragement(new NewsFragment());
                }else if(item.getItemId()==R.id.setting) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    replaceFragement(new SettingFragment());
                }else if(item.getItemId()==R.id.contact) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    replaceFragement(new ContactFragment());
                } else if(item.getItemId()==R.id.share) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    replaceFragement(new ShareFragment());
                }
                return true;
            }
        });
        getAllArticles();
    }
    private void replaceFragement(Fragment fragment) {
        FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
        fm.replace(R.id.frame_layout,fragment).commit();
    }

    private void setupRecycleView() {
        rcvArticle.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvArticle.setHasFixedSize(true);
        articleAdapter = new RecyclerArticleAdapter(getContext(), listArticle, 30);
        rcvArticle.setAdapter(articleAdapter);
    }

    private void getAllArticles() {
        databaseReference = FirebaseDatabase.getInstance().getReference("articles");
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listArticle.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Article article = itemSnapshot.getValue(Article.class);
                    if (article != null) {
                        listArticle.add(article);
                    }
                }
                // Sắp xếp theo thời gian giảm dần nếu có timestamp
                Collections.sort(listArticle, new Comparator<Article>() {
                    @Override
                    public int compare(Article a1, Article a2) {
                        return Long.compare(a2.getTimestamp(), a1.getTimestamp());
                    }
                });
                articleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }



}

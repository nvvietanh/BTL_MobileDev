package com.example.appdocbao.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdocbao.Adapter.RecyclerArticleAdapter;
import com.example.appdocbao.Model.Article;
import com.example.appdocbao.R;
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


public class TrendFragment extends Fragment {
    private RecyclerView rcvLikestArticle, rcv;
    private RecyclerArticleAdapter articleAdapter;
    private ArrayList<Article> listArticle;
    ValueEventListener eventListener;
    private boolean isListLoaded = false;
    ProgressBar progressbar;
    AppCompatButton detailLike, detailView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trend, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        detailLike = view.findViewById(R.id.detailLikest);

        detailView = view.findViewById(R.id.detailViewest);


        progressbar = view.findViewById(R.id.progressbar);
        rcvLikestArticle = view.findViewById(R.id.recyclerView);
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("likes");

        listArticle = new ArrayList<>();
        articleAdapter = new RecyclerArticleAdapter(getContext(), listArticle, 3);
        rcvLikestArticle.setAdapter(articleAdapter);
        rcvLikestArticle.setLayoutManager(new LinearLayoutManager(getContext()));



    }

}
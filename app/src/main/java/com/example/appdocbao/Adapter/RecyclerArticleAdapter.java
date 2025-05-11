package com.example.appdocbao.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.appdocbao.Model.Article;
import com.example.appdocbao.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;

import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class RecyclerArticleAdapter extends RecyclerView.Adapter<RecyclerArticleAdapter.ArticleViewHolder> {
    private Context mContext;
    private ArrayList<Article> mListArticle;
    private int amountArticleShow;

    public RecyclerArticleAdapter(Context mContext, ArrayList<Article> mListArticle, int amountArticleShow) {
        this.mContext = mContext;
        this.mListArticle = mListArticle;
        this.amountArticleShow = amountArticleShow;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View articleView = inflater.inflate(R.layout.item_articles, parent, false);
        ArticleViewHolder viewHolder = new ArticleViewHolder(articleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = mListArticle.get(position);
        if(article == null){
            return;
        }
        holder.titleArticle.setText(article.getTitle());
        Glide.with(mContext)
                .load(article.getImg())
                .into(holder.imgArticle);
        holder.authorArticle.setText(article.getAuthor());
        long timestamp = article.getTimestamp();
        Date dateTime = new Date(timestamp);
        String formattedDateTime;

        // Thời gian hiện tại
        Date now = new Date();
        //Tính toán xem thời gian đăng báo cách thời gian hiện tại bao lâu
        long diffInMillis = now.getTime() - timestamp;

        long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
        long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);

        if (days < 1) {
            if (hours < 1) {
                // Hiển thị số phút
                formattedDateTime = minutes + " phút trước";
            } else {
                // Hiển thị số giờ
                formattedDateTime = hours + " giờ trước";
            }
        } else {
            // Hiển thị ngày, tháng, năm
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            formattedDateTime = dateFormat.format(dateTime);
        }
        holder.dateArticle.setText(formattedDateTime);

    }

    @Override
    public int getItemCount() {
        if(amountArticleShow >= mListArticle.size()){
            return mListArticle.size();
        }else{
            return amountArticleShow;
        }
    }

    public class ArticleViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgArticle;
        private TextView titleArticle;
        private TextView authorArticle;
        private TextView dateArticle;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            imgArticle = itemView.findViewById(R.id.imgArticle);
            titleArticle = itemView.findViewById(R.id.titleArticle);
            authorArticle = itemView.findViewById(R.id.authorArticle);
            dateArticle = itemView.findViewById(R.id.dateArticle);
        }
    }

}

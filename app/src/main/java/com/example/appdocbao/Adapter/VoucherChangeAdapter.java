package com.example.appdocbao.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appdocbao.Model.Voucher;
import com.example.appdocbao.R;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class VoucherChangeAdapter extends RecyclerView.Adapter<VoucherChangeAdapter.VoucherChangeViewHolder>{

    private Context mContext;

    private ArrayList<Voucher> mListVoucher;
    public VoucherChangeAdapter(Context mContext, ArrayList<Voucher> mListVoucher) {
        this.mContext = mContext;
        this.mListVoucher = mListVoucher;
    }
    @NonNull
    @Override
    public VoucherChangeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View voucherView = inflater.inflate(R.layout.item_voucher_change, parent, false);
        VoucherChangeViewHolder viewHolder = new VoucherChangeViewHolder(voucherView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherChangeViewHolder holder, int position) {
        Voucher voucher = mListVoucher.get(position);
        if(voucher == null){
            return;
        }
        holder.title.setText(voucher.getTitle());
        Glide.with(mContext)
                .load(voucher.getImg())
                .into(holder.imgvoucher);
        long timestamp = voucher.getExpiryTimestamp();
        Date dateTime = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDateTime = dateFormat.format(dateTime);
        holder.expiryDate.setText(formattedDateTime);

//        try {
//            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
//            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
//            Bitmap bitmap = barcodeEncoder.encodeBitmap(voucher.getId(), BarcodeFormat.CODE_128, screenWidth, 100);
//            holder.barcode.setImageBitmap(bitmap);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        holder.barcode.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                holder.barcode.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int barcodeWidth = holder.barcode.getWidth(); // Lấy chiều rộng của ImageView
                try {
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.encodeBitmap(voucher.getKey(), BarcodeFormat.CODE_128, barcodeWidth * 7 / 10, 100);
                    holder.barcode.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.code.setText(voucher.getKey());

    }

    @Override
    public int getItemCount() {
        return mListVoucher.size();
    }

    public class VoucherChangeViewHolder extends RecyclerView.ViewHolder{
        ImageView imgvoucher, barcode;
        TextView title, expiryDate, code;
        public VoucherChangeViewHolder(@NonNull View itemView) {
            super(itemView);
            imgvoucher = itemView.findViewById(R.id.voucher);
            title = itemView.findViewById(R.id.title);
            expiryDate = itemView.findViewById(R.id.expirydate);
            code = itemView.findViewById(R.id.code);
            barcode = itemView.findViewById(R.id.barcode);
        }
    }
}

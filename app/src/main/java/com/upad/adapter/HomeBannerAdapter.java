package com.upad.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.upad.R;
import com.upad.model.BannerModel;
import com.upad.utils.LogUtil;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

public class HomeBannerAdapter extends BannerAdapter<BannerModel, HomeBannerAdapter.ViewHolder> {

    public HomeBannerAdapter(List<BannerModel> datas) {
        super(datas);
    }

    @Override
    public ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindView(ViewHolder holder, BannerModel data, int position, int size) {
        holder.imageView.setImageResource(data.getPicId());
        holder.tvTitle.setText(data.getTitle());
        holder.tvDes.setText(data.getDes());
/*        holder.imageView.post(new Runnable() {
            @Override
            public void run() {
                LogUtil.Log("HomeBannerAdapter-width:"+holder.imageView.getWidth()+"; height:"+holder.imageView.getHeight());
            }
        });*/
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvTitle;
        TextView tvDes;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.banner_image);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDes = itemView.findViewById(R.id.tv_des);
        }
    }
}

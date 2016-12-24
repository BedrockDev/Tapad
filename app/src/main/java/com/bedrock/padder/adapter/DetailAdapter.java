package com.bedrock.padder.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bedrock.padder.R;
import com.bedrock.padder.helper.AnimService;
import com.bedrock.padder.helper.IntentService;
import com.bedrock.padder.helper.RecyclerTouchListener;
import com.bedrock.padder.helper.WindowService;
import com.bedrock.padder.model.about.About;
import com.bedrock.padder.model.about.Item;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.DetailViewHolder> {
    private About about;
    private int rowLayout;
    private Context context;
    private Activity activity;

    WindowService window = new WindowService();

    public static class DetailViewHolder extends RecyclerView.ViewHolder {
        TextView detailTitle;
        RecyclerView itemRecycleView;

        public DetailViewHolder(View view) {
            super(view);
            detailTitle = (TextView) view.findViewById(R.id.layout_detail_title);
            itemRecycleView = (RecyclerView) view.findViewById(R.id.layout_item_recyclerview);
        }
    }

    public DetailAdapter(About about, int rowLayout, Context context, Activity activity) {
        this.about = about;
        this.rowLayout = rowLayout;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public DetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DetailViewHolder holder, int position) {
        holder.detailTitle.setText(String.valueOf(about.getDetail(position).getTitle()));
        holder.detailTitle.setTextColor(context.getResources().getColor(window.getColorId(about.getActionbarColorId())));

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        holder.itemRecycleView.setLayoutManager(layoutManager);
        holder.itemRecycleView.setAdapter(new ItemAdapter(about.getDetail(position).getItems(), R.layout.adapter_item, context, activity));
    }

    @Override
    public int getItemCount() {
        return about.getDetails().length;
    }

    private void onListItemClick(final Item item, final DetailViewHolder holder) {
    }
}
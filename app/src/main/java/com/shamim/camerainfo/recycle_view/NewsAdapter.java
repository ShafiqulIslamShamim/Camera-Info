package com.shamim.camerainfo.recycle_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.shamim.camerainfo.R;
import com.shamim.camerainfo.activity.*;
import com.shamim.camerainfo.c2api_key.*;
import com.shamim.camerainfo.exception_catcher.*;
import com.shamim.camerainfo.preference.*;
import com.shamim.camerainfo.update_checker.*;
import com.shamim.camerainfo.util.*;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

  private final String[] titles;
  private final int[] icons;
  private final OnItemClickListener listener;

  public interface OnItemClickListener {
    void onItemClick(int position);
  }

  public NewsAdapter(String[] titles, int[] icons, OnItemClickListener listener) {
    this.titles = titles;
    this.icons = icons;
    this.listener = listener;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_option, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.title.setText(titles[position]);
    holder.icon.setImageResource(icons[position]);

    holder.itemView.setOnClickListener(
        v -> {
          if (listener != null) listener.onItemClick(position);
        });
  }

  @Override
  public int getItemCount() {
    return titles.length;
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    ImageView icon;
    TextView title;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      icon = itemView.findViewById(R.id.item_icon);
      title = itemView.findViewById(R.id.item_title);
    }
  }
}

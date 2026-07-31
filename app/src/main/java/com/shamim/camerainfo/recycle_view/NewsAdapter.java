/*
 * Copyright (c) 2026 Shafiqul Islam Shamim
 * GitHub: https://github.com/ShafiqulIslamShamim/Camera-Info
 *
 * All Rights Reserved.
 *
 * This source code is made publicly available solely for viewing, collaboration,
 * educational reference, and submitting pull requests to the official repository.
 *
 * No permission is granted to copy, modify, redistribute, sublicense, or use
 * this source code, in whole or in part, for personal, commercial, or any other
 * purpose without the prior written permission of the copyright holder.
 */
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

  /** Listener interface for handling item click actions on options. */
  public interface OnItemClickListener {
    /**
     * Triggered when an item is clicked.
     *
     * @param position The position of the clicked item in the adapter.
     */
    void onItemClick(int position);
  }

  /**
   * Constructs a new NewsAdapter.
   *
   * @param titles List of text titles for the grid items.
   * @param icons Array of resource IDs representing icons corresponding to the titles.
   * @param listener Callback receiver for click events.
   */
  public NewsAdapter(String[] titles, int[] icons, OnItemClickListener listener) {
    this.titles = titles;
    this.icons = icons;
    this.listener = listener;
  }

  /** Standard onCreateViewHolder implementation to inflate the view. */
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_option, parent, false);
    return new ViewHolder(view);
  }

  /** Binds data (title and icon) and click listener to the ViewHolder at the specified position. */
  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.title.setText(titles[position]);
    holder.icon.setImageResource(icons[position]);

    holder.itemView.setOnClickListener(
        v -> {
          if (listener != null) listener.onItemClick(position);
        });
  }

  /** Returns the count of total items to be bound in the RecyclerView. */
  @Override
  public int getItemCount() {
    return titles.length;
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    ImageView icon;
    TextView title;

    /**
     * ViewHolder constructor, mapping views to local fields.
     *
     * @param itemView The instantiated item layout View.
     */
    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      icon = itemView.findViewById(R.id.item_icon);
      title = itemView.findViewById(R.id.item_title);
    }
  }
}

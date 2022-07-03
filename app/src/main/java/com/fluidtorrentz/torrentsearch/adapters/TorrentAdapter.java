/*
 * Copyright (c) 2020 Priyank Tejani
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.fluidtorrentz.torrentsearch.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.fluidtorrentz.torrentsearch.R;
import com.fluidtorrentz.torrentsearch.parsing.SearchResult;

import java.util.ArrayList;

public class TorrentAdapter extends RecyclerView.Adapter<TorrentAdapter.OneThreeViewHolder> {

    public boolean showShimmer, noResult = false;
    private final ArrayList<SearchResult> searchResults;
    private final RecyclerviewListener recyclerviewListener;

    public TorrentAdapter(RecyclerviewListener recyclerviewListener, ArrayList<SearchResult> oneThreeSearchResults) {
        this.recyclerviewListener = recyclerviewListener;
        this.searchResults = oneThreeSearchResults;
    }

    @NonNull
    @Override
    public OneThreeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);

        return new OneThreeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OneThreeViewHolder holder, int position) {
        if (showShimmer) {
            holder.itemLayout.setVisibility(View.GONE);
            holder.shimmerItemLayout.setVisibility(View.VISIBLE);
            holder.shimmerItemLayout.showShimmer(true);
        } else if (noResult){
            holder.shimmerItemLayout.hideShimmer();
            holder.shimmerItemLayout.setVisibility(View.GONE);
            holder.noResultLayout.setVisibility(View.VISIBLE);
        } else {
            holder.shimmerItemLayout.hideShimmer();
            holder.shimmerItemLayout.setVisibility(View.GONE);
            holder.itemLayout.setVisibility(View.VISIBLE);

            final SearchResult oneThreeSearchResult = searchResults.get(position);
            holder.textTitle.setText(oneThreeSearchResult.getTitle());
            holder.textSeeds.setText(oneThreeSearchResult.getSeeds());
            holder.textLeeches.setText(oneThreeSearchResult.getLeeches());
            holder.textSize.setText(oneThreeSearchResult.getSize());
            holder.textWebsite.setText(oneThreeSearchResult.getWebsite());
        }
    }

    @Override
    public int getItemCount() {
        int SHIMMER_ITEM_COUNT = 7;
        if (showShimmer) {
            return SHIMMER_ITEM_COUNT;
        } else if (noResult) {
            return 1 ;
        } else {
            return searchResults.size();
        }
    }

    public interface RecyclerviewListener {
        void onItemClick(int position);
    }

    public class OneThreeViewHolder extends RecyclerView.ViewHolder {
        public ShimmerFrameLayout shimmerItemLayout;
        public ConstraintLayout itemLayout , noResultLayout;
        public TextView textTitle, textSeeds, textLeeches, textSize, textWebsite;

        public OneThreeViewHolder(@NonNull View itemView) {
            super(itemView);
            shimmerItemLayout = itemView.findViewById(R.id.shimmer_item_layout);
            itemLayout = itemView.findViewById(R.id.item_layout);
            noResultLayout = itemView.findViewById(R.id.no_result_layout);
            textTitle = itemView.findViewById(R.id.text_title);
            textSeeds = itemView.findViewById(R.id.text_seeds);
            textLeeches = itemView.findViewById(R.id.text_leeches);
            textSize = itemView.findViewById(R.id.text_size);
            textWebsite = itemView.findViewById(R.id.text_website);

            itemView.setOnClickListener(v -> {
                if (!showShimmer)
                    recyclerviewListener.onItemClick(getBindingAdapterPosition());
            });
        }
    }
}

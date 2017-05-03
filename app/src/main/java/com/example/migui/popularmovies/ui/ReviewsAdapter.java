package com.example.migui.popularmovies.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.migui.popularmovies.R;
import com.example.migui.popularmovies.Review;

import java.util.List;

class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewAdapterViewHolder> {

    private List<Review> reviews;

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_movie_review;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new ReviewsAdapter.ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {
        Review review = reviews.get(position);

        holder.textViewAuthor.setText(review.getAuthor());
        holder.textViewContent.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    int setReviews(List<Review> reviews) {
        this.reviews = reviews;
        return reviews.size();
    }

    class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewAuthor;
        private TextView textViewContent;

        private ReviewAdapterViewHolder(View itemView) {
            super(itemView);

            textViewAuthor = (TextView) itemView.findViewById(R.id.text_rv_review_author);
            textViewContent = (TextView) itemView.findViewById(R.id.text_rv_review_content);
        }
    }
}

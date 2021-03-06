package com.example.migui.popularmovies.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.migui.popularmovies.sync.NetworkUtils;
import com.example.migui.popularmovies.R;
import com.example.migui.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterAdapterViewHolder> {
    private Cursor cursor;
    private Context context;
    private PosterAdapterOnClickHandler clickHandler;

    PosterAdapter(PosterAdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
        this.context = (Context) clickHandler;
    }

    @Override
    public PosterAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.rv_movie_posters;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new PosterAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PosterAdapterViewHolder holder, int position) {
        int idIndex = cursor.getColumnIndex(MovieContract.MovieEntry._ID);

        cursor.moveToPosition(position);
        int posterIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FRONT_POSTER);

        final int id = cursor.getInt(idIndex);
        holder.itemView.setTag(id);

        String posterUrl = cursor.getString(posterIndex);

        Picasso.with(context).load(NetworkUtils.IMAGE_BASE_URL + posterUrl)
                .placeholder(R.drawable.ic_unknown).error(R.drawable.ic_error)
                .into(holder.rvPosterImageView);
    }

    @Override
    public int getItemCount() {
        if (cursor == null) return 0;
        return cursor.getCount();
    }

    void swapCursor(Cursor cursor) {
        if (this.cursor != cursor) {
            this.cursor = cursor;
            if (cursor != null) {
                notifyDataSetChanged();
            }
        }
    }

    class PosterAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private ImageView rvPosterImageView;


        PosterAdapterViewHolder(View view) {
            super(view);
            rvPosterImageView = (ImageView) view.findViewById(R.id.rv_movie_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            cursor.moveToPosition(adapterPosition);

            clickHandler.onClick(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry._ID)));
        }
    }

    interface PosterAdapterOnClickHandler {
        void onClick(String id);
    }
}

package com.example.migui.popularmovies.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.migui.popularmovies.R;
import com.example.migui.popularmovies.auxiliar.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

class TrailersAdapter
        extends RecyclerView.Adapter<TrailersAdapter.TrailerAdapterViewHolder> {

    private List<Trailer> trailers;
    private TrailerAdapterOnClickListener clickHandler;
    private Context context;

    TrailersAdapter(TrailerAdapterOnClickListener clickHandler, Context context) {
        this.clickHandler = clickHandler;
        this.context = context;
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_movie_trailers;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new TrailersAdapter.TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailersAdapter.TrailerAdapterViewHolder holder, int position) {
        Trailer trailer = trailers.get(position);

        Picasso.with(context).load(trailer.getThumbnailString())
                .placeholder(R.drawable.ic_unknown).error(R.drawable.ic_error)
                .into(holder.imageViewCover);
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    int setTrailers(List<Trailer> trailers) {
        this.trailers = new ArrayList<>();
        for (Trailer trailer : trailers)
            if (trailer.getSite().equals("YouTube"))
                this.trailers.add(trailer);
        return this.trailers.size();
    }

    class TrailerAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private ImageView imageViewCover;

        private TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            imageViewCover = (ImageView) itemView.findViewById(R.id.iv_trailer_cover);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            clickHandler.onClick(trailers.get(adapterPosition).getKey());
        }
    }

    interface TrailerAdapterOnClickListener {
        void onClick(String urlYoutube);
    }
}

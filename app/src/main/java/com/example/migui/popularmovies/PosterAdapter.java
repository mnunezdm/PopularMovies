package com.example.migui.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.List;

class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.AdapterPosterViewHolder> {
    private List<Film> filmList;
    private Context context;
    private PosterAdapterOnClickHandler clickHandler;

    PosterAdapter(PosterAdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
        this.context = (Context) clickHandler;
    }

    @Override
    public AdapterPosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.rv_movie_posters;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new AdapterPosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterPosterViewHolder holder, int position) {
        Film film = filmList.get(position);
        Picasso.with(context).load(film.getImageURL())
                .placeholder(R.drawable.ic_unknown).error(R.drawable.ic_error).into(holder.rvPosterImageView);
    }

    @Override
    public int getItemCount() {
        if(filmList == null) return 0;
        return filmList.size();
    }

    void setFilmList(List<Film> filmList) {
        this.filmList = filmList;
        notifyDataSetChanged();
    }

    class AdapterPosterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        ImageView rvPosterImageView;


        AdapterPosterViewHolder(View view) {
            super(view);
            rvPosterImageView = (ImageView) view.findViewById(R.id.rv_movie_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Film filmToExpand = filmList.get(getAdapterPosition());
            clickHandler.onClick(filmToExpand);
        }
    }

    interface PosterAdapterOnClickHandler {
        void onClick(Film filmToExpand);
    }
}

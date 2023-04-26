package com.missevan.game.demo.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.missevan.game.demo.R;
import com.missevan.game.demo.model.Post;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private static final String TAG = "PostAdapter";
    private List<Post> mPosts = new ArrayList<>();
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public Post getData(int p) {
        return mPosts.get(p);
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_row, parent, false);
        return new PostViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post post = mPosts.get(position);
        holder.name.setText(post.name);
        holder.text.setText(post.message);
        holder.image.setImageResource(R.drawable.ic_broken);
        holder.itemView.setTag(position);
    }

    public void setPosts(List<Post> posts) {
        mPosts.clear();
        mPosts = null;
        mPosts = posts;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView text;

        PostViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.post_image);
            name = itemView.findViewById(R.id.post_name);
            text = itemView.findViewById(R.id.post_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClick(v, getAdapterPosition());
                    }
                }
            });
        }
    }
}

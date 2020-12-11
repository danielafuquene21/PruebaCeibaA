package co.com.ceiba.mobile.pruebadeingreso.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import co.com.ceiba.mobile.pruebadeingreso.R;
import co.com.ceiba.mobile.pruebadeingreso.dataObject.Post;
import co.com.ceiba.mobile.pruebadeingreso.dataObject.User;
import co.com.ceiba.mobile.pruebadeingreso.view.PostActivity;

public class PostAdapterList  extends RecyclerView.Adapter<PostAdapterList.PostViewHolder>{

    private ArrayList<Post> data;
    private Context contexto;

    public PostAdapterList(ArrayList<Post> data, Context applicationContext) {
        this.data = data;
        this.contexto = applicationContext;
    }

    @Override
    public PostAdapterList.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PostAdapterList.PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(PostAdapterList.PostViewHolder holder, int position) {
        final Post post = data.get(position);
        holder.title.setText(post.getTitle());
        holder.body.setText(post.getBody());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView body;
        public PostViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            body = (TextView) itemView.findViewById(R.id.body);
        }
    }
}
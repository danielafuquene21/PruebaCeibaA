package co.com.ceiba.mobile.pruebadeingreso.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import co.com.ceiba.mobile.pruebadeingreso.R;
import co.com.ceiba.mobile.pruebadeingreso.dataObject.User;
import co.com.ceiba.mobile.pruebadeingreso.rest.Const;
import co.com.ceiba.mobile.pruebadeingreso.view.MainActivity;
import co.com.ceiba.mobile.pruebadeingreso.view.PostActivity;

public class UserAdapterList extends RecyclerView.Adapter<UserAdapterList.UserViewHolder>{

    private ArrayList<User> data;
    private Context contexto;


    public UserAdapterList(ArrayList<User> data, Context applicationContext) {
        this.data = data;
        this.contexto = applicationContext;

    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        final User userViewHolder = data.get(position);
        holder.name.setText(userViewHolder.getName());
        holder.phone.setText(userViewHolder.getPhone());
        holder.email.setText(userViewHolder.getEmail());

        holder.btn_view_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(contexto, PostActivity.class);
                intent.putExtra(Const.KEY_INTENT_USERPOST, userViewHolder);
                contexto.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView phone;
        TextView email;
        Button btn_view_post;


        public UserViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            phone = (TextView) itemView.findViewById(R.id.phone);
            email = (TextView) itemView.findViewById(R.id.email);
            btn_view_post = (Button) itemView.findViewById(R.id.btn_view_post);
        }
    }

    public void filterList (ArrayList<User> filterName){
        data = filterName;
        notifyDataSetChanged();
    }

}
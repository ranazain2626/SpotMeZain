package com.munib.spotme.adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.munib.spotme.R;
import com.munib.spotme.dataModels.UserModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchSpotMeUsersAdapter extends RecyclerView.Adapter<SearchSpotMeUsersAdapter.RestaurantViewHolder> {
    private ArrayList<UserModel> dataModels;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private Context mContext;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onLongItemClick(View view, int position);
    }

    public SearchSpotMeUsersAdapter(Context context, ArrayList<UserModel> dataModels) {
        mContext = context;
        this.dataModels = dataModels;
        pref= context.getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor= pref.edit();

    }

    @Override
    public SearchSpotMeUsersAdapter.RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_search_user, parent, false);
        RestaurantViewHolder viewHolder = new RestaurantViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SearchSpotMeUsersAdapter.RestaurantViewHolder holder, int position) {
        holder.bindGateway(dataModels.get(position));
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return dataModels.size();
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder {
        TextView name,username;
        private Context mContext;
        CardView cardView;
        CircleImageView image;

        public RestaurantViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();

//            date=itemView.findViewById(R.id.date);
//            eui=itemView.findViewById(R.id.eui);
//            type=itemView.findViewById(R.id.type);
//            value=itemView.findViewById(R.id.value);

            cardView=itemView.findViewById(R.id.card);
            name=itemView.findViewById(R.id.edtName);
            username=itemView.findViewById(R.id.edtUsername);
            image=itemView.findViewById(R.id.profile_image4);
        }

        public void bindGateway(UserModel restaurant) {
            name.setText(restaurant.getName());
            username.setText("@ "+restaurant.getUsername());
            try {
                Picasso.get().load(restaurant.getImage_url()).into(image);
            }catch (Exception ex)
            {

            }
//            type.setText(restaurant.getName());
//            value.setText(restaurant.getValue());
//
//            if(restaurant.getType().equals("ALARM")) {
//                type.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_light));
//                value.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_light));
//
//            }
//            else  if(restaurant.getType().equals("NOTIFICATION")) {
//                type.setTextColor(mContext.getResources().getColor(android.R.color.holo_green_dark));
//                value.setTextColor(mContext.getResources().getColor(android.R.color.holo_green_dark));
//            }

//            cardView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    // expandableLayout.setVisibility(View.VISIBLE);
//                    expandableLayout.expand();
//                }
//            });
        }
    }
}
package com.munib.spotme.adapters;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.munib.spotme.BusinessProfileActivity;
import com.munib.spotme.MainActivity;
import com.munib.spotme.R;
import com.munib.spotme.dataModels.BusinessProfileModel;
import com.munib.spotme.dataModels.DataModel;
import com.munib.spotme.dataModels.InvestmentModel;
import com.munib.spotme.dataModels.NotificationsModel;
import com.squareup.picasso.Picasso;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.RestaurantViewHolder>{
    private ArrayList<NotificationsModel> dataModels;
    private Context mContext;

    public NotificationsAdapter(Context context, ArrayList<NotificationsModel> dataModels) {
        mContext = context;
        this.dataModels = dataModels;

    }

    @Override
    public NotificationsAdapter.RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_notifications, parent, false);
        RestaurantViewHolder viewHolder = new RestaurantViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NotificationsAdapter.RestaurantViewHolder holder, int position) {
        holder.bindGateway(dataModels.get(position));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
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
        TextView txt;
        ImageView clear_notification;
        CardView cardView;

        public RestaurantViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            txt=itemView.findViewById(R.id.txt);
            cardView=itemView.findViewById(R.id.card);
            clear_notification=itemView.findViewById(R.id.clear_notification);
        }

        public void bindGateway(NotificationsModel restaurant) {

            txt.setText(restaurant.getNotification());
            clear_notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("notifications").child(restaurant.getUser_id()).child(restaurant.getNotification_id());
                    ref.removeValue();
                    dataModels.remove(restaurant);
                    notifyDataSetChanged();
                }
            });

        }
    }
}
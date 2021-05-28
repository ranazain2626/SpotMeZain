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
import com.squareup.picasso.Picasso;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class InvestmentsListAdapter extends RecyclerView.Adapter<InvestmentsListAdapter.RestaurantViewHolder>{
    private ArrayList<InvestmentModel> dataModels;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private Context mContext;

    public InvestmentsListAdapter(Context context, ArrayList<InvestmentModel> dataModels) {
        mContext = context;
        this.dataModels = dataModels;
        pref= context.getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor= pref.edit();

    }

    @Override
    public InvestmentsListAdapter.RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_investments_made, parent, false);
        RestaurantViewHolder viewHolder = new RestaurantViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(InvestmentsListAdapter.RestaurantViewHolder holder, int position) {
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
        TextView user_name,shares_bought,total_investment;
        CardView cardView;
        CircleImageView imageView;

        public RestaurantViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();

            total_investment=itemView.findViewById(R.id.total_investment);
            user_name=itemView.findViewById(R.id.user);
            shares_bought=itemView.findViewById(R.id.shares_bought);
            imageView=itemView.findViewById(R.id.profile_image2);

            cardView=itemView.findViewById(R.id.card);
        }

        public void bindGateway(InvestmentModel restaurant) {

            total_investment.setText("$ "+restaurant.getTotal_investment());
            shares_bought.setText(restaurant.getShares()+"");

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
            ref.child(restaurant.getUser_uid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        user_name.setText("(@"+dataSnapshot.child("username").getValue()+") "+dataSnapshot.child("name").getValue());
                        //Picasso.get().load(dataSnapshot.child("")).into(imageView);
                    } else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAGZ, error.getDetails());

                }
            });


           // Picasso.get().load(restaurant.getImage_url()).into(imageView);
            //    total_investment.setText(restaurant.getBusiness_name());
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
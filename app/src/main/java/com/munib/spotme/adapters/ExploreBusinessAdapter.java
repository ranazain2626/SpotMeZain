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
import com.squareup.picasso.Picasso;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class ExploreBusinessAdapter extends RecyclerView.Adapter<ExploreBusinessAdapter.RestaurantViewHolder>{
    private ArrayList<BusinessProfileModel> dataModels;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private Context mContext;

    public ExploreBusinessAdapter(Context context, ArrayList<BusinessProfileModel> dataModels) {
        mContext = context;
        this.dataModels = dataModels;
        pref= context.getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor= pref.edit();

    }

    @Override
    public ExploreBusinessAdapter.RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_explore_business, parent, false);
        RestaurantViewHolder viewHolder = new RestaurantViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ExploreBusinessAdapter.RestaurantViewHolder holder, int position) {
        holder.bindGateway(dataModels.get(position));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a=new Intent(mContext, BusinessProfileActivity.class);
                a.putExtra("key",dataModels.get(position).getUid());
                a.putExtra("investment",holder.total_investment.getText().toString());
                ((MainActivity) mContext).startActivity(a);
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
        TextView business_name,user_name,shares,value,total_investment;
        CardView cardView;
        ImageView imageView;

        public RestaurantViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();

            business_name=itemView.findViewById(R.id.business_name);
            user_name=itemView.findViewById(R.id.user_name);
            shares=itemView.findViewById(R.id.shares);
            value=itemView.findViewById(R.id.value);
            total_investment=itemView.findViewById(R.id.total_investment);
            imageView=itemView.findViewById(R.id.profile_image);

            cardView=itemView.findViewById(R.id.card);
        }

        public void bindGateway(BusinessProfileModel restaurant) {

            business_name.setText(restaurant.getBusiness_name());
            user_name.setText(restaurant.getFirst_name()+" "+restaurant.getLast_name());
            shares.setText(restaurant.getShares()+"");
            value.setText("$ "+restaurant.getShare_value()+"");
            Picasso.get().load(restaurant.getImage_url()).into(imageView);
        //    total_investment.setText(restaurant.getBusiness_name());

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("investments");
            ref.child(restaurant.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int total=0;
                    if (dataSnapshot.exists()) {
                        for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren())
                        {
                            total+=Integer.parseInt(dataSnapshot1.child("total_investment").getValue().toString());
                        }
                        total_investment.setText("$ "+total);
                        //Picasso.get().load(dataSnapshot.child("")).into(imageView);
                    } else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAGZ, error.getDetails());

                }
            });


        }
    }
}
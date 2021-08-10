package com.munib.spotme.adapters;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.munib.spotme.SendMessageActivity;
import com.munib.spotme.dataModels.BusinessProfileModel;
import com.munib.spotme.dataModels.DataModel;
import com.munib.spotme.dataModels.InvestmentModel;
import com.munib.spotme.dataModels.Message;
import com.munib.spotme.dataModels.MessageModel;
import com.munib.spotme.dataModels.ReviewsModel;
import com.munib.spotme.dataModels.ThreadModel;
import com.squareup.picasso.Picasso;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.RestaurantViewHolder>{
    private ArrayList<ReviewsModel> dataModels;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private Context mContext;

    public ReviewAdapter(Context context, ArrayList<ReviewsModel> dataModels) {
        mContext = context;
        this.dataModels = dataModels;
        pref= context.getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor= pref.edit();

    }

    @Override
    public ReviewAdapter.RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_review, parent, false);
        RestaurantViewHolder viewHolder = new RestaurantViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.RestaurantViewHolder holder, int position) {
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
        TextView user_name,review,rating,type;
        ImageView imageView;

        public RestaurantViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();

            review=itemView.findViewById(R.id.review);
            user_name=itemView.findViewById(R.id.username);
            rating=itemView.findViewById(R.id.rating);
            type=itemView.findViewById(R.id.type);
            imageView=itemView.findViewById(R.id.profile_image2);
            }

        public void bindGateway(ReviewsModel restaurant) {

            review.setText(restaurant.getReview());
            rating.setText(restaurant.getRating()+"/5");
            type.setText(restaurant.getReview_type());

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
            ref.child(restaurant.getReview_by()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        user_name.setText(dataSnapshot.child("name").getValue().toString());
                        try {
                            Picasso.get().load(dataSnapshot.child("image_url").getValue().toString()).into(imageView);
                        }catch (Exception ex)
                        {

                        }
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
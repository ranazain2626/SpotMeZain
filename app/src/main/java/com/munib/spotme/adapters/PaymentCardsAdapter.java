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

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.munib.spotme.BusinessProfileActivity;
import com.munib.spotme.MainActivity;
import com.munib.spotme.R;
import com.munib.spotme.dataModels.BusinessProfileModel;
import com.munib.spotme.dataModels.CardModel;
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

public class PaymentCardsAdapter extends RecyclerView.Adapter<PaymentCardsAdapter.RestaurantViewHolder>{
    private ArrayList<CardModel> dataModels;
    private Context mContext;

    private SearchSpotMeUsersAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onLongItemClick(View view, int position);
    }

    public PaymentCardsAdapter(Context context, ArrayList<CardModel> dataModels) {
        mContext = context;
        this.dataModels = dataModels;

    }

    @Override
    public PaymentCardsAdapter.RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_payment_card, parent, false);
        RestaurantViewHolder viewHolder = new RestaurantViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PaymentCardsAdapter.RestaurantViewHolder holder, int position) {
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
        ImageView brand;
        TextView expiry,last4;
        CardView cardView;

        public RestaurantViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            expiry=itemView.findViewById(R.id.expiry);
            last4=itemView.findViewById(R.id.last4);
            cardView=itemView.findViewById(R.id.card);
            brand=itemView.findViewById(R.id.brand);
        }

        public void bindGateway(CardModel restaurant) {

            expiry.setText(restaurant.getExp_month()+"/"+restaurant.getExp_year());
            last4.setText(restaurant.getLast4());
            if(restaurant.getBrand().equals("visa"))
            {
                brand.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_visa));
            }else if(restaurant.getBrand().equals("mastercard"))
            {
                brand.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_mastercard));
            }
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("notifications").child(restaurant.getUser_id()).child(restaurant.getNotification_id());
//                    ref.removeValue();
//                    dataModels.remove(restaurant);
//                    notifyDataSetChanged();
                }
            });

        }
    }
}
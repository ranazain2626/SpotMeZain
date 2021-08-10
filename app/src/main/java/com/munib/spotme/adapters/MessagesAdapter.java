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
import android.widget.LinearLayout;
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
import com.munib.spotme.dataModels.ThreadModel;
import com.squareup.picasso.Picasso;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.RestaurantViewHolder>{
    private ArrayList<ThreadModel> dataModels;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private Context mContext;

    public MessagesAdapter(Context context, ArrayList<ThreadModel> dataModels) {
        mContext = context;
        this.dataModels = dataModels;
        pref= context.getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor= pref.edit();

    }

    @Override
    public MessagesAdapter.RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_messages, parent, false);
        RestaurantViewHolder viewHolder = new RestaurantViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessagesAdapter.RestaurantViewHolder holder, int position) {
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
        TextView user_name,last_text,notifcation_count;
        LinearLayout cardView;
        ImageView imageView;
        FrameLayout fm1;

        public RestaurantViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();

            last_text=itemView.findViewById(R.id.last_text);
            user_name=itemView.findViewById(R.id.username);
            notifcation_count=itemView.findViewById(R.id.notification_count);
            imageView=itemView.findViewById(R.id.profile_image4);
            fm1=itemView.findViewById(R.id.fm1);

            cardView=itemView.findViewById(R.id.card);
        }

        public void bindGateway(ThreadModel restaurant) {

            last_text.setText(restaurant.getLast_message());
            if(restaurant.getNotification_count()==0)
            {
                fm1.setVisibility(View.GONE);
            }else{
                notifcation_count.setText(restaurant.getNotification_count()+"");
            }
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
            ref.child(restaurant.getChat_with()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        user_name.setText(dataSnapshot.child("name").getValue().toString());
                        //Picasso.get().load(dataSnapshot.child("")).into(imageView);
                        try {
                            Picasso.get().load(dataSnapshot.child("image_url").getValue().toString()).into(imageView);
                        }catch (Exception ex)
                        {

                        }
                        notifyDataSetChanged();
                    } else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAGZ, error.getDetails());

                }
            });


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent a=new Intent(mContext, SendMessageActivity.class);
                    a.putExtra("uid1",((MainActivity) mContext).auth.getCurrentUser().getUid());
                    a.putExtra("uid2",restaurant.getChat_with());
                    a.putExtra("thread_id",restaurant.getThread_id());
                    a.putExtra("count",restaurant.getNotification_count());
                    ((MainActivity) mContext).startActivity(a);
                }
            });
        }
    }
}
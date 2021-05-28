package com.munib.spotme.adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.munib.spotme.R;
import com.munib.spotme.dataModels.ProposedMoneyModel;
import com.munib.spotme.dataModels.UserModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ProposedMoneyAdapter extends RecyclerView.Adapter<ProposedMoneyAdapter.RestaurantViewHolder> {
    private ArrayList<ProposedMoneyModel> dataModels;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private Context mContext;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onLongItemClick(View view, int position);
    }

    public ProposedMoneyAdapter(Context context, ArrayList<ProposedMoneyModel> dataModels) {
        mContext = context;
        this.dataModels = dataModels;
        pref= context.getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor= pref.edit();

    }

    @Override
    public ProposedMoneyAdapter.RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_proposed_money, parent, false);
        RestaurantViewHolder viewHolder = new RestaurantViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProposedMoneyAdapter.RestaurantViewHolder holder, int position) {
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
        TextView txtPrice,index,month,due_date;
        private Context mContext;
        CardView cardView;
        TextView status;

        public RestaurantViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();

            cardView=itemView.findViewById(R.id.card);
            txtPrice=itemView.findViewById(R.id.txtPrice);
            index=itemView.findViewById(R.id.index);
            month=itemView.findViewById(R.id.month);
            due_date=itemView.findViewById(R.id.due_date);
            status=itemView.findViewById(R.id.status);
        }

        public void bindGateway(ProposedMoneyModel restaurant) {
            int int_val=Integer.parseInt(((int)restaurant.getAmount())+"");
            txtPrice.setText("$ "+ int_val+".00");
            index.setText(restaurant.getIndex()+"");
            month.setText(""+restaurant.getMonth());
            if(restaurant.getStatus()==1)
            {
                status.setText("Paid");
                status.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                due_date.setVisibility(View.GONE);
            }else{
                SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss ZZZ yyyy",
                        Locale.ENGLISH);
                Date date = null;
                try {
                    date = sdf.parse(restaurant.getDue_date());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat month_date = new SimpleDateFormat("dd-MMM-yyyy");
                due_date.setText("Due Date : "+month_date.format(date));

                Calendar c1 = Calendar.getInstance();
                if(c1.getTime().after(date))
                {
                    status.setText("Late");
                    status.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
                }else {
                    status.setText("Pending");
                }
            }


        }
    }
}
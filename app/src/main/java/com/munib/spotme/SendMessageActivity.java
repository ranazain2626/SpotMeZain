package com.munib.spotme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.munib.spotme.base.BaseActivity;
import com.munib.spotme.dataModels.Author;
import com.munib.spotme.dataModels.Message;
import com.munib.spotme.dataModels.MessageModel;
import com.munib.spotme.dataModels.OffersModel;
import com.munib.spotme.dataModels.ThreadModel;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.munib.spotme.utils.CommonUtils.TAGZ;

public class SendMessageActivity extends BaseActivity {

    MessagesList messagesList;
    MessageInput messageInput;
    String uid1,uid2,thread_id;
    String thread="",selected_user_token="";
    int count=0;
    MessagesListAdapter<Message> adapter;
    TextView username,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        try {
            uid1 = getIntent().getStringExtra("uid1");
            uid2 = getIntent().getStringExtra("uid2");
            thread_id = getIntent().getStringExtra("thread_id");
        }catch (Exception ex)
        {
            uid1="1";
            uid2="1";
        }

        try {
            if(!thread_id.equals("0")) {
                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("users").child(uid1).child("conversations").child(thread_id).child("notification_count");
                ref1.setValue(0);
            }
            }catch (Exception ex)
        {

        }

        ImageView back_btn=findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        username=(TextView) findViewById(R.id.username);
        name=(TextView) findViewById(R.id.name);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.child(uid2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    username.setText("@"+dataSnapshot.child("username").getValue());
                    name.setText(dataSnapshot.child("name").getValue().toString());
                    selected_user_token=dataSnapshot.child("device_token").getValue().toString();

                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAGZ, error.getDetails());

            }
        });

        try{
            count=getIntent().getIntExtra("count",0);
        }catch (Exception ex)
        {

        }

        thread="1";
        int compare = uid1.compareTo(uid2);
        if(compare < 0){
            thread=uid1+uid2;
        }
        else if(compare > 0 ){
            thread=uid2+uid1;
        }

        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                Picasso.get().load(url).into(imageView);
            }
        };

         adapter= new MessagesListAdapter<>(uid1, imageLoader);

        messagesList=findViewById(R.id.messagesList);

        messageInput=findViewById(R.id.input);
        messageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                //validate and send message
                Date time=Calendar.getInstance().getTime();
                MessageModel model=new MessageModel(uid1,uid2,input.toString(), time.toString());
                String message_id=Calendar.getInstance().getTimeInMillis()+"";
                database.getReference("messages").child(thread).child(message_id).setValue(model);
                database.getReference("users").child(uid1).child("conversations").child(thread).setValue(new ThreadModel(uid2,input.toString(),0));
                database.getReference("users").child(uid2).child("conversations").child(thread).setValue(new ThreadModel(uid1,input.toString(),count+1));

//                Author author=new Author(uid1);
//                Message msg=new Message(message_id,author,input.toString(),time);
//                adapter.addToStart(msg, true);

                if(selected_user_token!=null)
                    sendNotification(currentUserData.getName()+" has sent you a message",selected_user_token);

                return true;
            }
        });
        messagesList.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        int compare = uid1.compareTo(uid2);
        if(compare < 0){
            thread=uid1+uid2;
        }
        else if(compare > 0 ){
            thread=uid2+uid1;
        }

        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("messages");
        ref1.child(thread).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    adapter.clear();
                    Log.d("mubi",dataSnapshot.toString());
                    ArrayList<MessageModel> arrayList=new ArrayList<>();
                    ArrayList<Message> arrayList1=new ArrayList<>();
                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                        MessageModel msg = datas.getValue(MessageModel.class);
                        msg.setThread_id(datas.getKey());
                        arrayList.add(msg);
                    }

                    for(int i=0;i<arrayList.size();i++)
                    {
                        arrayList1.add(new Message(arrayList.get(i).getThread_id(),new Author(arrayList.get(i).getSender()),arrayList.get(i).getText(),new Date(arrayList.get(i).getTime())));
                    }
                   adapter.addToEnd(arrayList1,true);

//                    EEE, d MMM yyyy HH:mm:ss Z
//                    Wed, 5 Dec 2018 10:37:43 +0000
//                    Sat Jan 09 15:31:14 GMT+05:00 2021
//                        "EEE MMM d HH:mm:ss z yyyy"

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
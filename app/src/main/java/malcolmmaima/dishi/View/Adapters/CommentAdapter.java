package malcolmmaima.dishi.View.Adapters;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import malcolmmaima.dishi.Model.StatusUpdateModel;
import malcolmmaima.dishi.R;
import malcolmmaima.dishi.View.ViewProfile;
import malcolmmaima.dishi.View.ViewStatus;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyHolder> {

    Context context;
    List<StatusUpdateModel> listdata;
    DatabaseReference myRef, postStatus;

    public CommentAdapter(Context context, List<StatusUpdateModel> listdata) {
        this.listdata = listdata;
        this.context = context;
    }

    @Override
    public CommentAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_comment,parent,false);

        CommentAdapter.MyHolder myHolder = new CommentAdapter.MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentAdapter.MyHolder holder, final int position) {

        final StatusUpdateModel statusUpdateModel = listdata.get(position);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String myPhone = user.getPhoneNumber(); //Current logged in user phone number
        myRef = FirebaseDatabase.getInstance().getReference(myPhone);
        final DatabaseReference [] dbRef = new DatabaseReference[listdata.size()];
        dbRef[position] = FirebaseDatabase.getInstance().getReference(statusUpdateModel.getCurrentWall());
        postStatus = FirebaseDatabase.getInstance().getReference();

        final String [] phone = new String[listdata.size()];

        final String profilePic[] = new String[listdata.size()];
        final String profileName[] = new String[listdata.size()];

        //Likes total counter
        postStatus.child(statusUpdateModel.getPostedTo()).child("status_updates").child("comments").child(statusUpdateModel.key).child("likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Toast.makeText(context, "Likes: " + dataSnapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                holder.likesTotal.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Fetch name
        postStatus.child(statusUpdateModel.getAuthor()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profileName[position] = dataSnapshot.getValue(String.class);
                holder.profileName.setText(profileName[position]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(!myPhone.equals(statusUpdateModel.getAuthor())){
            //Toast.makeText(context, statusUpdateModel.getStatus() + " Not my comment: " + statusUpdateModel.getCurrentWall(), Toast.LENGTH_SHORT).show();
            holder.deleteBtn.setVisibility(View.INVISIBLE);
            holder.likePost.setTag(R.drawable.ic_like);

            try {

                //Loading image from Glide library.
                dbRef[position].child("profilepic").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        profilePic[position] = dataSnapshot.getValue(String.class);
                        Glide.with(context).load(profilePic[position]).into(holder.profilePic);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.userUpdate.setText(statusUpdateModel.getStatus());

                //Likes total counter

                //On loading adapter fetch the like status
                postStatus.child(statusUpdateModel.getCurrentWall()).child("status_updates").child(statusUpdateModel.key).child("likes").child(myPhone).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        phone[position] = dataSnapshot.getValue(String.class);
                        if(phone[position] == null){
                            //Toast.makeText(context, "phoneLike: is null", Toast.LENGTH_SHORT).show();
                            holder.likePost.setTag(R.drawable.ic_like);
                            holder.likePost.setImageResource(R.drawable.ic_like);
                        }
                        else {
                            //Toast.makeText(context, "phoneLike: not null", Toast.LENGTH_SHORT).show();
                            holder.likePost.setTag(R.drawable.ic_liked);
                            holder.likePost.setImageResource(R.drawable.ic_liked);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.likePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int id = (int)holder.likePost.getTag();
                        if( id == R.drawable.ic_like){
                            //Add to my favourites
                            postStatus.child(statusUpdateModel.getCurrentWall()).child("status_updates").child(statusUpdateModel.key).child("likes").child(myPhone).setValue("like").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    holder.likePost.setTag(R.drawable.ic_liked);
                                    holder.likePost.setImageResource(R.drawable.ic_liked);
                                    //Toast.makeText(context,restaurantDetails.getName()+" added to favourites",Toast.LENGTH_SHORT).show();
                                }
                            });


                        } else{
                            //Remove from my favourites
                            postStatus.child(statusUpdateModel.getCurrentWall()).child("status_updates").child(statusUpdateModel.key).child("likes").child(myPhone).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    holder.likePost.setTag(R.drawable.ic_like);
                                    holder.likePost.setImageResource(R.drawable.ic_like);

                                    //Toast.makeText(context,restaurantDetails.getName()+" removed from favourites",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                    }
                });
            } catch (Exception e){

            }
        }
        else {
            //Toast.makeText(context, statusUpdateModel.getStatus() + " my Wall: " + statusUpdateModel.getAuthor(), Toast.LENGTH_SHORT).show();
            holder.deleteBtn.setVisibility(View.VISIBLE);
            holder.likePost.setTag(R.drawable.ic_like);

            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dbRef[position].child("status_updates").child(statusUpdateModel.getCommentKey()).child("comments").child(statusUpdateModel.key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            try {
                //Loading image using Glide library.
                myRef.child("profilepic").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        profilePic[position] = dataSnapshot.getValue(String.class);
                        Glide.with(context).load(profilePic[position]).into(holder.profilePic);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



                holder.userUpdate.setText(statusUpdateModel.getStatus());

                //On loading adapter fetch the like status
                dbRef[position].child("status_updates").child(statusUpdateModel.key).child("likes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot likes : dataSnapshot.getChildren()){
                            String phone = likes.getKey();
                            try {
                                if (phone.equals(myPhone)) {
                                    //Toast.makeText(context, "liked", Toast.LENGTH_SHORT).show();
                                    holder.likePost.setTag(R.drawable.ic_liked);
                                    holder.likePost.setImageResource(R.drawable.ic_liked);
                                } else {
                                    //Toast.makeText(context, "not liked", Toast.LENGTH_SHORT).show();
                                    holder.likePost.setTag(R.drawable.ic_like);
                                    holder.likePost.setImageResource(R.drawable.ic_like);
                                }
                            } catch (Exception e){
                                Toast.makeText(context, "error" + e, Toast.LENGTH_SHORT).show();
                                holder.likePost.setTag(R.drawable.ic_like);
                                holder.likePost.setImageResource(R.drawable.ic_like);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.likePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int id = (int)holder.likePost.getTag();
                        if( id == R.drawable.ic_like){
                            //Add to my favourites
                            dbRef[position].child("status_updates").child(statusUpdateModel.key).child("likes").child(myPhone).setValue("like").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    holder.likePost.setTag(R.drawable.ic_liked);
                                    holder.likePost.setImageResource(R.drawable.ic_liked);
                                    //Toast.makeText(context,restaurantDetails.getName()+" added to favourites",Toast.LENGTH_SHORT).show();
                                }
                            });


                        } else{
                            //Remove from my favourites
                            dbRef[position].child("status_updates").child(statusUpdateModel.key).child("likes").child(myPhone).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    holder.likePost.setTag(R.drawable.ic_like);
                                    holder.likePost.setImageResource(R.drawable.ic_like);

                                    //Toast.makeText(context,restaurantDetails.getName()+" removed from favourites",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                    }
                });

            } catch (Exception e){

            }
        }


        holder.profileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!myPhone.equals(statusUpdateModel.getAuthor())){
                    Intent slideactivity = new Intent(context, ViewProfile.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    slideactivity.putExtra("phone", statusUpdateModel.getAuthor());
                    Bundle bndlanimation =
                            ActivityOptions.makeCustomAnimation(context, R.anim.animation,R.anim.animation2).toBundle();
                    context.startActivity(slideactivity, bndlanimation);
                }

            }
        });

        /**
         *
         * holder.cardView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        if(statusUpdateModel.getAuthor() != null && profileName[position] != null && profilePic[position] != null){
        Intent slideactivity = new Intent(context, ViewStatus.class)
        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        slideactivity.putExtra("phone", statusUpdateModel.getAuthor());
        slideactivity.putExtra("username", profileName[position]);
        slideactivity.putExtra("update", statusUpdateModel.getStatus());
        slideactivity.putExtra("profilepic", profilePic[position]);
        slideactivity.putExtra("key", statusUpdateModel.key);
        Bundle bndlanimation =
        ActivityOptions.makeCustomAnimation(context, R.anim.animation,R.anim.animation2).toBundle();
        context.startActivity(slideactivity, bndlanimation);
        }
        else {
        Toast.makeText(context, "Error fetching data, try again!", Toast.LENGTH_SHORT).show();
        }

        }
        });
         */

    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        TextView profileName, userUpdate, likesTotal, commentsTotal;
        ImageView profilePic, deleteBtn, likePost, comments, sharePost;
        CardView cardView;

        public MyHolder(View itemView) {
            super(itemView);

            profileName = itemView.findViewById(R.id.profileName);
            userUpdate = itemView.findViewById(R.id.userUpdate);
            profilePic = itemView.findViewById(R.id.profilePic);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            likePost = itemView.findViewById(R.id.likePost);
            comments = itemView.findViewById(R.id.comments);
            sharePost = itemView.findViewById(R.id.sharePost);
            likesTotal = itemView.findViewById(R.id.likesTotal);
            commentsTotal = itemView.findViewById(R.id.commentsTotal);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}
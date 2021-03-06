package malcolmmaima.dishi.View.Adapters;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import malcolmmaima.dishi.Model.ProductDetails;
import malcolmmaima.dishi.R;
import malcolmmaima.dishi.View.Activities.AddMenu;
import malcolmmaima.dishi.View.Activities.ViewProfile;

public class RestaurantMenuAdapter extends RecyclerView.Adapter<RestaurantMenuAdapter.MyHolder>{

    Context context;
    List<ProductDetails> listdata;

    public RestaurantMenuAdapter(Context context, List<ProductDetails> listdata) {
        this.listdata = listdata;
        this.context = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_card,parent,false);

        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }


    public void onBindViewHolder(final MyHolder holder, int position) {
        final ProductDetails productDetails = listdata.get(position);

        final DatabaseReference menusRef;
        FirebaseDatabase db;
        StorageReference storageReference;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String myPhone = user.getPhoneNumber(); //Current logged in user phone number
        // Assign FirebaseStorage instance to storageReference.

        storageReference = FirebaseStorage.getInstance().getReference();

        final StorageReference storageReference2nd = storageReference.child(productDetails.getStorageLocation());

        db = FirebaseDatabase.getInstance();
        menusRef = db.getReference(myPhone + "/mymenu"); //Under the user's node, place their menu items

        holder.foodPrice.setText("Ksh "+productDetails.getPrice());
        holder.foodName.setText(productDetails.getName());
        holder.foodDescription.setText(productDetails.getDescription());

        try {
            //Loading image from Glide library.
            Glide.with(context).load(productDetails.getImageURL()).into(holder.foodPic);
            Log.d("glide", "onBindViewHolder: imageUrl: " + productDetails.getImageURL());
        } catch (Exception e){

        }

        holder.editBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(final View view){
                if(productDetails.getKey() != null){
                    Intent slideactivity = new Intent(context, AddMenu.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    slideactivity.putExtra("phone", myPhone);
                    slideactivity.putExtra("key", productDetails.getKey());
                    Bundle bndlanimation =
                            ActivityOptions.makeCustomAnimation(context, R.anim.animation,R.anim.animation2).toBundle();
                    context.startActivity(slideactivity, bndlanimation);
                }
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(final View view){

                final AlertDialog myQuittingDialogBox = new AlertDialog.Builder(view.getContext())
                        //set message, title, and icon
                        .setTitle("Delete item")
                        .setMessage("Are you sure you want to delete "+ productDetails.getName() + "?")
                        //.setIcon(R.drawable.icon) will replace icon with name of existing icon from project
                        //set three option buttons
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                /*
                                progressDialog = new ProgressDialog(context);
                                progressDialog.setCancelable(false);
                                progressDialog.setTitle("Processing...");
                                progressDialog.setMessage("Please wait...");
                                progressDialog.show();
                                */
                                menusRef.child(productDetails.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        storageReference2nd.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // File deleted successfully
                                                Snackbar snackbar = Snackbar
                                                        .make(view, "Deleted!", Snackbar.LENGTH_LONG);

                                                snackbar.show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Uh-oh, an error occurred!
                                                Toast.makeText(context, "error: " + exception, Toast.LENGTH_SHORT)
                                                        .show();

                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Uh-oh, an error occurred!
                                        Toast.makeText(context, "error", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                });
                            }
                        })//setPositiveButton


                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //Do not delete
                                //Toast.makeText(context, "No", Toast.LENGTH_SHORT).show();

                            }
                        })//setNegativeButton

                        .create();
                myQuittingDialogBox.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }


    class MyHolder extends RecyclerView.ViewHolder{
        TextView foodPrice , foodDescription, foodName;
        ImageView foodPic;
        ImageButton deleteBtn, editBtn;

        public MyHolder(View itemView) {
            super(itemView);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            foodName = itemView.findViewById(R.id.foodName);
            foodDescription = itemView.findViewById(R.id.foodDescription);
            foodPic = itemView.findViewById(R.id.foodPic);
            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);

        }
    }


}
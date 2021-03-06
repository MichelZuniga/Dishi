package malcolmmaima.dishi.View.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rey.material.widget.Switch;

import java.io.IOException;

import malcolmmaima.dishi.R;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FirebaseAuth mAuth;
    private ImageView profile_pic;

    EditText userName, userBio, userEmail;
    RadioButton maleRd, femaleRd;
    RadioGroup gender;
    Spinner accType;
    Switch notifications;
    String myPhone;
    String account_type;
    String temp, picUrl, account;

    String [] profilePicActions = {"View Profile Picture","Change Profile Picture"};

    // Folder path for Firebase Storage.
    String Storage_Path = "Users/";

    // Creating URI.
    Uri FilePathUri;

    // Creating StorageReference and DatabaseReference object.
    StorageReference storageReference;
    DatabaseReference databaseReference;

    // Image request code for onActivityResult() .
    int Image_Request_Code = 7;

    ProgressDialog progressDialog ;
    Boolean saved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getInstance().getCurrentUser() == null){

            //User is not signed in, send them back to verification page
            Toast.makeText(this, "Not logged in!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(SettingsActivity.this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));//Load Main Activity and clear activity stack
        }

        progressDialog = new ProgressDialog(SettingsActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar topToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("Settings");

        //Back button on toolbar
        topToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); //Go back to previous activity
            }
        });
        //topToolBar.setLogo(R.drawable.logo);
        //topToolBar.setLogoDescription(getResources().getString(R.string.logo_desc));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        myPhone = user.getPhoneNumber(); //Current logged in user phone number

        // Assign FirebaseStorage instance to storageReference.
        storageReference = FirebaseStorage.getInstance().getReference();

        // Assign FirebaseDatabase instance with root database name.
        databaseReference = FirebaseDatabase.getInstance().getReference(myPhone);

        //Our EditText Boxes
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.emailAddress);
        userBio = findViewById(R.id.userBio);

        //Our RadioButtons
        maleRd = findViewById(R.id.maleRd);
        femaleRd = findViewById(R.id.femaleRd);
        gender = findViewById(R.id.gender);

        userName.setFocusable(false);
        userName.setFocusableInTouchMode(false);
        userName.setClickable(false);

        //Fetch account details
        databaseReference.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    String profilename = dataSnapshot.getValue(String.class);
                    userName.setHint(profilename);
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                } catch (Exception e){
                    Toast.makeText(SettingsActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                    userName.setHint("Error");
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SettingsActivity.this, "Error: " + databaseError, Toast.LENGTH_SHORT).show();
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });

        databaseReference.child("bio").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    String bio = dataSnapshot.getValue(String.class);
                    userBio.setHint(bio);
                    //Toast.makeText(getContext(), "Name: " + profilename, Toast.LENGTH_SHORT).show();
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                } catch (Exception e){
                    Toast.makeText(SettingsActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                    userBio.setHint("Error");
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SettingsActivity.this, "Error: " + databaseError, Toast.LENGTH_SHORT).show();
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });

        databaseReference.child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    String email = dataSnapshot.getValue(String.class);
                    userEmail.setHint(email);
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                } catch (Exception e){
                    Toast.makeText(SettingsActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                    userEmail.setHint("Error");
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SettingsActivity.this, "Error: " + databaseError, Toast.LENGTH_SHORT).show();
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });

        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName.setFocusable(true);
                userName.setFocusableInTouchMode(true);
                userName.setClickable(true);
                userName.requestFocus();
            }
        });

        userEmail.setFocusable(false);
        userEmail.setFocusableInTouchMode(false);
        userEmail.setClickable(false);

        userEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEmail.setFocusable(true);
                userEmail.setFocusableInTouchMode(true);
                userEmail.setClickable(true);
                userEmail.requestFocus();
            }
        });

        userBio.setFocusable(false);
        userBio.setFocusableInTouchMode(false);
        userBio.setClickable(false);

        userBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userBio.setFocusable(true);
                userBio.setFocusableInTouchMode(true);
                userBio.setClickable(true);
                userBio.requestFocus();
            }
        });

        //Profile pic
        profile_pic = findViewById(R.id.profilePic);

        //Our RadioButtons
        maleRd = findViewById(R.id.maleRd);
        femaleRd = findViewById(R.id.femaleRd);
        gender = findViewById(R.id.gender);

        //Our Account Type Spinner
        accType = findViewById(R.id.accType);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.accounts, android.R.layout.simple_spinner_item);


        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        accType.setAdapter(adapter);
        accType.setOnItemSelectedListener(SettingsActivity.this);
        accType.setSelection(0);

        databaseReference.child("account_type").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }

                    account = dataSnapshot.getValue(String.class);
                    if(account.equals("1")){
                        account_type = "1";
                        accType.setSelection(1);
                        temp = "1";
                    }
                    if(account.equals("2")){
                        account_type = "2";
                        accType.setSelection(2);
                        temp = "2";
                    }
                    if(account.equals("3")){
                        account_type = "3";
                        accType.setSelection(3);
                        temp = "3";
                    }
                    if(account.equals("4")){
                        account_type = "4";
                        accType.setSelection(4);
                        temp = "4";
                    }
                } catch (Exception e){
                    accType.setSelection(0);
                    Toast.makeText(SettingsActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                accType.setSelection(0);
                Toast.makeText(SettingsActivity.this, "Error: " + databaseError, Toast.LENGTH_SHORT).show();
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });

        databaseReference.child("gender").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }

                    String gender = dataSnapshot.getValue(String.class);
                    if(gender.equals("Male")){
                        maleRd.setChecked(true);
                    }
                    if(gender.equals("Female")){
                        femaleRd.setChecked(true);
                    }
                } catch (Exception e){
                    Toast.makeText(SettingsActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(SettingsActivity.this, "Error: " + databaseError, Toast.LENGTH_SHORT).show();
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });


        //Our Notifications Switch
        notifications = findViewById(R.id.Notifications);
        databaseReference.child("notifications").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    String notif = dataSnapshot.getValue(String.class);
                    if(notif.equals("true")){
                        notifications.setChecked(true);
                    }

                    if(notif.equals("false")){
                        notifications.setChecked(false);
                    }
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                } catch (Exception e){
                    notifications.setChecked(false);
                    Toast.makeText(SettingsActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SettingsActivity.this, "Error: " + databaseError, Toast.LENGTH_SHORT).show();
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });

        //Works like a charm, in future remember to change, use dishiUer POJO and fetch all user data at once
        databaseReference.child("profilepic").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String ppic = dataSnapshot.getValue(String.class);

                    picUrl = ppic;
                    //Loading image from Glide library.
                    Glide.with(SettingsActivity.this).load(ppic).into(profile_pic);
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                } catch (Exception e){
                    //Error
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                        try {
                            Toast.makeText(SettingsActivity.this, "failed to load profile picture. try again", Toast.LENGTH_SHORT).show();
                        } catch (Exception ee){

                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                Toast.makeText(SettingsActivity.this, "Database Error. Load failed!", Toast.LENGTH_SHORT).show();
                try {
                    Glide.with(SettingsActivity.this).load(R.drawable.default_profile).into(profile_pic);
                } catch (Exception e){

                }
            }
        });

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setItems(profilePicActions, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){
                                    Intent imgActivity = new Intent(SettingsActivity.this, ViewPhoto.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    imgActivity.putExtra("link", picUrl);

                                    startActivity(imgActivity);
                                }
                                if(which == 1){
                                    // Creating intent.
                                    Intent intent = new Intent();
                                    // Setting intent type as image to select image from phone storage.
                                    intent.setType("image/*");
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);
                                }
                            }
                        });
                builder.create();
                builder.show();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            try {

                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);

                // Setting up bitmap selected image into ImageView.
                profile_pic.setImageBitmap(bitmap);

            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    public void saveData() {
        saved = false;
        progressDialog.setTitle("Saving...");
        progressDialog.show();

            //Check if user has selected new profile pic
            if (FilePathUri != null) {

                //user has selected new pic

                final StorageReference storageReference2nd = storageReference.child(Storage_Path + "/" + myPhone + "/" + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));

                // Adding addOnSuccessListener to second StorageReference.
                storageReference2nd.putFile(FilePathUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //Get image URL: //Here we get the image url from the firebase storage
                                storageReference2nd.getDownloadUrl().addOnSuccessListener(new OnSuccessListener() {

                                    @Override
                                    public void onSuccess(Object o) {
                                        databaseReference.child("profilepic").setValue(o.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                if (progressDialog.isShowing()) {
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }

                                    }
                                });
                            }
                        });
            }

        //save username
        if (!userName.getText().toString().equals("")) {
            databaseReference.child("name").setValue(userName.getText().toString());
        }

        //save user bio
        if (!userBio.getText().toString().equals("")) {
            databaseReference.child("bio").setValue(userBio.getText().toString());
        }

        //save email
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        //Check if email uses the appropriate email pattern
        if (!userEmail.getText().toString().equals("") || userEmail.equals(emailPattern)) {
            databaseReference.child("email").setValue(userEmail.getText().toString());
        }

        //save gender
        if (gender.getCheckedRadioButtonId() != -1) {

            int userGender = gender.getCheckedRadioButtonId();
            // find the radio button by returned id
            RadioButton radioButton = findViewById(userGender);

            String gender = radioButton.getText().toString();
            databaseReference.child("gender").setValue(gender);
        }

        Boolean switchState = notifications.isChecked();
        databaseReference.child("notifications").setValue(switchState.toString());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                saved = true;
                try {
                    progressDialog.dismiss();
                } catch (Exception e){

                }

                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.parentlayout), "Saved", Snackbar.LENGTH_LONG);

                snackbar.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                saved = false;
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.parentlayout), "Failed", Snackbar.LENGTH_LONG);

                snackbar.show();
            }
        });
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_save){
            try {
                saveData();
            }
            catch (Exception e){
                Toast.makeText(this, "Error " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        try {
            if (!account_type.equals(Integer.toString(position))) {
                //User has selected new account type. Prompt!
                account_type = Integer.toString(position);


                AlertDialog changeAcc = new AlertDialog.Builder(SettingsActivity.this)
                        //set message, title, and icon
                        .setTitle("Account change")
                        .setMessage("Are you sure you want to change your account type?")
                        //.setIcon(R.drawable.icon) will replace icon with name of existing icon from project
                        //set three option buttons
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                if(account.equals("1")){
                                    databaseReference.child("pending").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            if(dataSnapshot.exists()){
                                                AlertDialog activeOrder = new AlertDialog.Builder(SettingsActivity.this)
                                                        //set message, title, and icon
                                                        //.setTitle("Account change")
                                                        .setCancelable(false)
                                                        .setMessage("You cannot change your account type while there's an active order!")
                                                        //.setIcon(R.drawable.icon) will replace icon with name of existing icon from project
                                                        //set three option buttons
                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                                //Do nothing...
                                                            }
                                                        }).create();
                                                activeOrder.show();
                                            } else {
                                                if (!account_type.equals("0")) {
                                                    databaseReference.child("account_type").setValue(account_type).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            //Toast.makeText(SettingsActivity.this, "You will be redirected to your new account type on re-loading app", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                                if(account.equals("2")){
                                    databaseReference.child("orders").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            if(dataSnapshot.exists()){
                                                AlertDialog activeOrder = new AlertDialog.Builder(SettingsActivity.this)
                                                        //set message, title, and icon
                                                        //.setTitle("Account change")
                                                        .setCancelable(false)
                                                        .setMessage("You cannot change your account type while there's an active order!")
                                                        //.setIcon(R.drawable.icon) will replace icon with name of existing icon from project
                                                        //set three option buttons
                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                                //Do nothing...
                                                            }
                                                        }).create();
                                                activeOrder.show();
                                            } else {
                                                //Now check if there are deliveries active
                                                databaseReference.child("deliveries").addListenerForSingleValueEvent(new ValueEventListener() {

                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if(dataSnapshot.exists()){
                                                            AlertDialog activeOrder = new AlertDialog.Builder(SettingsActivity.this)
                                                                    //set message, title, and icon
                                                                    //.setTitle("Account change")
                                                                    .setCancelable(false)
                                                                    .setMessage("You cannot change your account type while there's an active order!")
                                                                    //.setIcon(R.drawable.icon) will replace icon with name of existing icon from project
                                                                    //set three option buttons
                                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int whichButton) {
                                                                            //Do nothing...
                                                                        }
                                                                    }).create();
                                                            activeOrder.show();
                                                        } else {
                                                            //no deliveries or pending
                                                            //save account type
                                                            if (!account_type.equals("0")) {
                                                                databaseReference.child("account_type").setValue(account_type).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        //Toast.makeText(SettingsActivity.this, "You will be redirected to your new account type on re-loading app", Toast.LENGTH_LONG).show();
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }

                                                });

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                                if(account.equals("3")){

                                    databaseReference.child("nearby_nduthis").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            if(dataSnapshot.exists()){
                                                AlertDialog activeOrder = new AlertDialog.Builder(SettingsActivity.this)
                                                        //set message, title, and icon
                                                        //.setTitle("Account change")
                                                        .setCancelable(false)
                                                        .setMessage("You cannot change your account type while there's an active order!")
                                                        //.setIcon(R.drawable.icon) will replace icon with name of existing icon from project
                                                        //set three option buttons
                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                                //Do nothing...
                                                            }
                                                        }).create();
                                                activeOrder.show();
                                            } else {
                                                //save account type
                                                if (!account_type.equals("0")) {
                                                    databaseReference.child("account_type").setValue(account_type).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            //Toast.makeText(SettingsActivity.this, "You will be redirected to your new account type on re-loading app", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }

                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                account_type = temp; //Revert to original account type

                            }
                        })//setNegativeButton

                        .create();
                changeAcc.show();
            }
        } catch (Exception e){

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}


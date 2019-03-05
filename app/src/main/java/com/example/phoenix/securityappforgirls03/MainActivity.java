package com.example.phoenix.securityappforgirls03;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    static ProgressDialog progressDialog;
    Button b_contact_picker;
    static TextView t_contact_show;
    static ImageView I_fingeprintButton;
    static TextView text_location;
    static int contact_flag=0;
    public static final String TAG = "PiyushTag";
    static Snackbar snackBar;
    static SmsManager smsManager;
    static AppDataBase appDataBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG,"MainActivity OnCreate");
        appDataBase = Room.databaseBuilder(getApplicationContext(),AppDataBase.class,"contact_db").build();
        new saveContact(17,"").execute();
        text_location = (TextView)findViewById(R.id.text_location);
        I_fingeprintButton = (ImageView)findViewById(R.id.fingerPrintDemo);
        t_contact_show = (TextView)findViewById(R.id.textView_contact);
        t_contact_show.setClickable(false);
        smsManager = SmsManager.getDefault();
        snackBar = Snackbar.make(findViewById(R.id.layout_main_relative),"Contact not Assigned..",Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction("Assign", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(i1,11);
                t_contact_show.setClickable(true);
            }
        });
        //b_contact_picker = (Button)findViewById(R.id.button_contact_picker);
//        b_contact_picker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i(TAG,"button Clicked..");
//                Intent i1 = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
//                startActivityForResult(i1,11);
//            }
//        });
        View.OnClickListener tempListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contact_flag==1){
                    Log.i(TAG,"Fteching Location START....");
                    new FetchLocation(MainActivity.this,12).execute();
                    progressDialog.show();
                }else{

                    snackBar.show();
                    //Toast.makeText(MainActivity.this,"Set Contact First...",Toast.LENGTH_SHORT).show();
                }
            }
        };
        t_contact_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(i1,11);
            }
        });

        I_fingeprintButton.setOnClickListener(tempListener);

//        I_fingeprintButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                if(contact_flag==1){
////                    Log.i(TAG,"Fteching Location START....");
////                    new FetchLocation(MainActivity.this,12).execute();
////                    progressDialog.show();
////                }else{
////                    snackBar = Snackbar.make(findViewById(R.id.layout_main_relative),"Contact not Assigned..",Snackbar.LENGTH_INDEFINITE);
////                    snackBar.setAction("Assign", new View.OnClickListener() {
////                        @Override
////                        public void onClick(View v) {
////                            Intent i1 = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
////                            startActivityForResult(i1,11);
////                        }
////                    });
////                    snackBar.show();
////                    //Toast.makeText(MainActivity.this,"Set Contact First...",Toast.LENGTH_SHORT).show();
////                }
//            }
//        });
        progressDialog = new ProgressDialog(MainActivity.this, AlertDialog.THEME_HOLO_DARK);
        progressDialog.setMessage("GettingLocation..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.create();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.i(TAG,"onActivityResult start");
        switch(requestCode){
            case 11:
                Log.i(TAG,"11");
                if(data==null){
                    Log.i(TAG,"data set EMPTY..");
                }
                Uri uri = data.getData();
                Log.i(TAG,"getData()");
                Cursor cursor = getContentResolver().query(uri,null,null,null,null);
                Log.i(TAG,"getContentResolver()");
                cursor.moveToFirst();
                Log.i(TAG,"moveToFirst");
                String s = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Log.i(TAG,s);
                //int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                contact_flag = 1;
                if(snackBar.isShown()){
                    snackBar.dismiss();
                }
                t_contact_show.setText(s);
                new saveContact(16,s).execute();
                Log.i(TAG,"saveContact execute");
                //List<Contact> tempCon =appDataBase.contactDao().getAllWord().get(0);
//                for(Contact c: tempCon){
//                    Log.i(TAG," COntact : "+c.getId()+","+c.getContact());
//                }
                //Log.i(TAG,cursor.getString(column));
                break;
        }

    }

    public void got_loc_Toast(Location location){
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
            text_location.setText("Location : "+location.getLatitude()+","+location.getLongitude());
            Log.i(TAG, "Location 01: " + location.getLatitude() + "," + location.getLongitude());
            send_message(location);
        }
        //Toast.makeText(MainActivity.this,"Lat: "+location.getLatitude()+",Long: "+location.getLongitude(),Toast.LENGTH_SHORT).show();
    }

    public void send_message(Location location){
        Log.i(TAG,"Sending Location");
        smsManager.sendTextMessage(t_contact_show.getText().toString(),null,"https://www.google.com/maps/search/?api=1&query="+location.getLatitude()+","+location.getLongitude(),null,null);
    }

    class saveContact extends AsyncTask<Void,Void,Void>{
        int request_code;
        String contact;

        public saveContact(int request_code,String contact){
            this.contact = contact;
            this.request_code = request_code;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            switch(request_code){
                case 16:
                    Contact contact1 = new Contact();
                    contact1.setContact(contact);
                    if(appDataBase.contactDao().getAllWord().size()==0){
                        appDataBase.contactDao().insert(contact1);
                        Log.i(TAG,"Data Inserted...");
                        for(Contact c : appDataBase.contactDao().getAllWord()){
                            Log.i(TAG,c.getId()+","+c.getContact());
                        }
                    }else{
                        Contact temp = appDataBase.contactDao().getAllWord().get(0);
                        int tempID = temp.getId();
                        temp.setContact(contact1.getContact());
                        appDataBase.contactDao().update(temp);
                        Log.i(TAG,"Contact Updated....");
                        for(Contact c : appDataBase.contactDao().getAllWord()){
                            Log.i(TAG,c.getId()+","+c.getContact());
                        }
                    }
                    break;

                case 17:
                    if(appDataBase.contactDao().getAllWord().size()==0){
                        //do nothing
                    }else{
                        String temp01text = appDataBase.contactDao().getAllWord().get(0).getContact();
                        t_contact_show.setText(temp01text);
                        contact_flag=1;
                    }
                    break;
            }

            return null;
        }
    }
}

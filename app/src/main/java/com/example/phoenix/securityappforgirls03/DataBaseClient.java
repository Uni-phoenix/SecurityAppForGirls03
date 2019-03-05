package com.example.phoenix.securityappforgirls03;

import android.arch.persistence.room.Room;
import android.content.Context;

public class DataBaseClient {
    private Context context;
    private static DataBaseClient dataBaseClient;

    private AppDataBase appDataBase;
    private DataBaseClient(Context context){
        this.context = context;
        appDataBase = Room.databaseBuilder(context,AppDataBase.class,"conatact_db").build();
    }
    public static synchronized DataBaseClient getInstance(Context context){
        if(dataBaseClient==null){
            dataBaseClient = new DataBaseClient(context);
        }
        return dataBaseClient;
    }
    public AppDataBase getAppDataBase() {
        return appDataBase;
    }

}

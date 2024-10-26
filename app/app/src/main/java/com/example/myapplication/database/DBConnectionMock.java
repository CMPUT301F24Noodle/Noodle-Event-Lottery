package com.example.myapplication.database;

import android.content.Context;

import java.util.UUID;

/**
 * Author: Erin-Marie
 * just makes a database connection for testing that does not require context that contains the UUID
 */
public class DBConnectionMock extends DBConnection{

    /**
     * make an instance of DBconnection that doesn't need context, can enter the UUID manually for now
     *
     */
    public DBConnectionMock() {
        super(null);
    }

    @Override protected String genUUID(Context context) {
        String uuid = "uuidtest1"; //this is what i made a test collection in the db for
        return uuid;
    }

}

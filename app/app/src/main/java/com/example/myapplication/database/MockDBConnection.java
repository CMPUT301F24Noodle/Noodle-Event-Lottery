package com.example.myapplication.database;

import android.content.Context;

import java.util.UUID;

/**
 * Author: Erin-Marie
 * just makes a database connection for testing that does not require context that contains the UUID
 */
public class MockDBConnection extends DBConnection{

    String uuid = "";
    /**
     * make an instance of DBconnection that doesn't need context, can enter the UUID manually for now
     *
     */
    public MockDBConnection() {
        super(null);
        this.uuid = generateUUID();


    }

    public MockDBConnection(String uuid){
        super(null);
        this.uuid = uuid;
    }

    /**
     * Gets the UUID of the device, to identify the user.
     * Since this is a mock class,
     *
     * @param context: the context of the application
     */
    @Override protected String genUUID(Context context) {
        if (uuid.isBlank()){
            this.uuid = "test" + UUID.randomUUID().toString();
        }

        return uuid;
    }

    /**
     * Generates a random the UUID to identify the user for a test
     * Since this is a mock class,
     *
     */
    public String generateUUID() {
        this.uuid = "test" + UUID.randomUUID().toString();
        return uuid;
    }



}

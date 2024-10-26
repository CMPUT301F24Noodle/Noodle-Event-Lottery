package com.example.myapplication;

import android.content.Context;


import com.example.myapplication.database.DBConnection;
import com.example.myapplication.database.DBConnectionMock;
import com.example.myapplication.database.UserDB;
import com.example.myapplication.objects.userProfileClasses.UserProfile;



import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;




public class UserDBTest {

    private static final String TAG = "UserDBTest";

    Context context = mock(Context.class);
    DBConnection connection;
    UserDB Userdb;

    @Before
    public void setUpDB(){
        //get a DBConnection that does not need context
        this.connection = new DBConnection(context);
        this.Userdb = new UserDB(connection);
    }

    public UserProfile makeProfile(){
        assertNotNull(this.connection);
        String uuid = this.connection.getUUID();
        //make a test UserProfile object with the uuid
        UserProfile testProfile = new UserProfile(uuid);
        //make a UserDB instance
        return testProfile;
    }

    @Test
    public void testAddUser(){
        UserProfile testProfile = makeProfile();
        //try adding the user to the db
        this.Userdb.addUser(testProfile);
        //look at the db to see if it was added
    }

}

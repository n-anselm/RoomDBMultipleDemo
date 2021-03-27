package com.anselmdevelopment.shoppinglist2.db;

import android.content.Context;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Category.class, Items.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    // Create a singleton instance of the database
    public static AppDatabase INSTANCE;

    public abstract ShoppingListDao shoppingListDao();

    // Static function which will return the database instance
    public static AppDatabase getDBinstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "AppDB")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
}

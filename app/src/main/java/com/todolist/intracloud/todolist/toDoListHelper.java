package com.todolist.intracloud.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rafafernandezdominguez on 26/1/17.
 */

public class toDoListHelper extends SQLiteOpenHelper {

    // database version
    private static final int database_VERSION = 1;

    // database name
    private static final String database_NAME = "toDoListDataBase";

    public toDoListHelper(Context context) {
        super(context, database_NAME, null, database_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODOLIST =
                "CREATE TABLE todolist ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "title TEXT," +
                        "description TEXT," +
                        "level INTEGER," +
                        "done INTEGER)";

        db.execSQL(CREATE_TODOLIST);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // De moment no fem res

    }
}

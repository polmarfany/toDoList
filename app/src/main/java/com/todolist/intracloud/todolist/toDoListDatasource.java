package com.todolist.intracloud.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by rafafernandezdominguez on 26/1/17.
 */

public class toDoListDatasource {

    public static final String table_TODOLIST = "todolist";
    public static final String TODOLIST_ID = "_id";
    public static final String TODOLIST_TITLE = "title";
    public static final String TODOLIST_DESCRIPCION = "description";
    public static final String TODOLIST_LEVEL = "level";
    public static final String TODOLIST_DONE = "done";

    private toDoListHelper dbHelper;
    private SQLiteDatabase dbW, dbR;

    // CONSTRUCTOR
    public toDoListDatasource(Context ctx) {
        // En el constructor directament obro la comunicació amb la base de dades
        dbHelper = new toDoListHelper(ctx);

        // amés també construeixo dos databases un per llegir i l'altre per alterar
        open();
    }

    private void open() {
        dbW = dbHelper.getWritableDatabase();
        dbR = dbHelper.getReadableDatabase();
    }

    // DESTRUCTOR
    protected void finalize () {
        // Cerramos los databases
        dbW.close();
        dbR.close();
    }



    // ******************
    // Funcions que retornen cursors de todoList
    // ******************
    public Cursor toDoList() {
        // Retorem totes les tasques
        return dbR.query(table_TODOLIST, new String[]{TODOLIST_ID,TODOLIST_TITLE,TODOLIST_DESCRIPCION,TODOLIST_LEVEL,TODOLIST_DONE},
                null, null,
                null, null, TODOLIST_ID);
    }

    public Cursor toDoListPending() {
        // Retornem les tasques que el camp DONE = 0
        return dbR.query(table_TODOLIST, new String[]{TODOLIST_ID,TODOLIST_TITLE,TODOLIST_DESCRIPCION,TODOLIST_LEVEL,TODOLIST_DONE},
                TODOLIST_DONE + "=?", new String[]{String.valueOf(0)},
                null, null, TODOLIST_ID);
    }

    public Cursor toDoListCompleted() {
        // Retornem les tasques que el camp DONE = 1
        return dbR.query(table_TODOLIST, new String[]{TODOLIST_ID,TODOLIST_TITLE,TODOLIST_DESCRIPCION,TODOLIST_LEVEL,TODOLIST_DONE},
                TODOLIST_DONE + "=?", new String[]{String.valueOf(1)},
                null, null, TODOLIST_ID);
    }

    public Cursor task(long id) {
        // Retorna un cursor només amb el id indicat
        // Retornem les tasques que el camp DONE = 1
        return dbR.query(table_TODOLIST, new String[]{TODOLIST_ID,TODOLIST_TITLE,TODOLIST_DESCRIPCION,TODOLIST_LEVEL,TODOLIST_DONE},
                TODOLIST_ID+ "=?", new String[]{String.valueOf(id)},
                null, null, null);

    }

    // ******************
    // Funciones de manipualación de datos
    // ******************

    public long taskAdd(String title, String description, int level) {
        // Creem una nova tasca i retornem el id crear per si el necessiten
        ContentValues values = new ContentValues();
        values.put(TODOLIST_TITLE, title);
        values.put(TODOLIST_DESCRIPCION, description);
        values.put(TODOLIST_LEVEL,level);
        values.put(TODOLIST_DONE,0);  // Forcem 0 pq si s'està creant la tasca no pot estar finalitzada

        return dbW.insert(table_TODOLIST,null,values);
    }

    public void taskUpdate(long id, String title, String description, int level) {
         // Modifiquem els valors de las tasca amb clau primària "id"
        ContentValues values = new ContentValues();
        values.put(TODOLIST_TITLE, title);
        values.put(TODOLIST_DESCRIPCION, description);
        values.put(TODOLIST_LEVEL,level);
        values.put(TODOLIST_DONE,0);  // Forcem 0 pq si s'està creant la tasca no pot estar finalitzada

        dbW.update(table_TODOLIST,values, TODOLIST_ID + " = ?", new String[] { String.valueOf(id) });
    }

    public void taskDelete(long id) {
        // Eliminem la task amb clau primària "id"
        dbW.delete(table_TODOLIST,TODOLIST_ID + " = ?", new String[] { String.valueOf(id) });
    }

    public void taskPending(long id) {
        // Modifiquem al estat de pendent la task indicada
        ContentValues values = new ContentValues();
        values.put(TODOLIST_DONE,0);

        dbW.update(table_TODOLIST,values, TODOLIST_ID + " = ?", new String[] { String.valueOf(id) });
    }

    public void taskCompleted(long id) {
        // Modifiquem al estat de pendent la task indicada
        ContentValues values = new ContentValues();
        values.put(TODOLIST_DONE,1);

        dbW.update(table_TODOLIST,values, TODOLIST_ID + " = ?", new String[] { String.valueOf(id) });
    }



}



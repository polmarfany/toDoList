package com.todolist.intracloud.todolist;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class toDoListSimpleActivity extends ListActivity {

    private static int ACTIVITY_TASK_ADD = 1;
    private static int ACTIVITY_TASK_UPDATE = 2;

    private toDoListDatasource bd;
    private long idActual;
    private int positionActual;
    private SimpleCursorAdapter scTasks;

    private static String[] from = new String[]{
            "title",
            toDoListDatasource.TODOLIST_DESCRIPCION,
            toDoListDatasource.TODOLIST_LEVEL,
            toDoListDatasource.TODOLIST_DONE};

    private static int[] to = new int[]{
            R.id.lblTitulo,
            R.id.lblDescription,
            R.id.lblPriority,
            R.id.lblState};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolistsimple);
        setTitle("Activiry list simple");

        // Butó d'afegir
        Button btn = (Button) findViewById(R.id.btnAdd);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        bd = new toDoListDatasource(this);
        loadTasks();
    }

    private void loadTasks() {

        // Demanem totes les tasques
        Cursor cursorTasks = bd.toDoList();

        // Now create a simple cursor adapter and set it to display
        scTasks = new SimpleCursorAdapter(this,
                R.layout.row_todolistsimple,
                cursorTasks,
                from,
                to,
                1);

        setListAdapter(scTasks);
    }

    private void refreshTasks() {

        // Demanem totes les tasques
        Cursor cursorTasks = bd.toDoList();

        // Now create a simple cursor adapter and set it to display
        scTasks.changeCursor(cursorTasks);
        scTasks.notifyDataSetChanged();
    }


    private void addTask() {
        // Cridem a l'activity del detall de la tasca enviant com a id -1
        Bundle bundle = new Bundle();
        bundle.putLong("id",-1);

        idActual = -1;

        Intent i = new Intent(this, taskActivity.class );
        i.putExtras(bundle);
        startActivityForResult(i,ACTIVITY_TASK_ADD);
    }

    private void updateTask(long id) {
        // Cridem a l'activity del detall de la tasca enviant com a id -1
        Bundle bundle = new Bundle();
        bundle.putLong("id",id);

        idActual = id;


        Intent i = new Intent(this, taskActivity.class );
        i.putExtras(bundle);
        startActivityForResult(i,ACTIVITY_TASK_UPDATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_TASK_ADD) {
            if (resultCode == RESULT_OK) {
                // Carreguem totes les tasques a lo bestia
                refreshTasks();
            }
        }

        if (requestCode == ACTIVITY_TASK_UPDATE) {
            if (resultCode == RESULT_OK) {
                refreshTasks();
            }
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // modifiquem el id
        updateTask(id);
    }
}












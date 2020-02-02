package com.todolist.intracloud.todolist;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.CamcorderProfile;
import android.support.v4.widget.SimpleCursorAdapter;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class toDoListFilterActivity extends ListActivity {

    private static int ACTIVITY_TASK_ADD = 1;
    private static int ACTIVITY_TASK_UPDATE = 2;

    private toDoListDatasource bd;
    private long idActual;
    private int positionActual;
    private adapterTodoListFilter scTasks;

    private filterKind filterActual;

    private static String[] from = new String[]{toDoListDatasource.TODOLIST_TITLE, toDoListDatasource.TODOLIST_DESCRIPCION, toDoListDatasource.TODOLIST_LEVEL};
    private static int[] to = new int[]{R.id.lblTitulo, R.id.lblDescription, R.id.lblPriority};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolistfilter);
        setTitle("Activity filter");

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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filter, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mnuTot:
                filterTot();
                return true;
            case R.id.mnuPendent:
                filterPendents();
                return true;
            case R.id.mnuFinalitzades:
                filterFinalitzades();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void filterTot() {
        // Demanem totes les tasques
        Cursor cursorTasks = bd.toDoList();
        filterActual = filterKind.FILTER_ALL;

        // Notifiquem al adapter que les dades han canviat i que refresqui
        scTasks.changeCursor(cursorTasks);
        scTasks.notifyDataSetChanged();

        // Ens situem en el primer registre
        getListView().setSelection(0);
    }

    private void filterPendents() {
        // Demanem totes les tasques pendents
        Cursor cursorTasks = bd.toDoListPending();
        filterActual = filterKind.FILTER_PENDING;

        // Notifiquem al adapter que les dades han canviat i que refresqui
        scTasks.changeCursor(cursorTasks);
        scTasks.notifyDataSetChanged();

        // Ens situem en el primer registre
        getListView().setSelection(0);
    }

    private void filterFinalitzades() {
        // Demanem totes les tasques finalitzades
        Cursor cursorTasks = bd.toDoListCompleted();
        filterActual = filterKind.FILTER_COMPLETED;

        // Notifiquem al adapter que les dades han canviat i que refresqui
        scTasks.changeCursor(cursorTasks);
        scTasks.notifyDataSetChanged();

        // Ens situem en el primer registre
        getListView().setSelection(0);
    }

    private void loadTasks() {

        // Demanem totes les tasques
        Cursor cursorTasks = bd.toDoListPending();
        filterActual = filterKind.FILTER_PENDING;

        // Now create a simple cursor adapter and set it to display
        scTasks = new adapterTodoListFilter(this, R.layout.row_todolistfilter, cursorTasks, from, to, 1);
        setListAdapter(scTasks);
    }

    private void refreshTasks() {

        Cursor cursorTasks = null;

        // Demanem les tasques depenen del filtre que s'estigui aplicant
        switch (filterActual) {
            case FILTER_ALL:
                cursorTasks = bd.toDoList();
                break;
            case FILTER_COMPLETED:
                cursorTasks = bd.toDoListCompleted();
                break;
            case FILTER_PENDING:
                cursorTasks = bd.toDoListPending();
                break;
        }


        // Notifiquem al adapter que les dades han canviat i que refresqui
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

class adapterTodoListFilter extends android.widget.SimpleCursorAdapter {
    private static final String colorTaskPending = "#d78290";
    private static final String colorTaskCompleted = "#d7d7d7";

    public adapterTodoListFilter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = super.getView(position, convertView, parent);

        // Agafem l'objecte de la view que es una LINEA DEL CURSOR
        Cursor linia = (Cursor) getItem(position);
        int done = linia.getInt(
                linia.getColumnIndexOrThrow(toDoListDatasource.TODOLIST_DONE)
        );

        // Pintem el fons de la view segons està completada o no
        if (done==1) {
            view.setBackgroundColor(Color.parseColor(colorTaskCompleted));
        }
        else {
            view.setBackgroundColor(Color.parseColor(colorTaskPending));
        }

        return view;
    }
}

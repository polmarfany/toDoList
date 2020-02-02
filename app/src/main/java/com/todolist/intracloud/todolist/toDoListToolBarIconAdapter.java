package com.todolist.intracloud.todolist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

public class toDoListToolBarIconAdapter extends AppCompatActivity {
    private static int ACTIVITY_TASK_ADD = 1;
    private static int ACTIVITY_TASK_UPDATE = 2;

    private toDoListDatasource bd;
    private long idActual;
    private int positionActual;
    private adapterTodoIcon scTasks;
    private filterKind filterActual;

    private static String[] from = new String[]{toDoListDatasource.TODOLIST_TITLE, toDoListDatasource.TODOLIST_DESCRIPCION, toDoListDatasource.TODOLIST_LEVEL, toDoListDatasource.TODOLIST_DONE};
    private static int[] to = new int[]{R.id.lblTitulo, R.id.lblDescription, R.id.lblPriority, R.id.lblState};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_todolisttoolbariconadapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
            }
        });

        setTitle("ToolBar & Adapter Icon");
        bd = new toDoListDatasource(this);
        loadTasks();
    }

    private void loadTasks() {

        // Demanem totes les tasques
        Cursor cursorTasks = bd.toDoList();

        // Now create a simple cursor adapter and set it to display
        scTasks = new adapterTodoIcon(this, R.layout.row_todolisticon, cursorTasks, from, to, 1);
        //scTasks.oTodoListIcon = this;

        filterActual = filterKind.FILTER_ALL;

        ListView lv = (ListView) findViewById(R.id.lvDades);
        lv.setAdapter(scTasks);

        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,
                                            int position, long id) {

                        // modifiquem el id
                        updateTask(id);
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnAdd:
                addTask();
                return true;
            case R.id.btnChecked:
                filterFinalitzades();
                return true;
            case R.id.btnUnChecked:
                filterPendents();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    private void filterTot() {
        // Demanem totes les tasques
        Cursor cursorTasks = bd.toDoList();
        filterActual = filterKind.FILTER_ALL;

        // Notifiquem al adapter que les dades han canviat i que refresqui
        scTasks.changeCursor(cursorTasks);
        scTasks.notifyDataSetChanged();

        // Ens situem en el primer registre
        ListView lv = (ListView) findViewById(R.id.lvDades);
        lv.setSelection(0);

        Snackbar.make(findViewById(android.R.id.content), "Està visualitzant totes les tasques...", Snackbar.LENGTH_LONG)
                .show();
    }

    private void filterPendents() {
        // Demanem totes les tasques pendents
        Cursor cursorTasks = bd.toDoListPending();
        filterActual = filterKind.FILTER_PENDING;

        // Notifiquem al adapter que les dades han canviat i que refresqui
        scTasks.changeCursor(cursorTasks);
        scTasks.notifyDataSetChanged();

        // Ens situem en el primer registre
        ListView lv = (ListView) findViewById(R.id.lvDades);
        lv.setSelection(0);

        Snackbar.make(findViewById(android.R.id.content), "Està visualitzant taques pendents...", Snackbar.LENGTH_LONG)
                .show();
    }

    private void filterFinalitzades() {
        // Demanem totes les tasques finalitzades
        Cursor cursorTasks = bd.toDoListCompleted();
        filterActual = filterKind.FILTER_COMPLETED;

        // Notifiquem al adapter que les dades han canviat i que refresqui
        scTasks.changeCursor(cursorTasks);
        scTasks.notifyDataSetChanged();

        // Ens situem en el primer registre
        ListView lv = (ListView) findViewById(R.id.lvDades);
        lv.setSelection(0);

        Snackbar.make(findViewById(android.R.id.content), "Està visualitzant tasques finalitzades...", Snackbar.LENGTH_LONG)
                .show();
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

    public void deleteTask(final int _id) {
        // Pedimos confirmación
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("¿Desitja eliminar la tasca?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                bd.taskDelete(_id);
                refreshTasks();
            }
        });

        builder.setNegativeButton("No", null);

        builder.show();
    }

}

class adapterTodoIcon extends android.widget.SimpleCursorAdapter {

    private static final String colorTaskPending = "#d78290";
    private static final String colorTaskCompleted = "#d7d7d7";

    private  toDoListToolBarIconAdapter oTodoListIcon;

    public adapterTodoIcon(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        oTodoListIcon = (toDoListToolBarIconAdapter) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = super.getView(position, convertView, parent);

        // Agafem l'objecte de la view que es una LINEA DEL CURSOR
        Cursor linia = (Cursor) getItem(position);

        int done = linia.getInt(linia.getColumnIndexOrThrow(toDoListDatasource.TODOLIST_DONE));

        // Pintem el fons de la view segons està completada o no
        if (done == 1) {
            view.setBackgroundColor(Color.parseColor(colorTaskCompleted));
        }
        else {
            view.setBackgroundColor(Color.parseColor(colorTaskPending));
        }

        // Capturem botons
        ImageView btnMensage = (ImageView) view.findViewById(R.id.btnDelete);

        btnMensage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Busco la ROW
                View row = (View) v.getParent();
                // Busco el ListView
                ListView lv = (ListView) row.getParent();
                // Busco quina posicio ocupa la Row dins de la ListView
                int position = lv.getPositionForView(row);

                // Carrego la linia del cursor de la posició.
                Cursor linia = (Cursor) getItem(position);

                oTodoListIcon.deleteTask(linia.getInt(linia.getColumnIndexOrThrow(toDoListDatasource.TODOLIST_ID)));
            }
        });

        return view;
    }
}


package com.todolist.intracloud.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class taskActivity extends Activity {

    private long idTask;
    private toDoListDatasource bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        bd = new toDoListDatasource(this);

        // Botones de aceptar y cancelar
        // Boton ok
        Button  btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                aceptarCambios();
            }
        });

        // Boton eliminar
        Button  btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteTask();
            }
        });

        // Boton cancelar
        Button  btnCancel = (Button) findViewById(R.id.btnCancelar);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancelarCambios();
            }
        });

        // Busquem el id que estem modificant
        // si el el id es -1 vol dir que s'està creant
        idTask = this.getIntent().getExtras().getLong("id");

        if (idTask != -1) {
            // Si estem modificant carreguem les dades en pantalla
            cargarDatos();
        }
        else {
            // Si estem creant amaguem el checkbox de finalitzada i el botó d'eliminar
            CheckBox chk = (CheckBox) findViewById(R.id.chkCompleted);
            chk.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        }
    }

    private void cargarDatos() {

        // Demanem un cursor que retorna un sol registre amb les dades de la tasca
        // Això es podria fer amb un classe pero...
        Cursor datos = bd.task(idTask);
        datos.moveToFirst();

        // Carreguem les dades en la interfície
        TextView tv;

        tv = (TextView) findViewById(R.id.edtTitulo);
        tv.setText(datos.getString(datos.getColumnIndex(toDoListDatasource.TODOLIST_TITLE)));

        tv = (TextView) findViewById(R.id.edtDescripcion);
        tv.setText(datos.getString(datos.getColumnIndex(toDoListDatasource.TODOLIST_DESCRIPCION)));

        tv = (TextView) findViewById(R.id.edtPrioritat);
        tv.setText(datos.getString(datos.getColumnIndex(toDoListDatasource.TODOLIST_LEVEL)));

        // Finalment el checkbox
        int finalitzada = datos.getInt(datos.getColumnIndex(toDoListDatasource.TODOLIST_DONE));

        CheckBox chk;
        chk = (CheckBox) findViewById(R.id.chkCompleted);
        if (finalitzada == 1) {
            chk.setChecked(true);
        } else {
            chk.setChecked(false);
        }
    }

    private void aceptarCambios() {
        // Validem les dades
        TextView tv;

        // Títol ha d'estar informat
        tv = (TextView) findViewById(R.id.edtTitulo);
        String titol = tv.getText().toString();
        if (titol.trim().equals("")) {
            myDialogs.showToast(this,"Ha d'informar el títol");
            return;
        }

        // La Prioritat entre 1 i 5
        tv = (TextView) findViewById(R.id.edtPrioritat);
        int iPrioritat;
        try {
           iPrioritat = Integer.valueOf(tv.getText().toString());
        }
        catch (Exception e) {
            myDialogs.showToast(this,"La prioritat ha de ser un valor entre 1 i 5");
            return;
        }

        if ((iPrioritat < 1) || (iPrioritat > 5)) {
            myDialogs.showToast(this,"La prioritat ha de ser un valor entre 1 i 5");
            return;
        }

        tv = (TextView) findViewById(R.id.edtDescripcion);
        String descripcion = tv.getText().toString();

        // Mirem si estem creant o estem guardant
        if (idTask == -1) {
            idTask = bd.taskAdd(titol, descripcion, iPrioritat);
        }
        else {
            bd.taskUpdate(idTask,titol,descripcion,iPrioritat);

            // ara indiquem si la tasca esta finalitzada o no
            CheckBox chk = (CheckBox) findViewById(R.id.chkCompleted);
            if (chk.isChecked()) {
                bd.taskCompleted(idTask);
            }
            else {
                bd.taskPending(idTask);
            }
        }

        Intent mIntent = new Intent();
        mIntent.putExtra("id", idTask);
        setResult(RESULT_OK, mIntent);

        finish();
    }

    private void cancelarCambios() {
        Intent mIntent = new Intent();
        mIntent.putExtra("id", idTask);
        setResult(RESULT_CANCELED, mIntent);

        finish();
    }

    private void deleteTask() {

        // Pedimos confirmación
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("¿Desitja eliminar la tasca?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                bd.taskDelete(idTask);

                Intent mIntent = new Intent();
                mIntent.putExtra("id", -1);  // Devolvemos -1 indicant que s'ha eliminat
                setResult(RESULT_OK, mIntent);

                finish();
            }
        });

        builder.setNegativeButton("No", null);

        builder.show();

    }
}

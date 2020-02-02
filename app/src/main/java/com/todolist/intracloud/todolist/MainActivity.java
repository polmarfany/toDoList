package com.todolist.intracloud.todolist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {

    private static final int ACTIVITY_SIMPLE = 0;
    private static final int ACTIVITY_FILTER = 1;
    private static final int ACTIVITY_ICON = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn;

        // layout simple
        btn = (Button) findViewById(R.id.btnLayoutSimple);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                activitySimple();
            }
        });

        // layout filter
        btn = (Button) findViewById(R.id.btnLayoutFilter);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                activityFilter();
            }
        });

        // toolbar i adapter amb icon
        btn = (Button) findViewById(R.id.btnLayoutBotons);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                activityToolBarIcon();
            }
        });

        setTitle("toDoList by Rafa ;-)");
    }

    private void activitySimple() {
        Intent i = new Intent(this, toDoListSimpleActivity.class );
        startActivityForResult(i,ACTIVITY_SIMPLE);
    }

    private void activityFilter() {
        Intent i = new Intent(this, toDoListFilterActivity.class );
        startActivityForResult(i,ACTIVITY_FILTER);
    }

    private void activityToolBarIcon() {
        Intent i = new Intent(this, toDoListToolBarIconAdapter.class );
        startActivityForResult(i,ACTIVITY_ICON);
    }
}

package se.frand.app.todo;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import se.frand.app.todo.data.TodoContract;
import se.frand.app.todo.data.TodoDbHelper;


public class MainActivity extends ListActivity {

    private TodoDbHelper dbHelper;
    private TodoListAdapter adapter;
    public static final String ADAPTER_KEY = "adapter";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TodoDbHelper(this);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = TodoContract.getTodoItems(db, this);
        adapter = new TodoListAdapter(this, cursor, 0);
        setListAdapter(adapter);

        View footer = getLayoutInflater().inflate(R.layout.todo_list_footer, null, false);
        ListView listview = getListView();
        listview.addFooterView(footer);

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteItemDialog(id);
                return true;
            }
        });

        db.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_filter_settings) {
            // open the filter preferences
            Intent intent = new Intent(this, FilterSettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // in todo_list_footer.xml onClick refers to this
    public void newTodoItem(View v) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // show a dialog to get the title and description
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.new_dialog_title));

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        final EditText title = new EditText(this);
        title.setHint(R.string.new_dialog_title_hint);
        title.setInputType(InputType.TYPE_CLASS_TEXT);

        final EditText desc = new EditText(this);
        desc.setHint(R.string.new_dialog_desc_hint);
        desc.setInputType(InputType.TYPE_CLASS_TEXT);

        ll.addView(title);
        ll.addView(desc);
        builder.setView(ll);

        // Set up the buttons
        builder.setPositiveButton(getString(R.string.new_dialog_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                long newId = TodoContract.newTodoItem(db, title.getText().toString(), desc.getText().toString());
                //redraw the list now.
                if(newId != -1) {
                    // refresh the list view
                    adapter.swapAdapterCursor(db);
                }
            }
        });
        builder.setNegativeButton(getString(R.string.new_dialog_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void deleteItemDialog(final long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // show a dialog to get the title and description
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.delete_dialog_title));

        // Set up the buttons
        builder.setPositiveButton(getString(R.string.delete_dialog_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Context context = getApplicationContext();
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                TodoContract.deleteTodoItem(db, id);
                adapter.swapAdapterCursor(db);
            }
        });
        builder.setNegativeButton(getString(R.string.delete_dialog_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}

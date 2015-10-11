package se.frand.app.todo;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TodoDbHelper(this);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = TodoContract.getTodoItems(db);
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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {
            // TODO requery the db and show cursor to the adapter
            return true;
        }else if (id == R.id.action_filter_settings) {
            // TODO open the filter preferences
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // in todo_list_footer.xml onClick refers to this
    public void newTodoItem(View v) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // show a dialog to get the title and description
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Todo Item:");

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        final EditText title = new EditText(this);
        title.setHint(R.string.hint_title);
        title.setInputType(InputType.TYPE_CLASS_TEXT);

        final EditText desc = new EditText(this);
        desc.setHint(R.string.hint_desc);
        desc.setInputType(InputType.TYPE_CLASS_TEXT);

        ll.addView(title);
        ll.addView(desc);
        builder.setView(ll);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
        builder.setTitle("Delete this Item?");

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Context context = getApplicationContext();
                SQLiteDatabase db = context.openOrCreateDatabase(
                        TodoDbHelper.DATABASE_NAME,
                        context.MODE_PRIVATE,
                        null
                );

                TodoContract.deleteTodoItem(db, id);
                adapter.swapAdapterCursor(db);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}

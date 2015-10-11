package se.frand.app.todo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.TextView;

import se.frand.app.todo.data.TodoContract;
import se.frand.app.todo.data.TodoDbHelper;

/**
 * Created by victorfrandsen on 10/11/15.
 */
public class TodoListAdapter extends CursorAdapter {

    private static class ViewHolder {
        //views
        public CheckBox doneCheckbox;
        public TextView titleTextView;
        public TextView descTextView;

        public ViewHolder(View v) {
            doneCheckbox = (CheckBox) v.findViewById(R.id.todo_complete_checkbox);
            titleTextView = (TextView) v.findViewById(R.id.todo_title);
            descTextView = (TextView) v.findViewById(R.id.todo_description);
        }
    }


    public TodoListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.todo_list_item,parent,false);

        ViewHolder holder = new ViewHolder(v);
        v.setTag(holder);

        return v;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        final String title = cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_NAME_TITLE));
        final String desc = cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_NAME_DESC));
        final long id = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoEntry._ID));
        final boolean completed = cursor.isNull(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_NAME_COMPLETED));

        if(completed) {
            view.setBackgroundColor(Color.RED);
        } else {
            view.setBackgroundColor(Color.GREEN);
            holder.doneCheckbox.setChecked(true);
        }

        holder.descTextView.setText(desc);
        holder.titleTextView.setText(title);

        // when check box is checked then set completed date to current time
        // if unchecked then set to null
        holder.doneCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SQLiteDatabase db = context.openOrCreateDatabase(
                        TodoDbHelper.DATABASE_NAME,
                        context.MODE_PRIVATE,
                        null
                );

                if(isChecked) {
                    TodoContract.updateTodoItemCompleted(db, id);
                } else {
                    TodoContract.updateTodoItemUnComplete(db, id);
                }
                swapAdapterCursor(db);
            }
        });
    }



    public void swapAdapterCursor(SQLiteDatabase db) {
        swapCursor(TodoContract.getTodoItems(db));
    }
}

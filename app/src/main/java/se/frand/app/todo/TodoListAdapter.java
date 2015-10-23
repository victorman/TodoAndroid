package se.frand.app.todo;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.Date;

import se.frand.app.todo.data.TodoContract;
import se.frand.app.todo.data.TodoDbHelper;

/**
 * Created by victorfrandsen on 10/11/15.
 */
public class TodoListAdapter extends CursorAdapter implements SharedPreferences.OnSharedPreferenceChangeListener {

    Context mContext;


    private static class ViewHolder {
        //views
        public CheckBox doneCheckbox;
        public TextView titleTextView;
        public TextView descTextView;
        public TextView dateTextView;

        public ViewHolder(View v) {
            doneCheckbox = (CheckBox) v.findViewById(R.id.todo_complete_checkbox);
            titleTextView = (TextView) v.findViewById(R.id.todo_title);
            descTextView = (TextView) v.findViewById(R.id.todo_description);
            dateTextView = (TextView) v.findViewById(R.id.todo_date);
        }
    }


    public TodoListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        preferences.registerOnSharedPreferenceChangeListener(TodoListAdapter.this);
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
        final String date = Util.getDate(new Date(cursor.getLong(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_NAME_CREATED))*1000), Util.MDY_DATE_FORMAT);
        final long id = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoEntry._ID));
        final boolean completed = !cursor.isNull(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_NAME_COMPLETED));

        if(completed) {
            //view.setBackgroundColor(Color.RED);
            holder.doneCheckbox.setChecked(true);
        } else {
            //view.setBackgroundColor(Color.GREEN);
            holder.doneCheckbox.setChecked(false);
        }

        holder.descTextView.setText(desc);
        holder.titleTextView.setText(title);
        holder.dateTextView.setText(date);

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

                if (isChecked) {
                    TodoContract.updateTodoItemCompleted(db, id);
                } else {
                    TodoContract.updateTodoItemUnComplete(db, id);
                }
                swapAdapterCursor(db);
            }
        });
    }



    public void swapAdapterCursor(SQLiteDatabase db) {
        swapCursor(TodoContract.getTodoItems(db, mContext));
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
;

        Log.v("onPreferenceChange()", "preference changed " + key);

        swapAdapterCursor(mContext.openOrCreateDatabase(
                TodoDbHelper.DATABASE_NAME,
                mContext.MODE_PRIVATE,
                null
        ));
    }
}

package se.frand.app.todo.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by victorfrandsen on 10/10/15.
 */
public class TodoContract {

    public TodoContract() {}

    public static class TodoEntry implements BaseColumns {

        public static final String TABLE_NAME = "todo";
        public static final String COLUMN_NAME_CREATED = "datecreated";
        public static final String COLUMN_NAME_COMPLETED = "datecompleted";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESC = "description";
    }

    public static Cursor getTodoItems(SQLiteDatabase db) {
        String[] projection = {
                TodoEntry._ID,
                TodoEntry.COLUMN_NAME_CREATED,
                TodoEntry.COLUMN_NAME_COMPLETED,
                TodoEntry.COLUMN_NAME_TITLE,
                TodoEntry.COLUMN_NAME_DESC
        };

        // TODO get preferences

        // put oldest first
        String sortOrder = TodoEntry.COLUMN_NAME_CREATED;

        Cursor c = db.query(
                TodoEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        return c;
    }

    public static long newTodoItem(SQLiteDatabase db, String title, String desc) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TodoEntry.COLUMN_NAME_CREATED, System.currentTimeMillis());
        values.put(TodoEntry.COLUMN_NAME_TITLE, title);
        values.put(TodoEntry.COLUMN_NAME_DESC, desc);

        long newId = db.insert(
                TodoEntry.TABLE_NAME,
                null,
                values);

        //db.close();
        return newId;
    }

    public static int deleteTodoItem(SQLiteDatabase db, long id) {
        String where =
                TodoEntry._ID + "=?";

        String[] args = {
                "" + id
        };

        int num = db.delete(
                TodoEntry.TABLE_NAME,
                where,
                args
        );

        return num;
    }

    public static int updateTodoItemCompleted(SQLiteDatabase db, long id) {
        String where =
                TodoEntry._ID + "=?";

        String[] args = {
                "" + id
        };
        ContentValues vals = new ContentValues();
        vals.put(TodoEntry.COLUMN_NAME_COMPLETED,System.currentTimeMillis());

        int num = db.update(
                TodoEntry.TABLE_NAME,
                vals,
                where,
                args
        );

        return num;
    }

    public static int updateTodoItemUnComplete(SQLiteDatabase db, long id) {
        String where =
                TodoEntry._ID + "=?";

        String[] args = {
                "" + id
        };
        ContentValues vals = new ContentValues();
        vals.putNull(TodoEntry.COLUMN_NAME_COMPLETED);

        int num = db.update(
                TodoEntry.TABLE_NAME,
                vals,
                where,
                args
        );

        return num;
    }
}

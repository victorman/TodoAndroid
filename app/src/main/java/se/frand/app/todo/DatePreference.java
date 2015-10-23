package se.frand.app.todo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.Preference;
import android.util.AttributeSet;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by victorfrandsen on 10/21/15.
 */
public class DatePreference extends Preference {
    private DatePicker picker;
    private Long time;
    private Context mContext;

    public DatePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    /*@Override
    protected View onCreateView(ViewGroup parent) {
        LayoutInflater li = (LayoutInflater)getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        return li.inflate(R.layout.date_filter_preference, parent, false);
    }*/

    public void showPicker() {
        picker = new DatePicker(mContext);
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dateTextView.setText();

                Calendar cal = Calendar.getInstance();
                cal.clear();
                cal.set(picker.getYear(), picker.getMonth(), picker.getDayOfMonth());

                time = cal.getTime().getTime();
                persistLong(time);
                callChangeListener(time);
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.setView(picker);

        dialog.show();
    }

}

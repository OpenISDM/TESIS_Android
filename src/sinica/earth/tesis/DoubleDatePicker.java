package sinica.earth.tesis;

import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

import java.util.Calendar;

public class DoubleDatePicker implements OnDateChangedListener {

    HomeActivity homeActivity;
    private DurationHelper From, To;
    Dialog dialogDatePicker;
    DatePicker fromDatePicker, toDatePicker;

    public DoubleDatePicker(HomeActivity homeActivity) {

        this.homeActivity = homeActivity;
        dialogDatePicker = new Dialog(homeActivity);

        dialogDatePicker.setContentView(R.layout.double_date_picker);

        fromDatePicker = (DatePicker) dialogDatePicker.findViewById(R.id.datePicker1);
        toDatePicker = (DatePicker) dialogDatePicker.findViewById(R.id.datePicker2);

        // fromDatePicker.updateDate(myDate.getFrom().getYear(),
        // myDate.getFrom().getMonth(), myDate.getFrom().getDay());

        fromDatePicker.init(
                DurationHelper.getFrom().getYear(),
                DurationHelper.getFrom().getMonth() - 1,
                DurationHelper.getFrom().getDay(), this);

        toDatePicker.init(
                DurationHelper.getTo().getYear(),
                DurationHelper.getTo().getMonth() - 1,
                DurationHelper.getTo().getDay(), this);

        Button btn = (Button) dialogDatePicker.findViewById(R.id.buttonConfirmDate);

        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO select date
                setDate();
                Log.d("Here!", "at picker From:" + From.DatetoString() + " To:" + To.DatetoString());
                dialogDatePicker.dismiss();
            }
        });
        dialogDatePicker.setTitle("請選擇日期區間");
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        Calendar calendarFrom = Calendar.getInstance();
        Calendar calendarTo = Calendar.getInstance();

        calendarFrom.set(
                fromDatePicker.getYear(),
                fromDatePicker.getMonth(),
                fromDatePicker.getDayOfMonth());

        calendarTo.set(
                toDatePicker.getYear(),
                toDatePicker.getMonth(),
                toDatePicker.getDayOfMonth());

        if (calendarFrom.getTimeInMillis() > calendarTo.getTimeInMillis()) {
            if (view == fromDatePicker) {
                toDatePicker.updateDate(year, monthOfYear, dayOfMonth + 1);
            } else if (view == toDatePicker) {
                fromDatePicker.updateDate(year, monthOfYear, dayOfMonth - 1);
            }
        }
    }

    private void setDate() {
        DatePicker datePicker1 =
                (DatePicker) dialogDatePicker.findViewById(R.id.datePicker1);

        DatePicker datePicker2 =
                (DatePicker) dialogDatePicker.findViewById(R.id.datePicker2);

        From = new DurationHelper(
                datePicker1.getYear(),
                datePicker1.getMonth(),
                datePicker1.getDayOfMonth());

        To = new DurationHelper(
                datePicker2.getYear(),
                datePicker2.getMonth(),
                datePicker2.getDayOfMonth());

        homeActivity.updateEarthquakeIds(From.DatetoString(), To.DatetoString());

//        homeActivity.downloadEarthquakeEventsData();
    }

    public void show() {
        dialogDatePicker.show();
    }

    public DurationHelper getFrom() {
        return From;
    }

    public DurationHelper getTo() {
        return To;
    }

}

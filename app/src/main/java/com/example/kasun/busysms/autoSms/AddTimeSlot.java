package com.example.kasun.busysms.autoSms;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;

import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.app.AlertDialog;
import android.widget.Toast;

import com.example.kasun.busysms.DatabaseHelper;
import com.example.kasun.busysms.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Kasun Somadasa
 * This is the activity which save new time slot to db
 */

public class AddTimeSlot extends AppCompatActivity {

    static final int DILOG_FROM=0;
    static final int DILOG_TO=1;
    EditText msg,state;
    ArrayList<String> selectedItems=new ArrayList<String>();
    public String selections;
    Button saveBtn;
    TextView fromTimeText,toTimeText,displayText;
    int noOfHour,noOfminute;
    DatabaseHelper db;
    AlertDialog alert;
    CheckBox checkBoxCall,checkBoxSms;
    String checkSms="false",checkCall="false",testCheck;

    // Get current hour,minute and second
    Calendar now = Calendar.getInstance();
    int hour = now.get(Calendar.HOUR_OF_DAY);
    int minute = now.get(Calendar.MINUTE);
    int second = now.get(Calendar.SECOND);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time_slot);

        db = new DatabaseHelper(this);
        msg=(EditText)findViewById(R.id.messege);
        state=(EditText)findViewById(R.id.status);
        fromTimeText=(TextView)findViewById(R.id.time_from);
        toTimeText=(TextView) findViewById(R.id.time_to);
        displayText=(TextView) findViewById(R.id.repeat);
        saveBtn=(Button) findViewById(R.id.addTimeSlot);
        fromTimeText.setText(hour + ":" + minute+":"+second);
        toTimeText.setText(hour + ":" + minute+":"+second);
        displayText.setText("Choose your days");

        showDialogTimeFrom();
        showDialogTimeTo();
        showDialogdays();
        addData();
        addListenerForCheckBox();
        //enable action bar back btn
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }



    public void showIcon(){
        /*
         * show notification with app icon on mobile notification bar
         */
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)//R.mipmap.ic_launcher-->for app icon
                .setContentTitle("Busy SMS Activated");
        Intent resultIntent = new Intent(this, SmsHome.class); //when user click on notification then directly comes to SmsHome activity
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(1, notification);
    }


public void showDialogTimeFrom(){
     /*
      * show time picker dialog for 'From'
      */
    fromTimeText.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(DILOG_FROM);
                }
            }
    );
}
    public void showDialogTimeTo(){
     /*
      * show time picker dialog for 'To'
      */
        toTimeText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(DILOG_TO);
                    }
                }
        );
    }

    @Override
    protected Dialog onCreateDialog(int id){
     /*
      * get user choosen hour and minute to variables
      */
        if(id==DILOG_FROM )
            return new TimePickerDialog(this,2,timePikerListnerFrom,noOfHour,noOfminute,false);
        else if(id==DILOG_TO)
            return new TimePickerDialog(this,2,timePikerListnerTo,noOfHour,noOfminute,false);
        return  null;
    }

private  TimePickerDialog.OnTimeSetListener timePikerListnerFrom
        =new TimePickerDialog.OnTimeSetListener(){
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        noOfHour=hourOfDay;
        noOfminute=minute;

        fromTimeText.setText(noOfHour+":"+noOfminute+":00");
    }


};
    private  TimePickerDialog.OnTimeSetListener timePikerListnerTo
            =new TimePickerDialog.OnTimeSetListener(){
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            noOfHour=hourOfDay;
            noOfminute=minute;

            toTimeText.setText(noOfHour+":"+noOfminute+":00");
        }


    };

    public void showDialogdays() {
        displayText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selections="";
                        alert.show();
                }
                }
        );

        final  String[] items=getResources().getStringArray(R.array.my_date_choose);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle("Choose your days");
        // Specify the list array, the items to be selected by default (null for none),
        // and the listener through which to receive callbacks when items are selected
        builder.setMultiChoiceItems(R.array.my_date_choose, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    selectedItems.add(items[which]);
                                } else if (selectedItems.contains(items[which])) {
                                    // Else, if the item is already in the array, remove it
                                   selectedItems.remove(items[which]);
                                }
                            }
                        });
                // Set the action buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        selections="";
                        for (String ms:selectedItems) {
                            if(selections==""){
                                selections=ms;
                            }else{
                                selections=selections+","+ms;
                            }

                        }
                        //if selection is empty then display Choose your days" again
                        if(selections.equals("")){
                            displayText.setText("Choose your days");
                        }else{
                            displayText.setText(selections);
                        }

}
                });
        builder .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        alert =  builder.create();

    }

    public void addData() {
        /*
         * add new time slot details to db
         */
        saveBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      if (checkCall.equals("false") && checkSms.equals("false")) {
                          testCheck = "false";
                        } else {
                          testCheck = "true";
                        }

                       if (!displayText.getText().toString().equals("Choose your days") && !msg.getText().toString().equals("") && !state.getText().toString().equals("") && testCheck.equals("true")) {
                            boolean isInserted = db.insertData(fromTimeText.getText().toString(), toTimeText.getText().toString(), state.getText().toString(), displayText.getText().toString(), msg.getText().toString(), checkCall, checkSms, "Active");
                            if (isInserted == true) {
                                Toast.makeText(AddTimeSlot.this, "Your record is saved !!!", Toast.LENGTH_LONG).show();
                                showIcon();
                            } else {
                                Toast.makeText(AddTimeSlot.this, "Your record is not saved !!!", Toast.LENGTH_LONG).show();
                            }
                            Intent i = new Intent(AddTimeSlot.this, SmsHome.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        } else {
                           Toast.makeText(AddTimeSlot.this, "Some required fields are missing !!!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }




    public void addListenerForCheckBox() {
        //get check box values
        checkBoxCall = (CheckBox) findViewById(R.id.for_call);
        checkBoxSms = (CheckBox) findViewById(R.id.for_sms);

        checkBoxCall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is Call CheckBox checked?
                if (((CheckBox) v).isChecked()) {
                    checkCall="true";
                }else {
                    checkCall="false";
                }

            }
        });
        checkBoxSms.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is SMS CheckBox checked?
                if (((CheckBox) v).isChecked()) {
                    checkSms="true";
                }else {
                    checkSms="false";
                }

            }
        });

    }
 }


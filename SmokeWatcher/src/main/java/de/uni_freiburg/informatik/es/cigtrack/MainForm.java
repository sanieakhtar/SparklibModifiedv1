package de.uni_freiburg.informatik.es.cigtrack;

/*
 * Created by elio on 7/13/17.
 * This file contains the Registration Formular activity.
 */

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainForm extends AppCompatActivity {
    UserData myDb;
    EditText box_name,box_birth,box_weight;
    Button buttonAddData;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_form);

        myDb = new UserData(this);

        box_name = (EditText)findViewById(R.id.box_name);
        box_birth = (EditText)findViewById(R.id.box_birth);
        box_weight = (EditText)findViewById(R.id.box_weight);
        buttonAddData = (Button)findViewById(R.id.button_send);

        box_birth.setOnClickListener(openCalender);
        box_weight.setOnClickListener(openWeightSelector);

        AddData();
    }

    /********************************* ADD DATA TO DB *********************************/

    private boolean proceed = false;
    public void AddData() {
        buttonAddData.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor result = myDb.readData();
                        if(result.getCount() == 0){
                            boolean isInserted = myDb.insertData(
                                    box_name.getText().toString(),
                                    box_birth.getText().toString(),
                                    box_weight.getText().toString());
                            if (isInserted) {
                                proceed = true;
                            }
                            else {
                                Toast.makeText(MainForm.this, "Data not Inserted", Toast.LENGTH_LONG).show();
                                proceed = false;
                            }
                        }
                        else {
                            boolean isUpdated = myDb.updateData(
                                    String.valueOf(1),
                                    box_name.getText().toString(),
                                    box_birth.getText().toString(),
                                    box_weight.getText().toString());
                            if (isUpdated) {
                                proceed = true;
                            }
                            else {
                                Toast.makeText(MainForm.this, "Data not Updated", Toast.LENGTH_LONG).show();
                                proceed = false;
                            }
                        }
                        if (proceed){
                            ReadData();
                        }

                    }
                }
        );
    }

    /********************************* READ FROM DB *********************************/

    public void ReadData() {
        Cursor result = myDb.readData();
        if(result.getCount() == 0){
            // Show message
            ShowMsg("Error","No Data");
        }
        else {
            StringBuffer buffer = new StringBuffer();
            while(result.moveToNext()) {
                buffer.append("Nme: " + result.getString(1)+"\n");
                buffer.append("Birthday: " + result.getString(2)+"\n");
                buffer.append("Weight: " + result.getString(3)+" Kg.\n");
            }
            // Show data
            ShowMsg(getString(R.string.form_dialogConfirm),buffer.toString());
        }
    }

    public void ShowMsg(String title,String data){
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
        alert_confirm.setCancelable(true);
        alert_confirm.setTitle(title);
        alert_confirm.setMessage(data);
        alert_confirm.setPositiveButton("Accept",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });

        alert_confirm.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });
        alert_confirm.show();
    }

    /********************************* BIRTHDAY DIALOG *********************************/

    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabelBirth();
        }

    };

    private View.OnClickListener openCalender = new View.OnClickListener () {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            new DatePickerDialog(MainForm.this, date, 1990, 0, 9).show();
        }
    };

    private void updateLabelBirth() {

        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.GERMANY);

        box_birth.setText(sdf.format(myCalendar.getTime()));
    }

    /********************************* WEIGHT SELECTOR ********************************/

    private View.OnClickListener openWeightSelector = new View.OnClickListener () {

        @Override
        public void onClick(View v) {

            final Dialog d = new Dialog(MainForm.this);
            d.setTitle("NumberPicker");
            d.setContentView(R.layout.dialog);
            Button b1 = (Button) d.findViewById(R.id.button1);
            final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
            np.setMaxValue(130);
            np.setMinValue(30);
            np.setValue(70);
            np.setWrapSelectorWheel(false);
            //np.setOnValueChangedListener(this);
            b1.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    box_weight.setText(String.valueOf(np.getValue()));
                    d.dismiss();
                }
            });
            d.show();
        }
    };
}
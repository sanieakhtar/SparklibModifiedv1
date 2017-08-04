package de.uni_freiburg.informatik.es.cigtrack;

/*
 * Created by elio on 7/13/17.
 * This file contains the Registration Formular activity.
 */

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import de.uni_freiburg.informatik.es.cigtrack.NewPetActivity;

public class MainForm extends AppCompatActivity {
    UserData myDb;
    EditText box_name,box_birth,box_weight,box_petname,box_avgcigs;
    Button buttonAddData;
    boolean boDataBaseExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_form);

        myDb = new UserData(this);

        box_name = (EditText)findViewById(R.id.box_name);
        box_birth = (EditText)findViewById(R.id.box_birth);
        box_weight = (EditText)findViewById(R.id.box_weight);
        box_petname = (EditText)findViewById(R.id.box_petname);
        box_avgcigs = (EditText)findViewById(R.id.box_avgcigs);
        buttonAddData = (Button)findViewById(R.id.button_send);

        box_birth.setOnClickListener(openCalender);
        box_weight.setOnClickListener(openWeightSelector);

        AddData();
    }

    /********************************* FULLSCREEN MODE *********************************/
    //TODO: make entire layout so that it's fullscreen when it opens and no bar is seen at the top initially

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        decorView.setSystemUiVisibility(uiOptions);

        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    /********************************* ADD DATA TO DB *********************************/

    private boolean proceed = false;
    public void AddData() {
        buttonAddData.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int avgcigs = 5;
                        String str_avgcigs = box_avgcigs.getText().toString();
                        if (str_avgcigs.isEmpty()) {
                            avgcigs = 5;
                        }
                        else {
                            avgcigs = Integer.parseInt(str_avgcigs);
                        }
                        Cursor result = myDb.readData();
                        if(result.getCount() == 0){
                            boolean isInserted = myDb.insertData(
                                    box_name.getText().toString(),
                                    box_birth.getText().toString(),
                                    box_weight.getText().toString(),
                                    box_petname.getText().toString(),
                                    avgcigs);
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
                                    box_weight.getText().toString(),
                                    box_petname.getText().toString(),
                                    avgcigs);
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
                buffer.append("Your Name: " + result.getString(1)+"\n");
                buffer.append("Your Pet\'s Name: " + result.getString(4)+"\n");
                buffer.append("Your Birthday: " + result.getString(2)+"\n");
                buffer.append("Your Weight: " + result.getString(3)+" kg\n");
                buffer.append("Daily Cigarettes (Average): " + result.getString(5)+"\n");
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
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        boDataBaseExist = true;
                        Intent newPet = new Intent(MainForm.this, NewPetActivity.class);
                        startActivity(newPet);
                        finish();
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
            d.setContentView(R.layout.form_dialog);
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
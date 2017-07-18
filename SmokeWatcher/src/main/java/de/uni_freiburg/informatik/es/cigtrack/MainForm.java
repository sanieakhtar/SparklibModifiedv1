package de.uni_freiburg.informatik.es.cigtrack;

/**
 * Created by elio on 7/13/17.
 */

import android.app.DatePickerDialog;
import android.app.Dialog;
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
    Button buttonAddData, buttonReadData;
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
        buttonReadData = (Button)findViewById(R.id.button_read);
        textView = (TextView)findViewById(R.id.text_update);

        box_birth.setOnClickListener(openCalender);
        box_weight.setOnClickListener(openWeightSelector);

        AddData();
        ReadData();
    }

    /********************************* ADD DATA TO DB *********************************/

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
                            if (isInserted)
                                Toast.makeText(MainForm.this, "Data Inserted", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(MainForm.this, "Data not Inserted", Toast.LENGTH_LONG).show();
                        }
                        else {
                            boolean isUpdated = myDb.updateData(
                                    String.valueOf(1),
                                    box_name.getText().toString(),
                                    box_birth.getText().toString(),
                                    box_weight.getText().toString());
                            if (isUpdated)
                                Toast.makeText(MainForm.this, "Data Updated", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(MainForm.this, "Data not Updated", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    /********************************* READ FROM DB *********************************/

    public void ReadData() {
        buttonReadData.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor result = myDb.readData();
                        if(result.getCount() == 0){
                            // Show message
                            ShowMsg("Error","No Data");
                            return;
                        }
                        else {
                            StringBuffer buffer = new StringBuffer();
                            while(result.moveToNext()) {
                                buffer.append("NAME :" + result.getString(1)+"\n");
                                buffer.append("BIRTHDAY :" + result.getString(2)+"\n");
                                buffer.append("WEIGHT :" + result.getString(3)+" Kg.\n");
                            }
                            // Show data
                            // ShowMsg("Data",buffer.toString());
                            textView.setText(buffer.toString());
                        }
                    }
                }
        );
    }

    public void ShowMsg(String title,String data){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(data);
        builder.show();
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
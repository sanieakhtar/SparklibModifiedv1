package de.uni_freiburg.informatik.es.cigtrack;

/**
 * Created by elio on 7/13/17.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainForm extends AppCompatActivity {
    UserData myDb;
    EditText box_name,box_birth,box_weight;
    Button buttonAddData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_form);
        myDb = new UserData(this);

        box_name = (EditText)findViewById(R.id.box_name);
        box_birth = (EditText)findViewById(R.id.box_birth);
        box_weight = (EditText)findViewById(R.id.box_weight);
        buttonAddData = (Button)findViewById(R.id.button_adduser);
        //AddData();
    }

/*    public void AddData() {
        buttonAddData.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isInserted = myDb.insertData(
                                box_name.getText().toString(),
                                box_birth.getText().toString(),
                                box_weight.getText().toString());
                        if (isInserted == true)
                            Toast.makeText(MainForm.this, "Data Inserted", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainForm.this, "Data not Inserted", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }*/
}
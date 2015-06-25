package com.kylecorry.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by kyle on 6/25/15.
 */
public class AddActivity extends AppCompatActivity implements View.OnClickListener {

    // UI elements and db functions
    EditText amount, name;
    FloatingActionButton fab;
    MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // display the back arrow in the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_add);
        fab = (FloatingActionButton) findViewById(R.id.fabDone);
        amount = (EditText) findViewById(R.id.item_amount);
        name = (EditText) findViewById(R.id.item_name);
        dbHandler = new MyDBHandler(this, null);
        // listen for clicks on the fab
        fab.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        // get the item name from the edit text
        String itemName = name.getText().toString();
        // default value for the item amount
        int itemAmount;
        try {
            // get the integer value of the item amount
            // in try and catch in case it is blank because blank is not a number
            itemAmount = Integer.valueOf(amount.getText().toString());
        } catch (Exception e) {
            itemAmount = -1;
        }
        // make sure the fields are filled out
        if (!(itemName.isEmpty() || itemName.equals("")) && itemAmount > 0) {
            // add the item to the database
            dbHandler.addItem(itemName, itemAmount);
            // go back to the main screen
            startActivity(new Intent(getBaseContext(), MainActivity.class));
            // stop this screen
            finish();
        } else {
            // if the item name is not filled out display error
            if (itemName.isEmpty() || itemName.equals("")) {
                name.setError("Invalid item name");
            }
            // if the item amount is not filled out display error
            if (itemAmount <= 0) {
                amount.setError("Invalid item amount");
            }
        }
    }
}

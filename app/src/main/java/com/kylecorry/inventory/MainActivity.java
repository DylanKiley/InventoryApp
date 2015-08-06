package com.kylecorry.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Tutorial for Android development can be found here - https://www.udacity.com/course/developing-android-apps--ud853
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener {

    // for the list of inventory
    ArrayAdapter<String> adapter;
    ListView inventoryListView;
    List<String> inventory;
    // database functions
    MyDBHandler dbHandler;
    // fab
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawable(null);
        dbHandler = new MyDBHandler(this, null);
        // Use a list to store the inventory
        inventory = new ArrayList<>();
        // fill the list with the inventory from the database
        populateList();
        // set the listview UI element's data to inventory list
        adapter = new ArrayAdapter<String>(this, R.layout.list_item_inventory, R.id.item, inventory);
        inventoryListView = (ListView) findViewById(R.id.list_view_inventory);
        inventoryListView.setAdapter(adapter);
        // register on item click listeners for the listview
        inventoryListView.setOnItemClickListener(this);
        inventoryListView.setOnItemLongClickListener(this);
        // register click listener on the fab
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        // tell the user what to do
        Toast.makeText(this, "Tap item to subtract 1, hold to delete", Toast.LENGTH_LONG).show();
        createAds();
    }


    // handle clicks for list items
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // make sure it is not empty
        if (!inventory.get(position).equals("No items in inventory")) {
            // get the clicked item
            String item = inventory.get(position);
            // get the amount and name of the item
            int amount = Integer.valueOf(item.split("     --     ")[1]);
            String name = inventory.get(position).split("     --     ")[0];
            // clear the inventory list
            inventory.clear();
            // subtract 1 from the amount and update the database
            dbHandler.updateItem(name, amount - 1);
            // fill the list view
            populateList();
            adapter.notifyDataSetChanged();
        }
    }

    // if user clicks and holds and item
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // make sure it is not empty
        if (!inventory.get(position).equals("No items in inventory")) {
            // delete the item form the database
            dbHandler.deleteItem(inventory.get(position).split("     --     ")[0]);
            // Tell the user it was deleted
            Toast.makeText(this, inventory.get(position).split("     --     ")[0] + " removed", Toast.LENGTH_SHORT).show();
            // clear the inventory list
            inventory.clear();
            // fill the list view
            populateList();
            adapter.notifyDataSetChanged();
        }
        return false;
    }

    // for the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // if menu item clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Settings.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createAds() {
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void populateList() {
        // get the items from the database
        String[] items = dbHandler.dbToString();
        // make sure it is not empty
        if (items.length == 0) {
            // if it is empty display this message
            items = new String[]{"No items in inventory"};
        }
        // add all of the items to the inventory list
        inventory.addAll(Arrays.asList(items));
    }


    @Override
    public void onClick(View v) {
        // bring the user to the add item screen
        startActivity(new Intent(getBaseContext(), AddActivity.class));
        // stop this screen
        finish();
    }
}

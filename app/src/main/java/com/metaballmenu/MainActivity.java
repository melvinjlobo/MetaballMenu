package com.metaballmenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.metaballmenu.widgets.MetaballMenu;


public class MainActivity extends AppCompatActivity implements MetaballMenu.MetaballMenuClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set the listener
        ((MetaballMenu)findViewById(R.id.metaball_menu)).setMenuClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menuitem1:
                Toast.makeText(this, "Clicked Menu Item 1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuitem2:
                Toast.makeText(this, "Clicked Menu Item 2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuitem3:
                Toast.makeText(this, "Clicked Menu Item 3", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuitem4:
                Toast.makeText(this, "Clicked Menu Item 4", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

package com.example.ex00231;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //when the app is opening show the ברירת מחדל את המסך הוספה
        if (savedInstanceState == null) {
            loadFragment(new AddExpenseFragment());
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            loadFragment(new AddExpenseFragment());
            return true;
        } else if (id == R.id.action_list) {
            loadFragment(new ViewExpensesFragment());
            return true;
        } else if (id == R.id.action_search) {
            loadFragment(new SearchExpensesFragment());
            return true;
        } else if (id == R.id.action_credits) {
            // מסך הקרדיטים הוא Activity נפרד (לפי הדרישות), לכן נשתמש ב-Intent
            startActivity(new Intent(this, CreditsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_container, fragment)
                .commit();
    }

}
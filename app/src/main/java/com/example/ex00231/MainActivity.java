package com.example.ex00231;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // טעינת מסך ברירת המחדל (הוספה) בפעם הראשונה
        if (savedInstanceState == null) {
            loadFragment(new AddExpenseFragment());
        }

        // חיבור כפתורי הניווט התחתוhtml וקביעת ההתנהגות שלהם בלחיצה
        Button btnAdd = findViewById(R.id.btn_nav_add);
        Button btnView = findViewById(R.id.btn_nav_view);
        Button btnSearch = findViewById(R.id.btn_nav_search);
        Button btnCredits = findViewById(R.id.btn_nav_credits);

        btnAdd.setOnClickListener(v -> loadFragment(new AddExpenseFragment()));
        btnView.setOnClickListener(v -> loadFragment(new ViewExpensesFragment()));
        btnSearch.setOnClickListener(v -> loadFragment(new SearchExpensesFragment()));

        btnCredits.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreditsActivity.class);
            startActivity(intent);
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_container, fragment)
                .commit();
    }
}
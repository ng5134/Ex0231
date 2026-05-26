package com.example.ex00231;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.fragment.app.Fragment;
public class AddExpenseFragment extends Fragment{
    private EditText etDescription, etAmount;
    private Spinner spinnerCategory;
    private Button btnPickDate, btnSaveExpense;
    private TextView tvSelectedDate;
    private String selectedDateStr = "";

    public AddExpenseFragment() { //default constructor required for fragmen
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_expense, container, false);

        etDescription = view.findViewById(R.id.etDescription);
        etAmount = view.findViewById(R.id.etAmount);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        btnPickDate = view.findViewById(R.id.btnPickDate);
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate);
        btnSaveExpense = view.findViewById(R.id.btnSaveExpense);
        btnPickDate.setOnClickListener(v -> showDatePicker()); //listener for the date
        btnSaveExpense.setOnClickListener(v -> saveExpenseToFirebase()); //listener for the save
        return view;
    }
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year1, month1, dayOfMonth) -> {
            calendar.set(year1, month1, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            selectedDateStr = sdf.format(calendar.getTime());
            tvSelectedDate.setText(selectedDateStr);
        }, year, month, day);

        datePickerDialog.show();
    }
    private void saveExpenseToFirebase() {
        String desc = etDescription.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (desc.isEmpty() || amountStr.isEmpty() || selectedDateStr.isEmpty()) {
            Toast.makeText(getContext(), "נא למלא את כל השדות ולבחור תאריך", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        //connect to firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance(DBConstants.FIREBASE_URL);
        DatabaseReference myRef = database.getReference(DBConstants.EXPENSES_NODE);


        String pushKey = myRef.push().getKey();
        if (pushKey == null) {
            Log.e("FirebaseError", "Failed to generate push key");
            return;
        }

        ExpenseRecord newExpense = new ExpenseRecord(pushKey, desc, amount, category, selectedDateStr);

        myRef.child(pushKey).setValue(newExpense)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseAsync", "Expense saved successfully: " + pushKey);
                    Toast.makeText(getContext(), "הוצאה נשמרה בהצלחה!", Toast.LENGTH_SHORT).show();
                    //Clear the fields after saving
                    etDescription.setText("");
                    etAmount.setText("");
                    tvSelectedDate.setText("!תאריך לא נבחר!");
                    selectedDateStr = "";
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseAsync", "Error saving expense: " + e.getMessage());
                    Toast.makeText(getContext(), "שגיאה בשמירה", Toast.LENGTH_SHORT).show();
                });
    }

}

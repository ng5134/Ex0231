package com.example.ex00231;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ViewExpensesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private List<ExpenseRecord> expenseList;
    private TextView tvTotalSum;

    private DatabaseReference myRef;

    public ViewExpensesFragment() {
        // בנאי ריק
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_expenses, container, false);

        tvTotalSum = view.findViewById(R.id.tvTotalSum);
        recyclerView = view.findViewById(R.id.recyclerViewExpenses);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        expenseList = new ArrayList<>();

        adapter = new ExpenseAdapter(expenseList, this::showOptionsDialog);
        recyclerView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance(DBConstants.FIREBASE_URL);
        myRef = database.getReference(DBConstants.EXPENSES_NODE);

        loadDataFromFirebase();

        return view;
    }

    private void loadDataFromFirebase() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                expenseList.clear();
                double totalSum = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ExpenseRecord expense = dataSnapshot.getValue(ExpenseRecord.class);
                    if (expense != null) {
                        expenseList.add(expense);
                        totalSum += expense.getAmount();
                    }
                }

                Collections.sort(expenseList, (e1, e2) -> e2.getDate().compareTo(e1.getDate()));

                adapter.notifyDataSetChanged();
                tvTotalSum.setText(String.format("סך הכל הוצאות: ₪ %.2f", totalSum));
                Log.d("FirebaseSync", "Data loaded and synced successfully.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseSync", "Failed to read value.", error.toException());
                Toast.makeText(getContext(), "שגיאה בטעינת נתונים", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showOptionsDialog(ExpenseRecord expense) {
        String[] options = {"עדכון הוצאה", "מחיקת הוצאה"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("בחר פעולה");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                showUpdateDialog(expense);
            } else if (which == 1) {
                deleteExpense(expense);
            }
        });
        builder.show();
    }


    private void deleteExpense(ExpenseRecord expense) {
        myRef.child(expense.getKeyID()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "נמחק בהצלחה", Toast.LENGTH_SHORT).show();
                    Log.d("FirebaseAsync", "Expense deleted: " + expense.getKeyID());
                })
                .addOnFailureListener(e -> Log.e("FirebaseAsync", "Error deleting", e));
    }


    private void showUpdateDialog(ExpenseRecord expense) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_update_expense, null);
        builder.setView(dialogView);

        EditText etUpdateDesc = dialogView.findViewById(R.id.etUpdateDescription);
        EditText etUpdateAmount = dialogView.findViewById(R.id.etUpdateAmount);

        etUpdateDesc.setText(expense.getDescription());
        etUpdateAmount.setText(String.valueOf(expense.getAmount()));

        builder.setPositiveButton("עדכן", (dialog, which) -> {
            String newDesc = etUpdateDesc.getText().toString().trim();
            String newAmountStr = etUpdateAmount.getText().toString().trim();

            if (!newDesc.isEmpty() && !newAmountStr.isEmpty()) {
                double newAmount = Double.parseDouble(newAmountStr);

                expense.setDescription(newDesc);
                expense.setAmount(newAmount);

                myRef.child(expense.getKeyID()).setValue(expense)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "עודכן בהצלחה", Toast.LENGTH_SHORT).show();
                            Log.d("FirebaseAsync", "Expense updated: " + expense.getKeyID());
                        });
            }
        });
        builder.setNegativeButton("ביטול", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
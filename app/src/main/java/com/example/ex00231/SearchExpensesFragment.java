package com.example.ex00231;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.List;


public class SearchExpensesFragment extends Fragment {

    private EditText etSearchDescription, etFilterAmount;
    private Button btnSearch, btnFilter;
    private RecyclerView recyclerView;

    private ExpenseAdapter adapter;
    private List<ExpenseRecord> searchResultsList;
    private DatabaseReference myRef;

    public SearchExpensesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_expenses, container, false);

        etSearchDescription = view.findViewById(R.id.etSearchDescription);
        etFilterAmount = view.findViewById(R.id.etFilterAmount);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnFilter = view.findViewById(R.id.btnFilter);

        recyclerView = view.findViewById(R.id.recyclerSearchResults);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchResultsList = new ArrayList<>();
        adapter = new ExpenseAdapter(searchResultsList, null);
        recyclerView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance(DBConstants.FIREBASE_URL);
        myRef = database.getReference(DBConstants.EXPENSES_NODE);

        btnSearch.setOnClickListener(v -> performSearch());
        btnFilter.setOnClickListener(v -> performFilter());

        return view;
    }


    private void performSearch() {
        String query = etSearchDescription.getText().toString().trim().toLowerCase();
        if (query.isEmpty()) {
            Toast.makeText(getContext(), "הכנס טקסט לחיפוש", Toast.LENGTH_SHORT).show();
            return;
        }

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                searchResultsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ExpenseRecord expense = dataSnapshot.getValue(ExpenseRecord.class);
                    if (expense != null && expense.getDescription().toLowerCase().contains(query)) {
                        searchResultsList.add(expense);
                    }
                }
                adapter.notifyDataSetChanged();

                if (searchResultsList.isEmpty()) {
                    Toast.makeText(getContext(), "לא נמצאו תוצאות", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseSearch", "Search failed: " + error.getMessage());
            }
        });
    }

    private void performFilter() {
        String amountStr = etFilterAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(getContext(), "הכנס סכום מינימלי לסינון", Toast.LENGTH_SHORT).show();
            return;
        }

        double minAmount = Double.parseDouble(amountStr);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                searchResultsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ExpenseRecord expense = dataSnapshot.getValue(ExpenseRecord.class);
                    if (expense != null && expense.getAmount() >= minAmount) {
                        searchResultsList.add(expense);
                    }
                }
                adapter.notifyDataSetChanged();

                if (searchResultsList.isEmpty()) {
                    Toast.makeText(getContext(), "לא נמצאו הוצאות מעל סכום זה", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseFilter", "Filter failed: " + error.getMessage());
            }
        });
    }
}
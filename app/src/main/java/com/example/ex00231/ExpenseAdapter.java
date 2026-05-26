package com.example.ex00231;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<ExpenseRecord> expensesList;
    private OnItemLongClickListener longClickListener;


    public interface OnItemLongClickListener {
        void onLongClick(ExpenseRecord expense);
    }

    public ExpenseAdapter(List<ExpenseRecord> expensesList, OnItemLongClickListener longClickListener) {
        this.expensesList = expensesList;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_expense_item, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        ExpenseRecord currentExpense = expensesList.get(position);

        holder.tvDescription.setText(currentExpense.getDescription());
        holder.tvDetails.setText(currentExpense.getCategory() + " | " + currentExpense.getDate());
        holder.tvAmount.setText(String.format("₪ %.2f", currentExpense.getAmount()));

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onLongClick(currentExpense);
                return true; // return true indicating the event was handled
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return expensesList.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription, tvDetails, tvAmount;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvRowDescription);
            tvDetails = itemView.findViewById(R.id.tvRowDetails);
            tvAmount = itemView.findViewById(R.id.tvRowAmount);
        }
    }
}
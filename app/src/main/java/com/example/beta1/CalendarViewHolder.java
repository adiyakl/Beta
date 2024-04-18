package com.example.beta1;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

//class purpose:
//	Represents a single item view within a RecyclerView for displaying calendar dates.
//	Handles click events on the item view.
public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public  final View parentView;
    private final ArrayList<LocalDate> days;
    private final CalendarAdapter.OnItemListener onItemListener;
    public final TextView dayOfMonth;
    public CalendarViewHolder( @NonNull View itemView, CalendarAdapter.OnItemListener onItemListener, ArrayList<LocalDate> days) {
        super(itemView);
        this.parentView = itemView.findViewById(R.id.parentView); //calendar cell view
        dayOfMonth =itemView.findViewById(R.id.cellDayText);  //calendar cell text
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
        this.days = days;
    }
    public void onClick(View view){
        onItemListener.onItemClick(getAdapterPosition(),days.get(getAdapterPosition()));
// Pass the adapter position and text of dayOfMonth to the onItemClick method of the listener
    }
}

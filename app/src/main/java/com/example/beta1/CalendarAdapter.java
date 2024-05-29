package com.example.beta1;



import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.time.LocalDate;
import java.util.ArrayList;
;

//Serves as an adapter for populating calendar items in a RecyclerView.
//Inflates the layout for each calendar item and binds data to them.
public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    // ArrayList to store the dates to be displayed in the calendar
    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;



    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener) {
        this.days = days;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        if (days.size()<=15){ // week view
            layoutParams.height = (int) (parent.getHeight()*0.5 );

        }
        view.setLayoutParams(layoutParams);
        // Return a new instance of CalendarViewHolder containing the view.
        return new CalendarViewHolder(view, onItemListener, days );
    }

    // Called when RecyclerView needs a new ViewHolder of the given type to represent an item
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        final LocalDate date = days.get(position);
        if(date == null)
            holder.dayOfMonth.setText("");
        else
        {
            holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
            if(date.equals(CalendarUtils.selectedDate))
                holder.parentView.setBackgroundColor(Color.parseColor("#F3D0D7"));

        }
    }


    @Override
    public int getItemCount() {
        return days.size();
    }

    //    Defines a method onItemClick to handle item click events.
    public interface OnItemListener {
        void onItemClick(int position, LocalDate date);
    }


}

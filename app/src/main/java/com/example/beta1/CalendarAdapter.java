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

//purpose:
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
        // Create a LayoutInflater from the parent's context to inflate views.
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // Inflate the 'calender_cell.xml' layout file into a View object without attaching to the parent.
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        // Access the view's current layout parameters.
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
        //This method ensures that each calendar cell displays the correct day number as you scroll through RecyclerView.
        // indicates the position of the item within the dataset (daysOfMonth ArrayList) that needs to be bound to the corresponding view holder.
    }


    //    Returns the total number of items in the days list.
    @Override
    public int getItemCount() {
        return days.size();
    }

    //    Defines a method onItemClick to handle item click events.
    public interface OnItemListener {
        void onItemClick(int position, LocalDate date);
    }


}

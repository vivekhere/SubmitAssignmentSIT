package edu.sinhgad.submitassignmentsit;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    String[] uploads, dates, times, uploaderNames;
    Context context;
    Activity activity;
    boolean isStudent;

    public RecyclerAdapter(Activity activity, Context context, String[] uploads, String[] dates, String[] times, String[] uploaderNames, boolean isStudent) {
        this.activity = activity;
        this.context = context;
        this.uploads = uploads;
        this.dates = dates;
        this.times = times;
        this.uploaderNames = uploaderNames;
        this.isStudent = isStudent;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_row, parent, false);
        return new MyViewHolder(view, mListener, activity, isStudent);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.pdfNameTextView.setText(uploads[position]);
        holder.dateTextView.setText(dates[position]);
        holder.timeTextView.setText(times[position]);
        holder.uploaderNameTextView.setText(uploaderNames[position]);
    }

    @Override
    public int getItemCount() {
        return uploads.length;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView pdfNameTextView, dateTextView, timeTextView, uploaderNameTextView, deleteTextView;

        public MyViewHolder(@NonNull View itemView, final OnItemClickListener listener, final Activity activity, final boolean isStudent) {
            super(itemView);

            pdfNameTextView = itemView.findViewById(R.id.pdfNameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            uploaderNameTextView = itemView.findViewById(R.id.uploaderNameTextView);
            deleteTextView = itemView.findViewById(R.id.deleteTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            deleteTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            AlertRecyclerDialog alertRecyclerDialog = new AlertRecyclerDialog(activity, position, isStudent);
                            alertRecyclerDialog.showAlert();
                        }
                    }
                }
            });

        }

    }
}

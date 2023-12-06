package com.example.eegreader.database;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eegreader.R;

import java.util.Arrays;
import java.util.List;

public class CsvDataAdapter extends RecyclerView.Adapter<CsvDataAdapter.ViewHolder> {

    private List<String[]> data;

    public CsvDataAdapter(List<String[]> data) {
        this.data = data;
    }
    public void setData(List<String[]> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        Log.d("creating","recview");
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_csv_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String[] rowData = data.get(position);
        Log.d("creating", Arrays.toString(rowData));
        if (position == 0) {
            holder.textView1.setText("");
            holder.textView2.setText("");
            holder.textView3.setText("");
            holder.textView4.setText("");
            holder.textView5.setText("");
            return;
        }
        if ("0.0".equals(rowData[1]) && "0.0".equals(rowData[2])) {
            holder.textView1.setText("");
            holder.textView2.setText("");
            holder.textView3.setText("");
            holder.textView4.setText("");
            holder.textView5.setText("");
            return;
        }
        // Adjust position to account for the skipped row
        rowData = data.get(position );
        // Customize this part based on your CSV structure
        holder.textView1.setText("Freq:"+rowData[0]);

    holder.textView2.setText("Impedance:"+rowData[1]);
        holder.textView3.setText("Phase:"+rowData[2]);
        holder.textView4.setText("Positive Terminal:"+rowData[3]);
        holder.textView5.setText("Negative Terminal:"+rowData[4]);
        // Add more TextViews for additional columns
Log.d("textview", String.valueOf(holder.textView1.getText()));
        // Customize based on your data structure
    }

    @Override
    public int getItemCount() {

        Log.d("creating", String.valueOf(data.size()));
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView1;
        TextView textView2;
        TextView textView3;
        TextView textView4;
        TextView textView5;
        // Add more TextViews for additional columns

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.text1);
            textView2 = itemView.findViewById(R.id.text2);
            textView3 = itemView.findViewById(R.id.text3);
            textView4 = itemView.findViewById(R.id.text4);
            textView5 = itemView.findViewById(R.id.text5);
            // Initialize additional TextViews
        }
    }

}

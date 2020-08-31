package elisey.lobanov.weatherreport;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class RecyclerViewAdapterMain extends RecyclerView.Adapter<RecyclerViewAdapterMain.ViewHolder> {

    private String[] times;
    private String[] timeTemps;

    public RecyclerViewAdapterMain(String[] times, String[] timeTemps) {
        this.times = times;
        this.timeTemps = timeTemps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_temp_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.timeText.setText(times[position]);
        holder.timeTempText.setText(timeTemps[position]);
    }

    @Override
    public int getItemCount() {
        if (times == null) return 0;
        return times.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeText;
        TextView timeTempText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.timeTextView);
            timeTempText = itemView.findViewById(R.id.timeTempTextView);
        }
    }
}

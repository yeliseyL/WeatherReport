package elisey.lobanov.weatherreport;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapterHistory extends RecyclerView.Adapter<RecyclerViewAdapterHistory.ViewHolder> {

    private ArrayList<String> cities;
    private ArrayList<String> temps;

    public RecyclerViewAdapterHistory(ArrayList<String> cities, ArrayList<String> temps) {
        this.cities = cities;
        this.temps = temps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.cityText.setText(cities.get(position));
        holder.tempText.setText(temps.get(position));
    }

    @Override
    public int getItemCount() {
        if (cities == null) return 0;
        return cities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView cityText;
        TextView tempText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cityText = itemView.findViewById(R.id.historyCityText);
            tempText = itemView.findViewById(R.id.historyTempText);
        }
    }
}

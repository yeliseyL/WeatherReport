package elisey.lobanov.weatherreport;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import elisey.lobanov.weatherreport.history.HistoryField;
import elisey.lobanov.weatherreport.history.HistorySource;

public class RecyclerViewAdapterHistory extends RecyclerView.Adapter<RecyclerViewAdapterHistory.ViewHolder> {

    private HistorySource historySource;

    public RecyclerViewAdapterHistory(HistorySource historySource) {
        this.historySource = historySource;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        List<HistoryField> historyFields = historySource.getHistoryFields();
        HistoryField historyField = historyFields.get(position);

        holder.dateText.setText(historyField.date);
        holder.cityText.setText(historyField.cityName);
        holder.tempText.setText(historyField.temp);
    }

    @Override
    public int getItemCount() {
        if (historySource == null) return 0;
        return (int) historySource.getCountHistoryFields();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;
        TextView cityText;
        TextView tempText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.historyDateText);
            cityText = itemView.findViewById(R.id.historyCityText);
            tempText = itemView.findViewById(R.id.historyTempText);
        }
    }
}

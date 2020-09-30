package elisey.lobanov.weatherreport;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import elisey.lobanov.weatherreport.history.HistoryDao;
import elisey.lobanov.weatherreport.history.HistorySource;

public class HistoryFragment extends Fragment {
    private RecyclerViewAdapterHistory adapter;
    private HistorySource historySource;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.history_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HistoryDao historyDao = App
                .getInstance()
                .getHistoryDao();
        historySource = new HistorySource(historyDao);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewHistory);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapterHistory(historySource);
        recyclerView.setAdapter(adapter);
    }
}

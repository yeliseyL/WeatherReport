package elisey.lobanov.weatherreport;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapterCity extends RecyclerView.Adapter<RecyclerViewAdapterCity.ViewHolder> {

    private String[] cities;
    private FragmentCallback fragmentCallback;

    public RecyclerViewAdapterCity(String[] cities) {
        this.cities = cities;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cities_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.cityText.setText(cities[position]);
        holder.cityText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CityChooserParcel parcel = CityChooserParcel.getInstance();
                parcel.setCityName(cities[position]);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (cities == null) return 0;
        return cities.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView cityText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cityText = itemView.findViewById(R.id.cityRecyclerTextView);
        }
    }
}

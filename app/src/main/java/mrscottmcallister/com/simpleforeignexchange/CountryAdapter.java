package mrscottmcallister.com.simpleforeignexchange;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by scott on 16-06-05.
 */
public class CountryAdapter extends ArrayAdapter<Country> {

    public CountryAdapter(Context context, ArrayList<Country> countries) {
        super(context, 0, countries);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Country country = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_country, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.countryName);
        TextView tvHome = (TextView) convertView.findViewById(R.id.countryCode);
        // Populate the data into the template view using the data object
        tvName.setText(country.get_name());
        tvHome.setText(country.get_code());
        // Return the completed view to render on screen
        return convertView;
    }

}

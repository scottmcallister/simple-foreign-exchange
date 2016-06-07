package mrscottmcallister.com.simpleforeignexchange;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
        Country country = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_country, parent, false);
        }
        TextView countryName = (TextView) convertView.findViewById(R.id.countryName);
        TextView countryCode = (TextView) convertView.findViewById(R.id.countryCode);
        ImageView flag = (ImageView) convertView.findViewById(R.id.flag);
        countryName.setText(country.get_name());
        countryCode.setText(country.get_code());
        int resId = this.getContext().getResources().getIdentifier(country.get_image(), "drawable", this.getContext().getPackageName().toString());
        flag.setImageResource(resId);
        return convertView;
    }

}

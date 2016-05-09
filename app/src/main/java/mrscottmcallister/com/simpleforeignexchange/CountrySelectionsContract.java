package mrscottmcallister.com.simpleforeignexchange;

import android.provider.BaseColumns;

/**
 * Created by scott on 16-05-09.
 */
public final class CountrySelectionsContract {
    public CountrySelectionsContract(){}

    public static abstract class CountrySelections implements BaseColumns{
        public static final String TABLE_NAME = "selections";
        public static final String COLUMN_NAME_ID = "id";

    }
}

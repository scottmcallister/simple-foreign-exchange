package mrscottmcallister.com.simpleforeignexchange;

/**
 * Created by scott on 16-05-09.
 */
public class Country {
    private int _id;
    private String _name;
    private String _code;
    private String _keywords;

    public Country(){

    }

    public String get_keywords() {
        return _keywords;
    }

    public void set_keywords(String _keywords) {
        this._keywords = _keywords;
    }

    public Country(String name, String code){
        _name = name;
        _code = code;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_code() {
        return _code;
    }

    public void set_code(String _code) {
        this._code = _code;
    }
}
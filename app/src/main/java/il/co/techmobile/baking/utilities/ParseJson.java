package il.co.techmobile.baking.utilities;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;



import il.co.techmobile.baking.modal.Baking;

public class ParseJson {

    private final String json;

    public ParseJson(String jsonData) {
        json = jsonData;
    }

    public Baking[] Parse() {
        JsonElement jsonElement = new JsonParser().parse(json);
        Gson gson = new Gson();
        return gson.fromJson(jsonElement, Baking[].class);
    }
}

package model;

import com.google.gson.Gson;

import java.util.Random;


public class Ingredients {
    private static String[] hashIngredients;

    private String[] ingredients;

    public Ingredients(String[] ingredients) {
        this.ingredients = ingredients;
    }

    public static void initHashIngredients(String jsonResponse) {
        Gson gson = new Gson();
        ListIngredients response = gson.fromJson(jsonResponse, ListIngredients.class);

        if (response.getData() != null) {
            hashIngredients = new String[response.getData().length];
            for (int i = 0; i < response.getData().length; i++) {
                hashIngredients[i] = response.getData()[i].getId();
            }
        } else {
            throw new RuntimeException("Не удалось загрузить данные об ингредиентах.");
        }
    }

    public static Ingredients createListIngredients(int count){
        if (count < 0) {
            throw new IllegalArgumentException("Количество ингредиентов должно быть положительным числом.");
        }

        if (count == 0){
            return new Ingredients(new String[0]);
        }

        Random random = new Random();
        String[] ingredients = new String[count];

        for (int i = 0; i < count; i++) {
            int randomIndex = random.nextInt(hashIngredients.length);
            ingredients[i] = hashIngredients[randomIndex];
        }

        return new Ingredients(ingredients);
    }

    public static Ingredients createListIngredients(String hash){
        String[] ingredients = new String[]{hash};
        return new Ingredients(ingredients);
    }

    public String[] getIngredients() {
        return ingredients;
    }
}

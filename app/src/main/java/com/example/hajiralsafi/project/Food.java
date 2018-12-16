package com.example.hajiralsafi.project;

public class Food {

    public String recipe_name, user_id, recipe_image, prep_time, cook_time, ingredients, instructions;


    public Food() {
    }


    public Food(String recipe_name, String user_id, String recipe_image, String prep_time, String cook_time, String ingredients, String instructions) {
        this.recipe_name = recipe_name;
        this.user_id = user_id;
        this.recipe_image = recipe_image;
        this.prep_time = prep_time;
        this.cook_time = cook_time;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }


    public String getRecipe_name() {
        return recipe_name;
    }

    public void setRecipe_name(String recipe_name) {
        this.recipe_name = recipe_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getRecipe_image() {
        return recipe_image;
    }

    public void setRecipe_image(String recipe_image) {
        this.recipe_image = recipe_image;
    }

    public String getPrep_time() {
        return prep_time;
    }

    public void setPrep_time(String prep_time) {
        this.prep_time = prep_time;
    }

    public String getCook_time() {
        return cook_time;
    }

    public void setCook_time(String cook_time) {
        this.cook_time = cook_time;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
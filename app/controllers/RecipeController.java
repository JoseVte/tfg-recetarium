package controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ObjectNode;
import middleware.Authenticated;
import models.Ingredient;
import models.Recipe;
import models.User;
import models.dao.RecipeDAO;
import models.enums.RecipeDifficulty;
import models.enums.RecipeVisibility;
import models.service.*;
import play.data.Form;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class RecipeController extends AbstractController {
    private static Form<RecipeRequest> recipeForm = Form.form(RecipeRequest.class);
    private static Form<IngredientRequest> ingredientForm = Form.form(IngredientRequest.class);
    private static Form<RatingRequest> ratingForm = Form.form(RatingRequest.class);

    @Override
    @Transactional(readOnly = true)
    public Result list(Integer page, Integer size, String search) {
        List<Recipe> models = RecipeService.paginate(page - 1, size, search, request().username());
        Long count = RecipeService.count(search, request().username());
        String[] routesString = new String[3];
        routesString[0] = routes.RecipeController.list(page - 1, size, search).toString();
        routesString[1] = routes.RecipeController.list(page + 1, size, search).toString();
        routesString[2] = routes.RecipeController.list(page, size, search).toString();

        ObjectNode result = util.Json.generateJsonPaginateObject(models, count, page, size, routesString, !Objects.equals(search, ""));

        return util.Json.jsonResult(response(), ok(result));
    }

    @Override
    @Transactional(readOnly = true)
    public Result get(Integer id) {
        Recipe recipe = RecipeService.find(id);
        if (recipe == null) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found " + id)));
        }
        return util.Json.jsonResult(response(), ok(Json.toJson(recipe)));
    }

    /**
     * Get one recipe by slug
     *
     * @param slug String
     *
     * @return Result
     */
    @Transactional(readOnly = true)
    public Result get(String slug) {
        Recipe recipe = RecipeService.findBySlug(slug);
        if (recipe == null || recipe.isDraft) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found " + slug)));
        }
        if (recipe.isVisible(request().username())) {
            return util.Json.jsonResult(response(), ok(Json.toJson(recipe)));
        }
        return util.Json.jsonResult(response(), forbidden());
    }

    /**
     * Check if the slug exist
     *
     * @param slug String
     *
     * @return Result
     */
    @Transactional(readOnly = true)
    @Security.Authenticated(Authenticated.class)
    public Result checkSlug(String slug, Integer id) {
        Recipe recipe = RecipeService.findBySlugAndId(slug, id);
        if (recipe == null) {
            return util.Json.jsonResult(response(), ok());
        }
        return util.Json.jsonResult(response(), badRequest());
    }

    /**
     * Check if the recipe is mine
     *
     * @param slug String
     *
     * @return Result
     */
    @Transactional(readOnly = true)
    @Security.Authenticated(Authenticated.class)
    public Result isMine(String slug) {
        Recipe recipe = RecipeService.findBySlug(slug);
        if (recipe != null) {
            if (RecipeService.checkOwner(Json.fromJson(Json.parse(request().username()), User.class).email, recipe.id)) {
                return ok();
            }
            return util.Json.jsonResult(response(), unauthorized());
        }
        return util.Json.jsonResult(response(), notFound());
    }

    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result getDraft() {
        User user = Json.fromJson(Json.parse(request().username()), User.class);
        Recipe recipe = RecipeService.getDraft(user);
        if (recipe == null) {
            recipe = RecipeService.createDraft(user);
        }
        return util.Json.jsonResult(response(), ok(Json.toJson(recipe)));
    }

    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result createFromDraft() {
        User user = Json.fromJson(Json.parse(request().username()), User.class);
        Recipe recipe = RecipeService.getDraft(user);
        if (recipe == null) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found draft")));
        }
        recipe.isDraft = false;
        RecipeService.update(recipe);
        return util.Json.jsonResult(response(), ok(Json.toJson(recipe)));
    }

    @Override
    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result create() {
        Form<RecipeRequest> recipe = recipeForm.bindFromRequest();
        if (recipe.hasErrors()) {
            return util.Json.jsonResult(response(), badRequest(recipe.errorsAsJson()));
        }  else if (recipe.get().image_main != null && !FileService.checkOwner(Json.fromJson(Json.parse(request().username()), User.class), recipe.get().image_main)) {
            return util.Json.jsonResult(response(), badRequest(util.Json.generateJsonErrorMessages("The file with ID " + recipe.get().image_main + " isn't in your files")));
        }
        RecipeRequest aux = recipe.get();
        aux.email = Json.fromJson(Json.parse(request().username()), User.class).email;
        Recipe newRecipe = RecipeService.create(aux);
        IngredientService.create(aux.ingredients, newRecipe);
        aux.tags.addAll(TagService.create(aux.new_tags));
        RecipeService.addTags(aux.tags, newRecipe.id);
        RecipeService.syncFiles(aux.files, newRecipe);
        return util.Json.jsonResult(response(), created(Json.toJson(newRecipe)));
    }

    /**
     * Add an ingredient to a recipe
     *
     * @param id Integer
     *
     * @return Result
     */
    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result addIngredient(Integer id) {
        Form<IngredientRequest> ingredient = ingredientForm.bindFromRequest();
        if (ingredient.hasErrors()) {
            return util.Json.jsonResult(response(), badRequest(ingredient.errorsAsJson()));
        } else if (!RecipeService.checkOwner(Json.fromJson(Json.parse(request().username()), User.class).email, id)) {
            return unauthorized();
        }
        Ingredient ingredientModel = RecipeService.addIngredient(id, ingredient.get());
        if (ingredientModel == null) {
            return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages("Something went wrong")));
        }
        return util.Json.jsonResult(response(), ok(Json.toJson(ingredientModel)));
    }

    /**
     * Remove an ingredient to a recipe
     *
     * @param id Integer
     *
     * @return Result
     */
    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result deleteIngredient(Integer id, Integer ingredientId) {
        if (id == null || ingredientId == null) {
            return util.Json.jsonResult(response(), badRequest());
        } else if (!RecipeService.checkOwner(Json.fromJson(Json.parse(request().username()), User.class).email, id)) {
            return unauthorized();
        }
        if (!RecipeService.deleteIngredient(id, ingredientId)) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found " + ingredientId)));
        }
        return util.Json.jsonResult(response(), ok());
    }

    @Override
    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result update(Integer id) {
        Form<RecipeRequest> recipe = recipeForm.bindFromRequest();
        if (recipe.hasErrors()) {
            return util.Json.jsonResult(response(), badRequest(recipe.errorsAsJson()));
        }
        if (!Objects.equals(recipe.get().id, id)) {
            return util.Json.jsonResult(response(), badRequest(util.Json.generateJsonErrorMessages("The IDs don't coincide")));
        } else if (recipe.get().image_main != null && !FileService.checkOwner(Json.fromJson(Json.parse(request().username()), User.class), recipe.get().image_main)) {
            return util.Json.jsonResult(response(), badRequest(util.Json.generateJsonErrorMessages("The file with ID " + recipe.get().image_main + " isn't in your files")));
        } else if (!RecipeService.checkOwner(Json.fromJson(Json.parse(request().username()), User.class).email, id)) {
            return unauthorized();
        }
        RecipeRequest aux = recipe.get();
        Recipe recipeModel = RecipeService.update(recipe.get());
        IngredientService.update(aux.ingredients, recipeModel);
        aux.tags.addAll(TagService.create(aux.new_tags));
        RecipeService.deleteTags(recipeModel.id);
        RecipeService.addTags(aux.tags, recipeModel.id);
        RecipeService.syncFiles(aux.files, recipeModel);
        return util.Json.jsonResult(response(), ok(Json.toJson(recipeModel)));
    }

    @Override
    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result delete(Integer id) {
        if (RecipeService.delete(id, Json.fromJson(Json.parse(request().username()), User.class).email)) {
            return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages("Deleted " + id)));
        }
        return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found " + id)));
    }

    /**
     * Toggle favorite the current user into a recipe
     *
     * @param id Integer
     *
     * @return Result
     */
    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result toggleFav(Integer id) {
        ObjectNode data = Json.newObject();
        boolean fav = RecipeService.addFavorite(Json.fromJson(Json.parse(request().username()), User.class).id, id);
        if (!fav) {
            boolean noFav = RecipeService.deleteFavorite(Json.fromJson(Json.parse(request().username()), User.class).id, id);
            if (!noFav) {
                return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages("Something went wrong")));
            }
            data.put("fav", false);
        } else {
            data.put("fav", true);
        }
        data.put("favorites", RecipeService.countFavorites(id));

        return util.Json.jsonResult(response(), ok(data));
    }

    /**
     * Add rating the current user into a recipe
     *
     * @param id Integer
     *
     * @return Result
     */
    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result rating(Integer id) {
        Form<RatingRequest> rating = ratingForm.bindFromRequest();
        if (rating.hasErrors()) {
            return util.Json.jsonResult(response(), badRequest(rating.errorsAsJson()));
        }
        ObjectNode data = Json.newObject();
        boolean val = RecipeService.addRating(Json.fromJson(Json.parse(request().username()), User.class).id, id, rating.get().rating);
        if (!val) {
            val = RecipeService.updateRating(Json.fromJson(Json.parse(request().username()), User.class).id, id, rating.get().rating);
            if (!val) {
                return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages("Something went wrong")));
            }
            data.put("rating", false);
        } else {
            data.put("rating", true);
        }

        return util.Json.jsonResult(response(), ok(data));
    }

    public static class RatingRequest {
        @Constraints.Required
        public Double rating;
    }

    public static class IngredientRequest {
        public Integer id = null;

        @Constraints.Required
        public String name;
        public String count;

        public IngredientRequest() {
        }
    }

    public static class RecipeRequest {
        public Integer id = null;

        @Constraints.Required
        public String slug;

        @Constraints.Required
        public String title;
        public String steps;

        @Constraints.Required
        public RecipeDifficulty difficulty;

        @Constraints.Required
        public RecipeVisibility visibility;

        @Constraints.Required
        public String duration;
        public Integer num_persons = 0;
        public Integer category_id = null;
        public List<IngredientRequest> ingredients = new ArrayList<IngredientRequest>();
        public List<Integer> tags = new ArrayList<Integer>();
        public List<String> new_tags = new ArrayList<String>();
        public List<Integer> files = new ArrayList<Integer>();
        public boolean is_draft = false;
        public Integer image_main = null;

        @JsonIgnore
        public String email;
        @JsonIgnore
        public Date durationParsed;
        @JsonIgnore
        private RecipeDAO dao;
        @JsonIgnore
        private DateFormat format;

        public RecipeRequest() {
            dao = new RecipeDAO();
            format = new SimpleDateFormat("HH:mm:ss");
        }

        public List<ValidationError> validate() {
            List<ValidationError> errors = new ArrayList<ValidationError>();
            if (id != null && dao.find(id) == null) {
                errors.add(new ValidationError("id", "This recipe doesn't exist"));
            }
            if (!dao.check("slug", slug, id).isEmpty()) {
                errors.add(new ValidationError("slug", "This slug is already used"));
            }
            if (category_id != null && CategoryService.find(category_id) == null) {
                errors.add(new ValidationError("category", "The category doesn't exist"));
            }
            List<Integer> list = TagService.containAll(tags);
            if (!list.isEmpty()) {
                errors.add(new ValidationError("tag", "The tag don't exist: " + list.toString()));
            }
            // TODO Checkear tags e ingredientes
            try {
                durationParsed = format.parse(duration);
            } catch (ParseException e) {
                errors.add(new ValidationError("duration", "Invalid value"));
            }
            return errors.isEmpty() ? null : errors;
        }
    }
}

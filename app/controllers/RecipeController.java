package controllers;

import java.util.List;

import play.mvc.*;
import play.libs.Json;
import play.data.Form;
import play.db.jpa.*;

import models.*;
import models.service.RecipeService;
import views.html.*;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class RecipeController extends AbstractController {
    static Form<Recipe> recipeForm = Form.form(Recipe.class);

    /**
     * Add the content-type json to response
     *
     * @param Result httpResponse
     *
     * @return Result
     */
    public Result jsonResult(Result httpResponse) {
        response().setContentType("application/json; charset=utf-8");
        return httpResponse;
    }

    /**
     * Get the recipes with pagination
     *
     * @param Integer page
     * @param Integer size
     *
     * @return Result
     */
    @Transactional(readOnly = true)
    public Result list(Integer page, Integer size) {
        List<Recipe> models = RecipeService.paginate(page - 1, size);
        Long count = RecipeService.count();
        String[] routesString = new String[3];
        routesString[0] = routes.RecipeController.list(page - 1, size).toString();
        routesString[1] = routes.RecipeController.list(page + 1, size).toString();
        routesString[2] = routes.RecipeController.list(page, size).toString();
        
        ObjectNode result = util.Json.generateJsonPaginateObject(models, count, page, size, routesString);

        return util.Json.jsonResult(response(), ok(result));
    }

    /**
     * Get one recipe by id
     *
     * @param Integer id
     *
     * @return Result
     */
    @Transactional(readOnly = true)
    public Result get(Integer id) {
        Recipe recipe = RecipeService.find(id);
        if (recipe == null) {
            ObjectNode result = Json.newObject();
            result.put("error", "Not found " + id);
            return jsonResult(notFound(result));
        }
        return jsonResult(ok(Json.toJson(recipe)));
    }

    /**
     * Create an recipe with the data of request
     *
     * @return Result
     */
    @Transactional
    public Result create() {
        Form<Recipe> recipe = recipeForm.bindFromRequest();
        if (recipe.hasErrors()) {
            return jsonResult(badRequest(recipe.errorsAsJson()));
        }
        Recipe newRecipe = RecipeService.create(recipe.get());
        return jsonResult(created(Json.toJson(newRecipe)));
    }

    /**
     * Update an recipe with the data of request
     *
     * @param Integer id
     *
     * @return Result
     */
    @Transactional
    public Result update(Integer id) {
        Form<Recipe> recipe = recipeForm.bindFromRequest();
        if (recipe.hasErrors()) {
            return jsonResult(badRequest(recipe.errorsAsJson()));
        }
        Recipe recipeModel = recipe.get();
        recipeModel.id = id;
        recipeModel = RecipeService.update(recipeModel);
        return jsonResult(ok(Json.toJson(recipeModel)));
    }

    /**
     * Delete an recipe by id
     *
     * @param Integer id
     *
     * @return Result
     */
    @Transactional
    public Result delete(Integer id) {
        if (RecipeService.delete(id)) {
            ObjectNode result = Json.newObject();
            result.put("msg", "Deleted " + id);
            return jsonResult(ok(result));
        }
        ObjectNode result = Json.newObject();
        result.put("error", "Not found " + id);
        return jsonResult(notFound(result));
    }
}

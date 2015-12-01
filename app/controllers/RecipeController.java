package controllers;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import models.Recipe;
import models.service.RecipeService;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Result;

public class RecipeController extends AbstractController {
    static Form<Recipe> recipeForm = Form.form(Recipe.class);

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
     * @param String slug
     *
     * @return Result
     */
    @Transactional(readOnly = true)
    public Result get(String slug) {
        Recipe recipe = RecipeService.findBySlug(slug);
        if (recipe == null) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found " + slug)));
        }
        return util.Json.jsonResult(response(), ok(Json.toJson(recipe)));
    }

    @Transactional
    public Result create() {
        Form<Recipe> recipe = recipeForm.bindFromRequest();
        if (recipe.hasErrors()) {
            return util.Json.jsonResult(response(), badRequest(recipe.errorsAsJson()));
        }
        Recipe newRecipe = RecipeService.create(recipe.get());
        return util.Json.jsonResult(response(), created(Json.toJson(newRecipe)));
    }

    @Transactional
    public Result update(Integer id) {
        Form<Recipe> recipe = recipeForm.bindFromRequest();
        if (recipe.hasErrors()) {
            return util.Json.jsonResult(response(), badRequest(recipe.errorsAsJson()));
        }
        Recipe recipeModel = recipe.get();
        if (recipeModel.id != id) {
            return util.Json.jsonResult(response(),
                    badRequest(util.Json.generateJsonErrorMessages("The IDs don't coincide")));
        }
        recipeModel = RecipeService.update(recipeModel);
        return util.Json.jsonResult(response(), ok(Json.toJson(recipeModel)));
    }

    @Transactional
    public Result delete(Integer id) {
        if (RecipeService.delete(id)) {
            return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages("Deleted " + id)));
        }
        return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found " + id)));
    }
}

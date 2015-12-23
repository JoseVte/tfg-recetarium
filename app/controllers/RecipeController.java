package controllers;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ObjectNode;

import middleware.Authenticated;
import models.Recipe;
import models.dao.RecipeDAO;
import models.service.CategoryService;
import models.service.RecipeService;
import play.data.Form;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;

public class RecipeController extends AbstractController {
    static Form<RecipeRequest> recipeForm = Form.form(RecipeRequest.class);

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
    @Security.Authenticated(Authenticated.class)
    public Result create() {
        Form<RecipeRequest> recipe = recipeForm.bindFromRequest();
        if (recipe.hasErrors()) {
            return util.Json.jsonResult(response(), badRequest(recipe.errorsAsJson()));
        }
        RecipeRequest aux = recipe.get();
        aux.email = request().username();
        Recipe newRecipe = RecipeService.create(aux);
        return util.Json.jsonResult(response(), created(Json.toJson(newRecipe)));
    }

    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result update(Integer id) {
        Form<RecipeRequest> recipe = recipeForm.bindFromRequest();
        if (recipe.hasErrors()) {
            return util.Json.jsonResult(response(), badRequest(recipe.errorsAsJson()));
        }
        if (recipe.get().id != id) {
            return util.Json.jsonResult(response(),
                    badRequest(util.Json.generateJsonErrorMessages("The IDs don't coincide")));
        } else if (!RecipeService.checkOwner(request().username(), id)) {
            return unauthorized();
        }
        Recipe recipeModel = RecipeService.update(recipe.get());
        return util.Json.jsonResult(response(), ok(Json.toJson(recipeModel)));
    }

    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result delete(Integer id) {
        if (RecipeService.delete(id, request().username())) {
            return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages("Deleted " + id)));
        }
        return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found " + id)));
    }

    public static class RecipeRequest {
        public Integer    id          = null;

        @Constraints.Required
        public String     slug;

        @Constraints.Required
        public String     title;
        public String     description;
        public Integer    category_id = null;

        @JsonIgnore
        public String email;
        
        @JsonIgnore
        private RecipeDAO dao;

        public RecipeRequest() {
            dao = new RecipeDAO();
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
            return errors.isEmpty() ? null : errors;
        }
    }
}

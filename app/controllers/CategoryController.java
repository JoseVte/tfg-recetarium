package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import middleware.Admin;
import models.Category;
import models.service.CategoryService;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.List;
import java.util.Objects;

public class CategoryController extends Controller {
    private Form<CategoryRequest> formModel = Form.form(CategoryRequest.class);

    /**
     * Get the model with pagination
     *
     * @return Result
     */
    @Transactional(readOnly = true)
    public Result list(Integer page, Integer size, String search, String order) {
        if (!Boolean.valueOf(request().getHeader("Accept-Pagination"))) {
            List<Category> categories = CategoryService.all();
            return util.Json.jsonResult(response(), ok(Json.toJson(categories)));
        }

        String orderBy = order;
        if (order.equals("recipes") || order.equals("-recipes")) {
            orderBy = "categories.recipes.size";
            if (order.startsWith("-")) {
                orderBy = "-categories.recipes.size";
            }
        }
        order = orderBy;
        List<Category> models = CategoryService.paginate(page - 1, size, search, order);
        Long count = CategoryService.count(search);
        String[] routesString = new String[3];
        routesString[0] = routes.CategoryController.list(page - 1, size, search, order).toString();
        routesString[1] = routes.CategoryController.list(page + 1, size, search, order).toString();
        routesString[2] = routes.CategoryController.list(page, size, search, order).toString();

        ObjectNode result = util.Json.generateJsonPaginateObject(models, count, page, size, routesString, !Objects.equals(search, ""));

        return util.Json.jsonResult(response(), ok(result));
    }

    @Transactional(readOnly = true)
    public Result get(Integer id) {
        Category category = CategoryService.find(id);
        if (category == null) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages(Messages.get("error.not-found", Messages.get("article.female-single"), Messages.get("field.category"), id))));
        }
        return util.Json.jsonResult(response(), ok(Json.toJson(category)));
    }

    @Transactional
    @Security.Authenticated(Admin.class)
    public Result create() {
        Form<CategoryRequest> category = formModel.bindFromRequest();
        if (category.hasErrors()) {
            return util.Json.jsonResult(response(), badRequest(category.errorsAsJson()));
        }
        try {
            CategoryRequest categoryRequest = category.get();
            Category newCategory = CategoryService.create(new Category(categoryRequest.text));
            return util.Json.jsonResult(response(), created(Json.toJson(newCategory)));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages(Messages.get("error.server"))));
        }
    }

    @Transactional
    @Security.Authenticated(Admin.class)
    public Result update(Integer id) {
        Form<CategoryRequest> category = formModel.bindFromRequest();
        if (category.hasErrors()) {
            return util.Json.jsonResult(response(), badRequest(category.errorsAsJson()));
        }
        if (!Objects.equals(category.get().id, id)) {
            return util.Json.jsonResult(response(), badRequest(util.Json.generateJsonErrorMessages(Messages.get("error.field-equals", Messages.get("article.male-plural"), "IDs"))));
        }
        Category categoryModel = CategoryService.find(id);
        if (categoryModel == null) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages(Messages.get("error.not-found", Messages.get("article.female-single"), Messages.get("field.category"), id))));
        }
        categoryModel.text = category.get().text;
        categoryModel = CategoryService.update(categoryModel);
        return util.Json.jsonResult(response(), ok(Json.toJson(categoryModel)));
    }

    @Transactional
    @Security.Authenticated(Admin.class)
    public Result delete(Integer id) {
        if (CategoryService.delete(id)) {
            return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages(Messages.get("info.delete", Messages.get("article.female-single"), Messages.get("field.category"), id))));
        }
        return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages(Messages.get("error.not-found", Messages.get("article.female-single"), Messages.get("field.category"), id))));
    }

    @Transactional
    @Security.Authenticated(Admin.class)
    public Result deleteMultiple(scala.collection.Seq<Integer> ids) {
        Integer deleted = CategoryService.delete(scala.collection.JavaConversions.seqAsJavaList(ids));
        return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages(Messages.get("info.delete-multiple", deleted, Messages.get("field.categories")))));
    }

    public static class CategoryRequest {
        public Integer id = null;

        @Constraints.Required
        public String text;

        public CategoryRequest() {
        }
    }
}

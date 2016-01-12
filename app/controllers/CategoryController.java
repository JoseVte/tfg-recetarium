package controllers;

import java.util.List;

import models.Category;
import models.service.CategoryService;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class CategoryController extends Controller {
    /**
     * Get the model with pagination
     *
     * @return Result
     */
    @Transactional(readOnly = true)
    public Result list() {
        List<Category> categories = CategoryService.all();
        return util.Json.jsonResult(response(), ok(Json.toJson(categories)));
    }
}

package controllers;

import java.util.List;

import models.Tag;
import models.service.TagService;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class TagController extends Controller {
    /**
     * Get the model with pagination
     *
     * @return Result
     */
    @Transactional(readOnly = true)
    public Result list(String search) {
        List<Tag> tags = TagService.search(search);
        return util.Json.jsonResult(response(), ok(Json.toJson(tags)));
    }
}

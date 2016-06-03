package controllers;

import models.Tag;
import models.service.TagService;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

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

    /**
     * Get a tag by id
     *
     * @param  id Integer
     * @return   Result
     */
    @Transactional(readOnly = true)
    public Result get(Integer id) {
        Tag tag = TagService.find(id);
        if (tag == null) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages(Messages.get("error.not-found", Messages.get("article.female-single"), Messages.get("field.tag"), id))));
        }
        return util.Json.jsonResult(response(), ok(Json.toJson(tag)));
    }
}

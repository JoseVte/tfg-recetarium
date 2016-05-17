package controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ObjectNode;
import middleware.Authenticated;
import models.Comment;
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
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import providers.PusherService;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class RecipeController extends AbstractCrudController {
    private static Form<RecipeRequest> recipeForm = Form.form(RecipeRequest.class);
    private static Form<IngredientRequest> ingredientForm = Form.form(IngredientRequest.class);
    private static Form<RatingRequest> ratingForm = Form.form(RatingRequest.class);
    private static Form<CommentRequest> commentForm = Form.form(CommentRequest.class);
    private final PusherService pusher;

    @Inject
    public RecipeController() {
        this.pusher = new PusherService();
    }

    @Override
    @Transactional(readOnly = true)
    public Result list(Integer page, Integer size, String search, String order) {
        return list(page, size, search, order, null);
    }

    @Transactional(readOnly = true)
    public Result list(Integer page, Integer size, String search, String order, scala.collection.Seq<Integer> tags) {
        order = order.replace('+', ' ');
        List<Recipe> models = RecipeService.paginate(page - 1, size, search, request().username(), order, scala.collection.JavaConversions.seqAsJavaList(tags));
        Long count = RecipeService.count(search, request().username(), scala.collection.JavaConversions.seqAsJavaList(tags));
        String[] routesString = new String[3];
        routesString[0] = routes.RecipeController.list(page - 1, size, search, order, tags).toString();
        routesString[1] = routes.RecipeController.list(page + 1, size, search, order, tags).toString();
        routesString[2] = routes.RecipeController.list(page, size, search, order, tags).toString();

        ObjectNode result = util.Json.generateJsonPaginateObject(models, count, page, size, routesString, !Objects.equals(search, ""));

        return util.Json.jsonResult(response(), ok(result));
    }

    @Transactional(readOnly = true)
    public Result listByUser(Integer id, Integer page, Integer size) {
        List<Recipe> models = RecipeService.listByUser(id, request().username(), page - 1, size);
        Long count = RecipeService.countByUser(id, request().username());

        String[] routesString = new String[3];
        routesString[0] = routes.RecipeController.listByUser(id, page - 1, size).toString();
        routesString[1] = routes.RecipeController.listByUser(id, page + 1, size).toString();
        routesString[2] = routes.RecipeController.listByUser(id, page, size).toString();

        ObjectNode result = util.Json.generateJsonPaginateObject(models, count, page, size, routesString, false);

        return util.Json.jsonResult(response(), ok(result));
    }

    @Transactional(readOnly = true)
    public Result listFavByUser(Integer id, Integer page, Integer size) {
        List<Recipe> models = RecipeService.listByUser(id, request().username(), page - 1, size, true);
        Long count = RecipeService.countByUser(id, request().username(), true);

        String[] routesString = new String[3];
        routesString[0] = routes.RecipeController.listByUser(id, page - 1, size).toString();
        routesString[1] = routes.RecipeController.listByUser(id, page + 1, size).toString();
        routesString[2] = routes.RecipeController.listByUser(id, page, size).toString();

        ObjectNode result = util.Json.generateJsonPaginateObject(models, count, page, size, routesString, false);

        return util.Json.jsonResult(response(), ok(result));
    }

    @Override
    @Transactional(readOnly = true)
    public Result get(Integer id) {
        Recipe recipe = RecipeService.find(id);
        if (recipe == null) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages(Messages.get("error.not-found", Messages.get("article.female-single"), Messages.get("field.recipe"), id))));
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
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages(Messages.get("error.not-found", Messages.get("article.female-single"), Messages.get("field.recipe"), slug))));
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
            UserService.addRecipeCount(user);
        }
        return util.Json.jsonResult(response(), ok(Json.toJson(recipe)));
    }

    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result createFromDraft() {
        User user = Json.fromJson(Json.parse(request().username()), User.class);
        Recipe recipe = RecipeService.getDraft(user);
        if (recipe == null) {
            return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages(Messages.get("error.not-found", Messages.get("article.male-single"), Messages.get("field.draft"), ""))));
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
            return util.Json.jsonResult(response(), badRequest(util.Json.generateJsonErrorMessages(Messages.get("error.file-not-found", recipe.get().image_main))));
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
            return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages(Messages.get("error.server"))));
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
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages(Messages.get("error.not-found", Messages.get("article.male-single"), Messages.get("field.ingredient"), ingredientId))));
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
            return util.Json.jsonResult(response(), badRequest(util.Json.generateJsonErrorMessages(Messages.get("error.field-equals", Messages.get("article.male-plural"), "IDs"))));
        } else if (recipe.get().image_main != null && !FileService.checkOwner(Json.fromJson(Json.parse(request().username()), User.class), recipe.get().image_main)) {
            return util.Json.jsonResult(response(), badRequest(util.Json.generateJsonErrorMessages(Messages.get("error.file-not-found", recipe.get().image_main))));
        } else if (!RecipeService.checkOwner(Json.fromJson(Json.parse(request().username()), User.class).email, id)) {
            return unauthorized();
        }
        RecipeRequest aux = recipe.get();
        aux.email = RecipeService.find(id).user.email;
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
            return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages(Messages.get("info.delete", Messages.get("article.female-single"), Messages.get("field.recipe"), id))));
        }
        return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages(Messages.get("error.not-found", Messages.get("article.female-single"), Messages.get("field.recipe"), id))));
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
                return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages(Messages.get("error.server"))));
            }
            data.put("rating", false);
        } else {
            data.put("rating", true);
        }

        return util.Json.jsonResult(response(), ok(data));
    }

    @Transactional(readOnly = true)
    public Result getReplies(Integer id, Integer commentId) {
        List<Comment> comments = CommentService.getReplies(id, commentId);
        return util.Json.jsonResult(response(), ok(Json.toJson(comments)));
    }

    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result createComment(Integer id, Integer commentId) {
        User user = Json.fromJson(Json.parse(request().username()), User.class);
        Form<CommentRequest> comment = commentForm.bindFromRequest();
        if (comment.hasErrors()) {
            return util.Json.jsonResult(response(), badRequest(comment.errorsAsJson()));
        }
        Recipe recipe = RecipeService.find(id);
        if (recipe == null) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages(Messages.get("error.not-found", Messages.get("article.male-single"), Messages.get("field.recipe"), id))));
        }
        Comment commentModel = new Comment(comment.get().text, user, recipe,null);
        if (commentId == null) {
            commentModel = CommentService.create(commentModel);
            pusher.notificateComment(recipe, user);
        } else {
            Comment commentParent = CommentService.find(commentId);
            commentModel.parent = commentParent;
            commentModel = CommentService.create(commentModel);
            pusher.notificateComment(recipe, user);
            pusher.notificateReply(recipe, user, commentParent.user);
        }

        return util.Json.jsonResult(response(), ok(Json.toJson(commentModel)));
    }

    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result updateComment(Integer id, Integer commentId) {
        User user = Json.fromJson(Json.parse(request().username()), User.class);
        Form<CommentRequest> comment = commentForm.bindFromRequest();
        if (comment.hasErrors()) {
            return util.Json.jsonResult(response(), badRequest(comment.errorsAsJson()));
        }
        Comment commentModel = CommentService.find(commentId);
        if (commentModel == null) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages(Messages.get("error.not-found", Messages.get("article.male-single"), Messages.get("field.comment"), commentId))));
        }
        if (!Objects.equals(commentModel.user.id, user.id)) {
            return util.Json.jsonResult(response(), badRequest(util.Json.generateJsonErrorMessages(Messages.get("error.comment-owner", Messages.get("action.edit")))));
        }
        if (!Objects.equals(commentModel.recipe.id, id)) {
            return util.Json.jsonResult(response(), badRequest(util.Json.generateJsonErrorMessages(Messages.get("error.field-equals", Messages.get("article.male-plural"), "IDs"))));
        }
        commentModel.text = comment.get().text;
        commentModel = CommentService.update(commentModel);
        return util.Json.jsonResult(response(), ok(Json.toJson(commentModel)));
    }

    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result deleteComment(Integer id, Integer commentId) {
        User user = Json.fromJson(Json.parse(request().username()), User.class);
        Comment commentModel = CommentService.find(commentId);
        if (commentModel == null) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages(Messages.get("error.not-found", Messages.get("article.male-single"), Messages.get("field.comment"), commentId))));
        }
        if (!Objects.equals(commentModel.user.id, user.id) && !user.isAdmin()) {
            return util.Json.jsonResult(response(), badRequest(util.Json.generateJsonErrorMessages(Messages.get("error.comment-owner", Messages.get("action.delete")))));
        }
        if (CommentService.delete(commentModel)) {
            return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages(Messages.get("info.delete", Messages.get("article.male-single"), Messages.get("field.comment"), commentId))));
        }
        return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages(Messages.get("error.not-found", Messages.get("article.female-single"), Messages.get("field.recipe"), id))));
    }

    public static class RatingRequest {
        @Constraints.Required
        public Double rating;
    }

    public static class CommentRequest {
        @Constraints.Required
        public String text;
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
                errors.add(new ValidationError("id", Messages.get("error.no-existing", Messages.get("article.male-single"), "ID", id)));
            }
            if (!dao.check("slug", slug, id).isEmpty()) {
                errors.add(new ValidationError("slug", Messages.get("error.existing", "slug")));
            }
            if (category_id != null && CategoryService.find(category_id) == null) {
                errors.add(new ValidationError("category", Messages.get("error.no-existing", Messages.get("article.female-single"), Messages.get("field.category"), category_id)));
            }
            List<Integer> list = TagService.containAll(new ArrayList<>(tags));
            if (!list.isEmpty()) {
                errors.add(new ValidationError("tag", Messages.get("error.no-existing", Messages.get("article.female-plural"), Messages.get("field.tags"), list.toString())));
            }
            // TODO Checkear tags e ingredientes
            try {
                durationParsed = format.parse(duration);
            } catch (ParseException e) {
                errors.add(new ValidationError("duration", Messages.get("error.invalid value", duration)));
            }
            return errors.isEmpty() ? null : errors;
        }
    }
}

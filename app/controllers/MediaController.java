package controllers;

import java.io.File;

import middleware.Authenticated;
import models.Media;
import models.Recipe;
import models.service.MediaService;
import models.service.RecipeService;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security;

public class MediaController extends Controller {

    public static char FILE_SEPARARTOR = File.separatorChar;
    
    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result upload(Integer idRecipe) {
        MultipartFormData body = request().body().asMultipartFormData();
        FilePart file = body.getFile("file");
        
        if (file != null) {
            Recipe recipe = RecipeService.find(idRecipe);
            String fileName = file.getFilename();
            String path = "public" + FILE_SEPARARTOR + "files" + FILE_SEPARARTOR + idRecipe;
            File dir = new File(path);
            
            // Create the dir if not exists
            if (!dir.exists() && !dir.mkdirs()) {
                return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages("Error uploading the file.")));
            }
            
            // Update if the file is duplicated
            if (!MediaService.check(idRecipe, fileName, null).isEmpty()) {
                return TODO;
            }
            File fileStored = file.getFile();
            fileStored.renameTo(new File(path, fileName));

            Media media = new Media(fileName, recipe);
            MediaService.create(media);
            
            return ok("File uploaded");
        } else {
            return badRequest();
        }
    }


    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result delete(Integer id) {
        if (MediaService.delete(id, request().username())) {
            return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages("Deleted " + id)));
        }
        return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found " + id)));
    }

}

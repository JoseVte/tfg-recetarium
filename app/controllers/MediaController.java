package controllers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.FileUtils;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxRequestConfig;

import middleware.Authenticated;
import models.Media;
import models.Recipe;
import models.service.MediaService;
import models.service.RecipeService;
import play.Play;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security;


public class MediaController extends Controller {
	static final String ACCESS_TOKEN = Play.application().configuration().getString("dropbox.access.token");

    @Transactional(readOnly = true)
    public Result get(Integer idRecipe, String file) {
        try {
        	String path = "public" + MediaService.FILE_SEPARARTOR + "files" + MediaService.FILE_SEPARARTOR + idRecipe;
            File dir = new File(path);
        	File f = new File(path + MediaService.FILE_SEPARARTOR + file);
    		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
    		if (!dir.exists() && !dir.mkdirs()) {
                return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages("Something went wrong")));
            }

        	if (Play.isProd()) {
	        	DbxRequestConfig config = new DbxRequestConfig("Recetarium", Locale.getDefault().toString());
	        	DbxClient client = new DbxClient(config, ACCESS_TOKEN);

	        	FileOutputStream output = new FileOutputStream(f);
	            client.getFile("/" + idRecipe + "/" + file, null, output);
	            output.close();
        	}
        	return ok(FileUtils.readFileToByteArray(f)).as(mimeTypesMap.getContentType(f));
        } catch (Exception e) {
        	System.err.println(e);
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found file: " + file)));
        }
    }

    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result upload(Integer idRecipe) {
        MultipartFormData body = request().body().asMultipartFormData();
        FilePart file = body.getFile("file");

        if (file != null) {
            String fileName = file.getFilename();
            String path = "public" + MediaService.FILE_SEPARARTOR + "files" + MediaService.FILE_SEPARARTOR + idRecipe;
            File dir = new File(path);
            Recipe recipe = RecipeService.findByOwner(request().username(), idRecipe);
            Media media = new Media(fileName, recipe);

            // Check if recipe exist
            if (recipe == null) {
                return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found " + idRecipe)));
            }

            // Create the dir if not exists
            if (!dir.exists() && !dir.mkdirs()) {
                return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages("Error uploading the file")));
            }

            File fileStored = file.getFile();
            fileStored.renameTo(new File(path, fileName));

            // Update if the file is duplicated
            Media exist =  MediaService.find(idRecipe, fileName);
            if (exist != null) {
                MediaService.update(exist);
            } else {
                MediaService.create(media);
            }

            return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages("File '" + fileName + "' uploaded")));
        } else {
            return util.Json.jsonResult(response(), badRequest(util.Json.generateJsonErrorMessages("The file is required")));
        }
    }


    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result delete(Integer id) {
        Media media = MediaService.find(id);
        if (media != null && MediaService.delete(id, request().username())) {
            try {
                String pathDir = "public" + MediaService.FILE_SEPARARTOR + "files" + MediaService.FILE_SEPARARTOR + media.recipe.id;
                Path path = Paths.get(pathDir + media.filename);
                Files.delete(path);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages("Error deleting the file")));
            }
            return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages("Deleted file " + id)));
        }
        return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found file " + id)));
    }

}

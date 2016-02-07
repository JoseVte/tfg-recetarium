package controllers;

import com.dropbox.core.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import middleware.Authenticated;
import models.Media;
import models.Recipe;
import models.User;
import models.service.MediaService;
import models.service.RecipeService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import play.Play;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security;
import util.NullModelException;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MediaController extends Controller {
    private static final String ACCESS_TOKEN = Play.application().configuration().getString("dropbox.access.token");
    private static final String APP_NAME = Play.application().configuration().getString("dropbox.app.name");
    private String[] defaultValue = {"false"};

    private Result getFile(Media media) throws IOException, DbxException, NullModelException {
        if (media == null) {
            throw new NullModelException();
        }
        if (Play.isProd()) {
            DbxRequestConfig config = new DbxRequestConfig(APP_NAME, Locale.getDefault().toString());
            DbxClient client = new DbxClient(config, ACCESS_TOKEN);
            return redirect(client.createTemporaryDirectUrl("/" + media.recipe.id + "/" + media.filename).url);
        }

        if (Play.isDev()) {
            String path = "public" + MediaService.FILE_SEPARARTOR + "files" + MediaService.FILE_SEPARARTOR + media.recipe.id;
            File dir = new File(path);
            File f = new File(path + MediaService.FILE_SEPARARTOR + URLDecoder.decode(media.filename, "UTF-8"));
            MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
            if (!dir.exists() && !dir.mkdirs()) {
                return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages("Something went wrong")));
            }

            return ok(FileUtils.readFileToByteArray(f)).as(mimeTypesMap.getContentType(f));
        }

        return ok();
    }

    private Media uploadFileToDropbox(DbxClient client, FilePart file, Recipe recipe, String fileName, boolean isMain) {
        FileInputStream inputStream = null;
        DbxEntry fileDropbox;
        try {
            inputStream = new FileInputStream(file.getFile());
            DbxWriteMode mode = isMain ? DbxWriteMode.force() : DbxWriteMode.add();
            fileDropbox = client.uploadFile("/" + recipe.id + "/" + fileName, mode, file.getFile().length(), inputStream);
        } catch (DbxException | IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Media(fileDropbox.name, recipe);
    }

    private Media uploadFileToLocal(FilePart file, Recipe recipe, String fileName, boolean isMain) {
        String path = "public" + MediaService.FILE_SEPARARTOR + "files" + MediaService.FILE_SEPARARTOR + recipe.id;
        File dir = new File(path);

        // Create the dir if not exists
        if (!dir.exists() && !dir.mkdirs()) {
            return null;
        }

        if (!isMain) {
            File fileExist = new File(path, file.getFilename());
            File fileStored = file.getFile();
            boolean exists = false;
            int i = 0;
            while (!exists) {
                if (!fileExist.exists()) {
                    fileStored.renameTo(fileExist);
                    exists = true;
                } else {
                    i++;
                    fileName = FilenameUtils.getName(file.getFilename()) + "_" + i + "." + FilenameUtils.getExtension(file.getFilename());
                    fileExist = new File(path, fileName);
                }
            }
            return new Media(fileExist.getName(), recipe);
        } else {
            File fileExist = new File(path, fileName);
            File fileStored = file.getFile();
            fileStored.renameTo(fileExist);
            return new Media(fileExist.getName(), recipe);
        }
    }

    private Result uploadFile(Recipe recipe, MultipartFormData body, FilePart file) {
        boolean isMain = Boolean.parseBoolean(body.asFormUrlEncoded().getOrDefault("is_main", defaultValue)[0]);
        String fileName = isMain ? "main." + FilenameUtils.getExtension(file.getFilename()) : file.getFilename();
        Media media;
        if (Play.isProd()) {
            DbxRequestConfig config = new DbxRequestConfig(APP_NAME, Locale.getDefault().toString());
            DbxClient client = new DbxClient(config, ACCESS_TOKEN);
            media = uploadFileToDropbox(client, file, recipe, fileName, isMain);
        } else {
            media = uploadFileToLocal(file, recipe, fileName, isMain);
        }
        if (media != null) {
            if (isMain) {
                Media aux = MediaService.find(recipe.id, fileName);
                if (aux != null ) {
                    MediaService.update(aux);
                    return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages("File '" + fileName + "' uploaded")));
                }
            }
            MediaService.create(media);
            return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages("File '" + fileName + "' uploaded")));
        }
        return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages("Error uploading the file")));
    }

    private Result uploadFiles(Recipe recipe, List<FilePart> files) {
        List<ObjectNode> msg = new ArrayList<>();
        Media media;
        if (Play.isProd()) {
            DbxRequestConfig config = new DbxRequestConfig(APP_NAME, Locale.getDefault().toString());
            DbxClient client = new DbxClient(config, ACCESS_TOKEN);
            for (FilePart file : files) {
                media = uploadFileToDropbox(client, file, recipe, file.getFilename(), false);
                if (media == null) {
                    msg.add(util.Json.generateJsonErrorMessages("Error uploading the file: " + file.getFilename()));
                } else {
                    MediaService.create(media);
                    msg.add(util.Json.generateJsonInfoMessages("File '" + media.filename + "' uploaded"));
                }
            }
        } else if (Play.isDev()) {
            for (FilePart file : files) {
                media = uploadFileToLocal(file, recipe, file.getFilename(), false);
                if (media == null) {
                    msg.add(util.Json.generateJsonErrorMessages("Error uploading the file: " + file.getFilename()));
                } else {
                    MediaService.create(media);
                    msg.add(util.Json.generateJsonInfoMessages("File '" + media.filename + "' uploaded"));
                }
            }
        }

        return util.Json.jsonResult(response(), ok(Json.toJson(msg)));
    }

    private void deleteFile(Media media) throws DbxException, IOException {
        if (Play.isProd()) {
            DbxRequestConfig config = new DbxRequestConfig(APP_NAME, Locale.getDefault().toString());
            DbxClient client = new DbxClient(config, ACCESS_TOKEN);
            client.delete("/" + media.recipe.id + "/" + media.filename);
        } else if (Play.isDev()) {
            String pathDir = "public" + MediaService.FILE_SEPARARTOR + "files" + MediaService.FILE_SEPARARTOR + media.recipe.id + MediaService.FILE_SEPARARTOR;
            Path path = Paths.get(pathDir + media.filename);
            Files.delete(path);
        }
    }

    @Transactional(readOnly = true)
    public Result getById(Integer idRecipe, Integer id) {
        Media media = MediaService.find(idRecipe, id);
        try {
            return getFile(media);
        } catch (DbxException | IOException e) {
            e.printStackTrace();
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found file: [id: " + id + " recipe: " + idRecipe + "]")));
        } catch (NullModelException e) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found file: [id: " + id + " recipe: " + idRecipe + "]")));
        }
    }

    @Transactional(readOnly = true)
    public Result getByFile(Integer idRecipe, String file) {
        Media media = MediaService.find(idRecipe, file);
        try {
            return getFile(media);
        } catch (DbxException | IOException e) {
            e.printStackTrace();
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found file: [file: " + file + " recipe: " + idRecipe + "]")));
        } catch (NullModelException e) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found file: [file: " + file + " recipe: " + idRecipe + "]")));
        }
    }

    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result upload(Integer idRecipe) {
        MultipartFormData body = request().body().asMultipartFormData();
        boolean isMultiple = Boolean.parseBoolean(body.asFormUrlEncoded().getOrDefault("is_multiple", defaultValue)[0]);

        Recipe recipe = RecipeService.findByOwner(Json.fromJson(Json.parse(request().username()), User.class).email, idRecipe);
        // Check if recipe exist
        if (recipe == null) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found " + idRecipe)));
        }

        if (isMultiple) {
            List<FilePart> files = body.getFiles();
            if (files.isEmpty()) {
                return util.Json.jsonResult(response(), badRequest(util.Json.generateJsonErrorMessages("No files have been included in the request.")));
            } else {
                return uploadFiles(recipe, files);
            }
        } else {
            FilePart file = body.getFile("file");
            if (file != null) {
                return uploadFile(recipe, body, file);
            } else {
                return util.Json.jsonResult(response(), badRequest(util.Json.generateJsonErrorMessages("The file is required")));
            }
        }
    }

    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result deleteById(Integer idRecipe, Integer id) {
        Media media = MediaService.find(idRecipe, id);
        if (media != null && MediaService.delete(id, Json.fromJson(Json.parse(request().username()), User.class).email)) {
            try {
                deleteFile(media);
            } catch (IOException | DbxException e) {
                System.err.println(e.getMessage());
                return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages("Error deleting the file")));
            }
            return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages("Deleted file " + id)));
        }
        return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found file " + id)));
    }

    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result deleteByFile(Integer idRecipe, String file) {
        Media media = MediaService.find(idRecipe, file);
        if (media != null && MediaService.delete(media.id, Json.fromJson(Json.parse(request().username()), User.class).email)) {
            try {
                deleteFile(media);
            } catch (IOException | DbxException e) {
                System.err.println(e.getMessage());
                return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages("Error deleting the file " + file)));
            }
            return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages("Deleted file " + file)));
        }
        return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found file " + file)));
    }

}

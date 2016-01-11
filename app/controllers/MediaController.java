package controllers;

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

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWriteMode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import middleware.Authenticated;
import models.Media;
import models.Recipe;
import models.User;
import models.service.MediaService;
import models.service.RecipeService;
import play.Play;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security;

public class MediaController extends Controller {
    static final String ACCESS_TOKEN = Play.application().configuration().getString("dropbox.access.token");
    static final String APP_NAME     = Play.application().configuration().getString("dropbox.app.name");

    @Transactional(readOnly = true)
    public Result get(Integer idRecipe, String file) {
        try {
            if (Play.isProd()) {
                DbxRequestConfig config = new DbxRequestConfig(APP_NAME, Locale.getDefault().toString());
                DbxClient client = new DbxClient(config, ACCESS_TOKEN);
                return redirect(client.createTemporaryDirectUrl("/" + idRecipe + "/" + file).url);
            }

            String path = "public" + MediaService.FILE_SEPARARTOR + "files" + MediaService.FILE_SEPARARTOR + idRecipe;
            File dir = new File(path);
            File f = new File(path + MediaService.FILE_SEPARARTOR + URLDecoder.decode(file, "UTF-8"));
            MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
            if (!dir.exists() && !dir.mkdirs()) {
                return util.Json.jsonResult(response(),
                        internalServerError(util.Json.generateJsonErrorMessages("Something went wrong")));
            }

            return ok(FileUtils.readFileToByteArray(f)).as(mimeTypesMap.getContentType(f));
        } catch (Exception e) {
            e.printStackTrace();
            return util.Json.jsonResult(response(),
                    notFound(util.Json.generateJsonErrorMessages("Not found file: " + file)));
        }
    }

    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result upload(Integer idRecipe) {
        FileInputStream inputStream = null;
        DbxRequestConfig config = new DbxRequestConfig(APP_NAME, Locale.getDefault().toString());
        DbxClient client = new DbxClient(config, ACCESS_TOKEN);
        MultipartFormData body = request().body().asMultipartFormData();
        String[] defaultValue = { "false" };
        boolean isMain = Boolean.parseBoolean(body.asFormUrlEncoded().getOrDefault("is_main", defaultValue)[0]);
        boolean isMultiple = Boolean.parseBoolean(body.asFormUrlEncoded().getOrDefault("is_multiple", defaultValue)[0]);

        Recipe recipe = RecipeService.findByOwner(Json.fromJson(Json.parse(request().username()), User.class).email,
                idRecipe);
        // Check if recipe exist
        if (recipe == null) {
            return util.Json.jsonResult(response(),
                    notFound(util.Json.generateJsonErrorMessages("Not found " + idRecipe)));
        }

        if (isMultiple) {
            List<FilePart> files = body.getFiles();
            if (files.isEmpty()) {
                return util.Json.jsonResult(response(),
                        badRequest(util.Json.generateJsonErrorMessages("No files have been included in the request.")));
            } else {
                List<ObjectNode> msg = new ArrayList<ObjectNode>();
                DbxEntry.File fileDropbox = null;
                Media media = null;
                for (FilePart file : files) {
                    if (Play.isProd()) {
                        try {
                            inputStream = new FileInputStream(file.getFile());
                            fileDropbox = client.uploadFile("/" + idRecipe + "/" + file.getFilename(),
                                    DbxWriteMode.add(), file.getFile().length(), inputStream);
                            msg.add(util.Json.generateJsonInfoMessages("File '" + fileDropbox.name + "' uploaded"));
                            media = new Media(fileDropbox.name, recipe);
                        } catch (DbxException | IOException e) {
                            msg.add(util.Json.generateJsonErrorMessages("Error uploading the file: " + e.getMessage()));
                        } finally {
                            try {
                                if (inputStream != null) inputStream.close();
                            } catch (IOException e) {
                            }
                        }
                    } else {
                        String path = "public" + MediaService.FILE_SEPARARTOR + "files" + MediaService.FILE_SEPARARTOR
                                + idRecipe;
                        File dir = new File(path);

                        // Create the dir if not exists
                        if (!dir.exists() && !dir.mkdirs()) {
                            msg.add(util.Json.generateJsonErrorMessages("Error uploading the file"));
                        } else {
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
                                    fileExist = new File(path, FilenameUtils.getName(file.getFilename()) + "_" + i + "."
                                            + FilenameUtils.getExtension(file.getFilename()));
                                }
                            }
                            msg.add(util.Json.generateJsonInfoMessages("File '" + fileExist.getName() + "' uploaded"));
                            media = new Media(fileExist.getName(), recipe);
                        }
                    }
                    if (media != null) {
                        MediaService.create(media);
                    }
                }

                return util.Json.jsonResult(response(), ok(Json.toJson(msg)));
            }
        } else {
            FilePart file = body.getFile("file");
            if (file != null) {
                String fileName = isMain ? "main." + FilenameUtils.getExtension(file.getFilename())
                        : file.getFilename();
                Media media = new Media(fileName, recipe);
                if (Play.isProd()) {
                    try {
                        inputStream = new FileInputStream(file.getFile());
                        DbxWriteMode mode = isMain ? DbxWriteMode.force() : DbxWriteMode.add();
                        client.uploadFile("/" + idRecipe + "/" + fileName, mode ,
                                file.getFile().length(), inputStream);
                    } catch (DbxException | IOException e) {
                        e.printStackTrace();
                        return util.Json.jsonResult(response(),
                                internalServerError(util.Json.generateJsonErrorMessages("Error uploading the file")));
                    } finally {
                        try {
                            if (inputStream != null) inputStream.close();
                        } catch (IOException e) {
                        }
                    }
                } else {
                    String path = "public" + MediaService.FILE_SEPARARTOR + "files" + MediaService.FILE_SEPARARTOR
                            + idRecipe;
                    File dir = new File(path);

                    // Create the dir if not exists
                    if (!dir.exists() && !dir.mkdirs()) {
                        return util.Json.jsonResult(response(),
                                internalServerError(util.Json.generateJsonErrorMessages("Error uploading the file")));
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
                        media = new Media(fileExist.getName(), recipe);
                    } else {
                        File fileStored = file.getFile();
                        fileStored.renameTo(new File(path, fileName));
                    }
                }

                // Update if the file is duplicated
                Media exist = MediaService.find(idRecipe, fileName);
                if (exist != null && isMain) {
                    MediaService.update(exist);
                } else {
                    MediaService.create(media);
                }

                return util.Json.jsonResult(response(),
                        ok(util.Json.generateJsonInfoMessages("File '" + fileName + "' uploaded")));
            } else {
                return util.Json.jsonResult(response(),
                        badRequest(util.Json.generateJsonErrorMessages("The file is required")));
            }
        }
    }

    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result delete(Integer id) {
        Media media = MediaService.find(id);
        if (media != null
                && MediaService.delete(id, Json.fromJson(Json.parse(request().username()), User.class).email)) {
            try {
                if (Play.isProd()) {
                    DbxRequestConfig config = new DbxRequestConfig(APP_NAME, Locale.getDefault().toString());
                    DbxClient client = new DbxClient(config, ACCESS_TOKEN);
                    client.delete("/" + media.recipe.id + "/" + media.filename);
                } else {
                    String pathDir = "public" + MediaService.FILE_SEPARARTOR + "files" + MediaService.FILE_SEPARARTOR
                            + media.recipe.id + MediaService.FILE_SEPARARTOR;
                    Path path = Paths.get(pathDir + media.filename);
                    Files.delete(path);
                }
            } catch (IOException | DbxException e) {
                System.err.println(e.getMessage());
                return util.Json.jsonResult(response(),
                        internalServerError(util.Json.generateJsonErrorMessages("Error deleting the file")));
            }
            return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages("Deleted file " + id)));
        }
        return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found file " + id)));
    }

}

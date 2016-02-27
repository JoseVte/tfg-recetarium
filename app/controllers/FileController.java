package controllers;

import com.dropbox.core.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import middleware.Authenticated;
import models.User;
import models.service.FileService;
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

public class FileController extends Controller {
    private static final String ACCESS_TOKEN = Play.application().configuration().getString("dropbox.access.token");
    private static final String APP_NAME = Play.application().configuration().getString("dropbox.app.name");
    private String[] defaultValue = {"false"};

    private Result getFile(models.File file) throws IOException, DbxException, NullModelException {
        if (file == null) {
            throw new NullModelException();
        }
        if (Play.isProd()) {
            DbxRequestConfig config = new DbxRequestConfig(APP_NAME, Locale.getDefault().toString());
            DbxClient client = new DbxClient(config, ACCESS_TOKEN);
            return redirect(client.createTemporaryDirectUrl("/" + file.url).url);
        }

        if (Play.isDev()) {
            String path = "public" + FileService.FILE_SEPARARTOR + "files" + FileService.FILE_SEPARARTOR + file.id;
            File dir = new File(path);
            File f = new File(path + FileService.FILE_SEPARARTOR + URLDecoder.decode(file.title, "UTF-8"));
            MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
            if (!dir.exists() && !dir.mkdirs()) {
                return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages("Something went wrong")));
            }

            return ok(FileUtils.readFileToByteArray(f)).as(mimeTypesMap.getContentType(f));
        }

        return ok();
    }

    private models.File uploadFileToDropbox(DbxClient client, FilePart file, User user, String fileName, boolean isMain) {
        FileInputStream inputStream = null;
        DbxEntry fileDropbox;
        try {
            inputStream = new FileInputStream(file.getFile());
            DbxWriteMode mode = isMain ? DbxWriteMode.force() : DbxWriteMode.add();
            fileDropbox = client.uploadFile("/" + user.id + "/" + fileName, mode, file.getFile().length(), inputStream);
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
        return new models.File(fileDropbox.path, fileDropbox.iconName, fileDropbox.name, user);
    }

    private models.File uploadFileToLocal(FilePart file, User user, String fileName, boolean isMain) {
        String path = "public" + FileService.FILE_SEPARARTOR + "files" + FileService.FILE_SEPARARTOR + user.id;
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
            return new models.File(fileExist.getPath(), "", fileExist.getName(), user);
        } else {
            File fileExist = new File(path, fileName);
            File fileStored = file.getFile();
            fileStored.renameTo(fileExist);
            return new models.File(fileExist.getPath(), "", fileExist.getName(), user);
        }
    }

    private Result uploadFile(User user, MultipartFormData body, FilePart file) {
        boolean isMain = Boolean.parseBoolean(body.asFormUrlEncoded().getOrDefault("is_main", defaultValue)[0]);
        String fileName = isMain ? "main." + FilenameUtils.getExtension(file.getFilename()) : file.getFilename();
        models.File fileModel;
        if (Play.isProd()) {
            DbxRequestConfig config = new DbxRequestConfig(APP_NAME, Locale.getDefault().toString());
            DbxClient client = new DbxClient(config, ACCESS_TOKEN);
            fileModel = uploadFileToDropbox(client, file, user, fileName, isMain);
        } else {
            fileModel = uploadFileToLocal(file, user, fileName, isMain);
        }
        if (fileModel != null) {
            if (isMain) {
                models.File aux = FileService.find(user.id, fileName);
                if (aux != null) {
                    FileService.update(aux);
                    return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages("File '" + fileName + "' uploaded")));
                }
            }
            FileService.create(fileModel);
            return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages("File '" + fileName + "' uploaded")));
        }
        return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages("Error uploading the file")));
    }

    private Result uploadFiles(User user, List<FilePart> files) {
        List<ObjectNode> msg = new ArrayList<>();
        models.File fileModel;
        if (Play.isProd()) {
            DbxRequestConfig config = new DbxRequestConfig(APP_NAME, Locale.getDefault().toString());
            DbxClient client = new DbxClient(config, ACCESS_TOKEN);
            for (FilePart file : files) {
                fileModel = uploadFileToDropbox(client, file, user, file.getFilename(), false);
                if (fileModel == null) {
                    msg.add(util.Json.generateJsonErrorMessages("Error uploading the file: " + file.getFilename()));
                } else {
                    FileService.create(fileModel);
                    msg.add(util.Json.generateJsonInfoMessages("File '" + fileModel.title + "' uploaded"));
                }
            }
        } else if (Play.isDev()) {
            for (FilePart file : files) {
                fileModel = uploadFileToLocal(file, user, file.getFilename(), false);
                if (fileModel == null) {
                    msg.add(util.Json.generateJsonErrorMessages("Error uploading the file: " + file.getFilename()));
                } else {
                    FileService.create(fileModel);
                    msg.add(util.Json.generateJsonInfoMessages("File '" + fileModel.title + "' uploaded"));
                }
            }
        }

        return util.Json.jsonResult(response(), ok(Json.toJson(msg)));
    }

    private void deleteFile(models.File file) throws DbxException, IOException {
        if (Play.isProd()) {
            DbxRequestConfig config = new DbxRequestConfig(APP_NAME, Locale.getDefault().toString());
            DbxClient client = new DbxClient(config, ACCESS_TOKEN);
            client.delete(file.url);
        } else if (Play.isDev()) {
            Path path = Paths.get(file.url);
            Files.delete(path);
        }
    }

    @Transactional(readOnly = true)
    public Result getById(Integer idUser, Integer id) {
        models.File file = FileService.find(idUser, id);
        try {
            return getFile(file);
        } catch (DbxException | IOException e) {
            e.printStackTrace();
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found file: [id: " + id + " file: " + idUser + "]")));
        } catch (NullModelException e) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found file: [id: " + id + " file: " + idUser + "]")));
        }
    }

    @Transactional(readOnly = true)
    public Result getByFile(Integer idRecipe, String file) {
        models.File fileModel = FileService.find(idRecipe, file);
        try {
            return getFile(fileModel);
        } catch (DbxException | IOException e) {
            e.printStackTrace();
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found file: [file: " + file + " file: " + idRecipe + "]")));
        } catch (NullModelException e) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found file: [file: " + file + " file: " + idRecipe + "]")));
        }
    }

    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result upload(Integer idUser) {
        MultipartFormData body = request().body().asMultipartFormData();
        boolean isMultiple = Boolean.parseBoolean(body.asFormUrlEncoded().getOrDefault("is_multiple", defaultValue)[0]);

        User user = Json.fromJson(Json.parse(request().username()), User.class);
        // Check if file exist
        if (user == null) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found " + idUser)));
        }

        if (isMultiple) {
            List<FilePart> files = body.getFiles();
            if (files.isEmpty()) {
                return util.Json.jsonResult(response(), badRequest(util.Json.generateJsonErrorMessages("No files have been included in the request.")));
            } else {
                return uploadFiles(user, files);
            }
        } else {
            FilePart file = body.getFile("file");
            if (file != null) {
                return uploadFile(user, body, file);
            } else {
                return util.Json.jsonResult(response(), badRequest(util.Json.generateJsonErrorMessages("The file is required")));
            }
        }
    }

    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result deleteById(Integer idUser, Integer id) {
        models.File file = FileService.find(idUser, id);
        if (file != null && FileService.delete(id, Json.fromJson(Json.parse(request().username()), User.class).email)) {
            try {
                deleteFile(file);
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
    public Result deleteByFile(Integer idUser, String file) {
        models.File fileModel = FileService.find(idUser, file);
        if (fileModel != null && FileService.delete(fileModel.id, Json.fromJson(Json.parse(request().username()), User.class).email)) {
            try {
                deleteFile(fileModel);
            } catch (IOException | DbxException e) {
                System.err.println(e.getMessage());
                return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages("Error deleting the file " + file)));
            }
            return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages("Deleted file " + file)));
        }
        return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found file " + file)));
    }

}

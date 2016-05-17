package controllers;

import com.dropbox.core.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import middleware.Authenticated;
import models.User;
import models.service.FileService;
import models.service.UserService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import play.Play;
import play.db.jpa.Transactional;
import play.i18n.Messages;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
            return redirect(client.createTemporaryDirectUrl(file.url).url);
        }

        if (Play.isDev()) {
            File f = new File(file.url);
            MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
            if (!f.exists() && !f.isFile()) {
                return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages(Messages.get("error.server"))));
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
            fileDropbox = client.uploadFile("/user/" + user.id + "/" + fileName, mode, file.getFile().length(), inputStream);
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
        return new models.File(fileDropbox.path, file.getContentType(), file.getFilename(), fileDropbox.name, user);
    }

    private models.File uploadFileToLocal(FilePart file, User user, String fileName, boolean isMain) {
        String path = "public" + FileService.FILE_SEPARARTOR + "files" + FileService.FILE_SEPARARTOR + "user" + FileService.FILE_SEPARARTOR + user.id;
        File dir = new File(path);

        // Create the dir if not exists
        if (!dir.exists() && !dir.mkdirs()) {
            return null;
        }

        if (!isMain) {
            File fileExist = new File(path, fileName);
            File fileStored = file.getFile();
            boolean exists = false;
            int i = 0;
            while (!exists) {
                if (!fileExist.exists()) {
                    fileStored.renameTo(fileExist);
                    exists = true;
                } else {
                    i++;
                    fileName = FilenameUtils.getName(fileName) + "_" + i + "." + FilenameUtils.getExtension(file.getFilename());
                    fileExist = new File(path, fileName);
                }
            }
            return new models.File(fileExist.getPath(), file.getContentType(), file.getFilename(), fileExist.getName(), user);
        } else {
            File fileExist = new File(path, fileName);
            File fileStored = file.getFile();
            fileStored.renameTo(fileExist);
            return new models.File(fileExist.getPath(), file.getContentType(), file.getFilename(), fileExist.getName(), user);
        }
    }

    private Result uploadFile(User user, MultipartFormData body, FilePart file) {
        boolean isMain = Boolean.parseBoolean(body.asFormUrlEncoded().getOrDefault("is_main", defaultValue)[0]);
        String fileName = isMain ? "main." + FilenameUtils.getExtension(file.getFilename()) : RandomStringUtils.randomAlphanumeric(10) + "." + FilenameUtils.getExtension(file.getFilename());
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
                    return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages(Messages.get("info.file-upload", file.getFilename()))));
                }
            }
            fileModel = FileService.create(fileModel);
            return util.Json.jsonResult(response(), ok(Json.toJson(fileModel)));
        }
        return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages(Messages.get("error.file-upload", file.getFilename()))));
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
                    msg.add(util.Json.generateJsonErrorMessages(Messages.get("error.file-upload", file.getFilename())));
                } else {
                    FileService.create(fileModel);
                    msg.add(util.Json.generateJsonInfoMessages(Messages.get("info.file-upload", fileModel.title)));
                }
            }
        } else if (Play.isDev()) {
            for (FilePart file : files) {
                fileModel = uploadFileToLocal(file, user, file.getFilename(), false);
                if (fileModel == null) {
                    msg.add(util.Json.generateJsonErrorMessages(Messages.get("error.file-upload", file.getFilename())));
                } else {
                    FileService.create(fileModel);
                    msg.add(util.Json.generateJsonInfoMessages(Messages.get("info.file-upload", fileModel.title)));
                }
            }
        }

        return util.Json.jsonResult(response(), ok(Json.toJson(msg)));
    }

    /**
     * Method for delete a file in the system
     *
     * @param file File
     *
     * @throws DbxException
     * @throws IOException
     */
    public static void deleteFile(models.File file) throws DbxException, IOException {
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
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages(Messages.get("error.not-found", Messages.get("article.male-single"), Messages.get("field.file"), id))));
        } catch (NullModelException e) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages(Messages.get("error.not-found", Messages.get("article.male-single"), Messages.get("field.file"), id))));
        }
    }

    @Transactional(readOnly = true)
    public Result getByFile(Integer idUser, String file) {
        models.File fileModel = FileService.find(idUser, file);
        try {
            return getFile(fileModel);
        } catch (DbxException | IOException e) {
            e.printStackTrace();
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages(Messages.get("error.not-found", Messages.get("article.male-single"), Messages.get("field.file"), file))));
        } catch (NullModelException e) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages(Messages.get("error.not-found", Messages.get("article.male-single"), Messages.get("field.file"), file))));
        }
    }

    @Transactional(readOnly = true)
    @Security.Authenticated(Authenticated.class)
    public Result getUserFiles(Integer idUser) {
        User user = Json.fromJson(Json.parse(request().username()), User.class);
        if (Objects.equals(user.id, idUser) || user.isAdmin()) {
            List<models.File> files = FileService.all(idUser);
            return util.Json.jsonResult(response(), ok(Json.toJson(files)));
        }
        return unauthorized();
    }

    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result upload(Integer idUser) {
        MultipartFormData body = request().body().asMultipartFormData();
        boolean isMultiple = Boolean.parseBoolean(body.asFormUrlEncoded().getOrDefault("is_multiple", defaultValue)[0]);

        User user = Json.fromJson(Json.parse(request().username()), User.class);

        // Check permissions
        if (user == null || (!Objects.equals(user.id, idUser) && !user.isAdmin())) {
            return unauthorized();
        }

        if (isMultiple) {
            List<FilePart> files = body.getFiles();
            if (files.isEmpty()) {
                return util.Json.jsonResult(response(), badRequest(util.Json.generateJsonErrorMessages(Messages.get("error.no-files"))));
            } else {
                return uploadFiles(user, files);
            }
        } else {
            FilePart file = body.getFile("file");
            if (file != null) {
                return uploadFile(UserService.find(idUser), body, file);
            } else {
                return util.Json.jsonResult(response(), badRequest(util.Json.generateJsonErrorMessages(Messages.get("error.field-required", Messages.get("article.male-single"), Messages.get("field.file")))));
            }
        }
    }

    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result deleteById(Integer idUser, Integer id) {
        models.File file = FileService.find(idUser, id);
        if (file != null) {
            if (FileService.delete(file, Json.fromJson(Json.parse(request().username()), User.class))) {
                try {
                    deleteFile(file);
                } catch (IOException | DbxException e) {
                    System.err.println(e.getMessage());
                    return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages(Messages.get("error.delete", Messages.get("article.male-single"), Messages.get("field.file"), file.title))));
                }
                return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages(Messages.get("info.delete", Messages.get("article.male-single"), Messages.get("field.file"), file.title))));
            }
            return util.Json.jsonResult(response(), badRequest(util.Json.generateJsonErrorMessages(Messages.get("error.file-used", file.title))));
        }
        return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages(Messages.get("error.not-found", Messages.get("article.male-single"), Messages.get("field.file"), id))));
    }

    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result deleteByFile(Integer idUser, String file) {
        models.File fileModel = FileService.find(idUser, file);
        if (fileModel != null) {
            if (FileService.delete(fileModel, Json.fromJson(Json.parse(request().username()), User.class))) {
                try {
                    deleteFile(fileModel);
                } catch (IOException | DbxException e) {
                    System.err.println(e.getMessage());
                    return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages(Messages.get("error.delete", Messages.get("article.male-single"), Messages.get("field.file"), fileModel.title))));
                }
                return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages(Messages.get("info.delete", Messages.get("article.male-single"), Messages.get("field.file"), fileModel.title))));
            }
            return util.Json.jsonResult(response(), badRequest(util.Json.generateJsonErrorMessages(Messages.get("error.file-used", fileModel.title))));
        }
        return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages(Messages.get("error.not-found", Messages.get("article.male-single"), Messages.get("field.file"), file))));
    }

}

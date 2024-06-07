package io.github.sabuwalamustafa.fortesting;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.sabuwalamustafa.interfaces.IFileUtils;
import io.github.sabuwalamustafa.utils_utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;

public class FileUtils implements IFileUtils {
    private static FileUtils INSTANCE;

    private FileUtils() {
    }

    public static FileUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FileUtils();
        }
        return INSTANCE;
    }

    public String readIt(String filePathStr) throws IOException {
        Path filePath = Paths.get(filePathStr).toAbsolutePath();
        return Files.readString(filePath);
    }

    public JsonNode readJson(String filePathStr) throws IOException {
        String jsonString = readIt(filePathStr);
        return utils_utils.parseJsonStr(jsonString);
    }

    public void write(String filePathStr, String content)
            throws IOException {
        Path filePath = Paths.get(filePathStr).toAbsolutePath();
        Files.write(filePath, Collections.singletonList(content),
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    public void write(String filePathStr, String content,
            boolean createIfAbsent)
            throws IOException {
        if (createIfAbsent) {
            createFileIfMissing(filePathStr);
        }
        write(filePathStr, content);
    }

    public void append(String filePathStr, String content)
            throws IOException {
        Path filePath = Paths.get(filePathStr).toAbsolutePath();
        Files.write(filePath, Collections.singletonList(content),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public void append(String filePathStr, String content,
            boolean createIfAbsent)
            throws IOException {
        if (createIfAbsent) {
            createFileIfMissing(filePathStr);
        }
        append(filePathStr, content);
    }

    public void createDirRecursively(String directoryPathStr)
            throws IOException {
        Path directoryPath = Paths.get(directoryPathStr);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }
    }

    public void createFileIfMissing(String filePathStr)
            throws IOException {
        Path filePath = Paths.get(filePathStr);
        Path parentDirectoryPath = filePath.getParent();
        createDirRecursively(parentDirectoryPath.toString());
        if (!fileExists(filePathStr)) {
            Files.createFile(filePath);
        }
    }


    public void removeIfPresent(String filePathStr) throws IOException {
        Path filePath = Paths.get(filePathStr);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }

    public boolean fileExists(String filePathStr) {
        Path filePath = Paths.get(filePathStr);
        return Files.exists(filePath);
    }

    @Override public List<String> getAllFiles(String dir) {
        // todo: Implement.
        return null;
    }
}

package io.github.sabuwalamustafa;

import io.github.sabuwalamustafa.interfaces.IFileUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ZerodhaTokensProvider {
    private IFileUtils gfsFileUtils;
    private FilePathsProvider filePathsProvider;

    public ZerodhaTokensProvider(IFileUtils gfsFileUtils,
            FilePathsProvider filePathsProvider) {
        this.gfsFileUtils = gfsFileUtils;
        this.filePathsProvider = filePathsProvider;
    }

    public Map<String, String> provide() throws IOException {
        String filePath = filePathsProvider.getTokensFilePath();
        String content = gfsFileUtils.readIt(filePath).trim();
        String[] splitted = content.split("\n");
        Map<String, String> map = new HashMap<>();
        map.put("access_token", splitted[1]);
        map.put("public_token", splitted[2]);
        return map;
    }
}

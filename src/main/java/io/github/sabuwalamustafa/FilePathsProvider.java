package io.github.sabuwalamustafa;

public class FilePathsProvider {
    private String TOKENS_FILE_PATH;
    private String GOOGLE_KEY_FILE_PATH;

    public FilePathsProvider() {
        TOKENS_FILE_PATH = "zerodha_tokens.txt";
        GOOGLE_KEY_FILE_PATH = "google_cloud_key.json";
    }

    public String getTokensFilePath() {
        return utils_utils.checkNotNull(TOKENS_FILE_PATH);
    }

    public String getGoogleKeyFilePath() {
        return utils_utils.checkNotNull(GOOGLE_KEY_FILE_PATH);
    }
}

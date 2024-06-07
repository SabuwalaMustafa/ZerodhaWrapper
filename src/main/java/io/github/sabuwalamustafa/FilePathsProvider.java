package io.github.sabuwalamustafa;

public class FilePathsProvider {
    private String TOKENS_FILE_PATH;
    public FilePathsProvider() {
        TOKENS_FILE_PATH = "zerodha_tokens.txt";
    }

    public String getTokensFilePath() {
        return utils_utils.checkNotNull(TOKENS_FILE_PATH);
    }
}

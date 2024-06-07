package io.github.sabuwalamustafa.interfaces;

public interface IFilePathsProvider {
    public String getCurrentLogFilePath();

    public String getDataFolderPath();

    public String getBaseFolderPath();

    public String getStocksUniverseFilePath();

    String getOneTimeDataPath();
}

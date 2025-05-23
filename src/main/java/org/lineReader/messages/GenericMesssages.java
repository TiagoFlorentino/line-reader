package org.lineReader.messages;

public class GenericMesssages {

    public static final String ERROR_MESSAGE_OUT_BOUNDS = "Requested line is beyond the end of the file";

    public static final String ERROR_MESSAGE_GENERIC = "Something happened during the request process";

    public static final String ERROR_MESSAGE_FAILED_READING = "Something happened during the line reading process";

    public static final String ERROR_MESSAGE_FAILED_READING_INDEX = "Something happened during the index reading process";

    public static final String ERROR_MESSAGE_FILE_MISSING = "File does not exist!";

    public static final String ERROR_MESSAGE_INVALID_INDEX = "Index is invalid!";

    public static final String LOG_MESSAGE_START_INDEXING = "🧵 Start async thread for indexing";

    public static final String LOG_MESSAGE_FAILED_TO_DELETE_INDEX_FILE = "🚩Failed deleting the previous indexing file!";

    public static final String LOG_MESSAGE_FILE_MISSING_AT_INDEX = "👀 File does not exist in order to be indexed!";
}

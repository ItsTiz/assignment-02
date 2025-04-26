package it.unibo.pcd.assignment02.part1.utils;

public enum Errors {
    PARSING_ERROR (505, "Parsing failed"),
    ANALYSER_ERROR (606, "Analyzing failed"),
    UNKNOWN_ERROR (999, "Unknown error.");

    private final int code;
    private final String defaultMessage;

    Errors(final int code, final String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public static Errors getErrorFromCode(int code){
        return switch (code) {
            case 505 -> PARSING_ERROR;
            case 606 -> ANALYSER_ERROR;
            default -> UNKNOWN_ERROR;
        };
    }

    public int getCode () {
        return code;
    }

    public String defaultMessage() {
        return defaultMessage;
    }
}


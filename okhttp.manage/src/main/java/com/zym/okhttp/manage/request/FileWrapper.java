package com.zym.okhttp.manage.request;

import java.io.File;

import okhttp3.MediaType;

/**
 * Created by zymapp on 2016/9/26.
 */

public class FileWrapper {
    public File file;
    public String fileName;
    public MediaType mediaType;
    private long fileSize;

    public FileWrapper(File file, MediaType mediaType) {
        this.file = file;
        this.fileName = file.getName();
        this.mediaType = mediaType;
        this.fileSize = file.length();
    }

    public String getFileName() {
        if (fileName != null) {
            return fileName;
        } else {
            return "nofilename";
        }
    }

    public File getFile() {
        return file;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public long getFileSize() {
        return fileSize;
    }
}

package com.sgcc.sql.test;

import com.sgcc.share.util.PathHelper;

import java.io.IOException;

public class FileFormatException extends IOException {

    public FileFormatException() {}
    public FileFormatException(String gripe)
    {
        super(gripe);
    }
    public FileFormatException(String gripe,String name)
    {
        super(gripe + name);
    }
}

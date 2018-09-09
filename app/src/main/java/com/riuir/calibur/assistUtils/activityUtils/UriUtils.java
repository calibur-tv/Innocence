package com.riuir.calibur.assistUtils.activityUtils;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class UriUtils {
    //uri转file
    public static File uri2file(Uri uri){
        File file = null;
        try {
            file = new File(new URI(uri.toString()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return file;
    }
    //file转uri
    public static URI file2URI(File file){
        URI uri = file.toURI();
        return uri;
    }

}

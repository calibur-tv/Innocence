package com.riuir.calibur.utils.album;

import android.widget.ImageView;

import com.riuir.calibur.utils.GlideUtils;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.AlbumLoader;

public class MyAlbumLoader implements AlbumLoader {
    @Override
    public void load(ImageView imageView, AlbumFile albumFile) {
        load(imageView, albumFile.getPath());
    }

    @Override
    public void load(ImageView imageView, String url) {
        GlideUtils.loadImageView(imageView.getContext(),
                url,imageView);
    }
}

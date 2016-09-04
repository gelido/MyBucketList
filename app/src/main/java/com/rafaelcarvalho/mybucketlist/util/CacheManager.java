package com.rafaelcarvalho.mybucketlist.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Created by Rafael on 06/12/15.
 */
public class CacheManager<E> {


    private Context mContext;

    public CacheManager(Context mContext) {
        this.mContext = mContext;
    }

    public void save(BucketListItemType type, List<E> items) throws IOException {
        File cache = mContext.getCacheDir();
        final File suspend_f = new File(cache.getAbsoluteFile() + File.separator + "Search_"+
                                            type.getTitle());

        FileOutputStream fos = new FileOutputStream(suspend_f);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        oos.writeObject(items);

        oos.close();
        fos.close();

    }

    public List<E> read(BucketListItemType type) throws IOException, ClassNotFoundException {
        File cache = mContext.getCacheDir();
        final File suspend_f = new File(cache.getAbsoluteFile() + File.separator + "Search_"+
                                        type.getTitle());
        List<E> list;

        FileInputStream fis = new FileInputStream(suspend_f);
        ObjectInputStream ois = new ObjectInputStream(fis);

        list = (List<E>) ois.readObject();

        ois.close();
        fis.close();


        return list;
    }

    public void clearCache(final BucketListItemType type){

        //Fetches the files from this type
        File dir = mContext.getCacheDir();
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.contains(type.getTitle());
            }
        });

        for (File file : files) {
            file.delete();
        }
    }
}

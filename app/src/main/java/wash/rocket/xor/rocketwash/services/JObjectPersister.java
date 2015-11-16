package wash.rocket.xor.rocketwash.services;

import android.app.Application;

import com.bluelinelabs.logansquare.LoganSquare;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.exception.CacheLoadingException;
import com.octo.android.robospice.persistence.exception.CacheSavingException;
import com.octo.android.robospice.persistence.file.InFileObjectPersister;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import roboguice.util.temp.Ln;

/**
 * Created by aratj on 12.11.2015.
 */
public class JObjectPersister<T> extends InFileObjectPersister<T> {

    // ============================================================================================
    // CONSTRUCTOR
    // ============================================================================================

    public JObjectPersister(Application application, Class<T> clazz, File cacheFolder)
            throws CacheCreationException {
        super(application, clazz, cacheFolder);
    }

    public JObjectPersister(Application application, Class<T> clazz)
            throws CacheCreationException {
        super(application, clazz);
    }

    // ============================================================================================
    // METHODS
    // ============================================================================================

    @Override
    protected T readCacheDataFromFile(File file) throws CacheLoadingException {
        try {
            InputStream targetStream = new FileInputStream(file);
            T result = LoganSquare.parse(targetStream, getHandledClass());

            return result;
        } catch (FileNotFoundException e) {
            // Should not occur (we test before if file exists)
            // Do not throw, file is not cached
            Ln.w("file " + file.getAbsolutePath() + " does not exists", e);
            return null;
        } catch (Exception e) {
            throw new CacheLoadingException(e);
        }
    }

    @Override
    public T saveDataToCacheAndReturnData(final T data, final Object cacheKey) throws CacheSavingException {

        try {
            if (isAsyncSaveEnabled()) {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            saveData(data, cacheKey);
                        } catch (IOException e) {
                            Ln.e(e, "An error occured on saving request " + cacheKey + " data asynchronously");
                        } catch (CacheSavingException e) {
                            Ln.e(e, "An error occured on saving request " + cacheKey + " data asynchronously");
                        }
                    }
                };
                t.start();
            } else {
                saveData(data, cacheKey);
            }
        } catch (CacheSavingException e) {
            throw e;
        } catch (Exception e) {
            throw new CacheSavingException(e);
        }
        return data;
    }

    private void saveData(T data, Object cacheKey) throws IOException, CacheSavingException {
        LoganSquare.serialize(data, new FileOutputStream(getCacheFile(cacheKey)));
    }

}

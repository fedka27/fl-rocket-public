package wash.rocket.xor.rocketwash.services;

import android.app.Application;

import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.file.InFileObjectPersisterFactory;

import java.io.File;
import java.util.List;

/**
 * Created by aratj on 12.11.2015.
 */
public class JObjectPersisterFactory extends InFileObjectPersisterFactory {
    // ----------------------------------
    // CONSTRUCTOR
    // ----------------------------------
    public JObjectPersisterFactory(Application application) throws CacheCreationException {
        this(application, null, null);
    }

    public JObjectPersisterFactory(Application application, File cacheFolder)
            throws CacheCreationException {
        this(application, null, cacheFolder);
    }

    public JObjectPersisterFactory(Application application, List<Class<?>> listHandledClasses) throws CacheCreationException {
        this(application, listHandledClasses, null);
    }

    public JObjectPersisterFactory(Application application, List<Class<?>> listHandledClasses, File cacheFolder) throws CacheCreationException {
        super(application, listHandledClasses, cacheFolder);
    }

    // ----------------------------------
    // API
    // ----------------------------------
    @Override
    public <DATA> JObjectPersister<DATA> createInFileObjectPersister(Class<DATA> clazz, File cacheFolder)
            throws CacheCreationException {
        return new JObjectPersister<DATA>(getApplication(), clazz, cacheFolder);
    }
}

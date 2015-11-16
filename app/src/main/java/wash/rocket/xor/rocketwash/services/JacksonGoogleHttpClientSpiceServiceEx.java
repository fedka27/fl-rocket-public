package wash.rocket.xor.rocketwash.services;

import android.app.Application;

import com.octo.android.robospice.JacksonGoogleHttpClientSpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;


public class JacksonGoogleHttpClientSpiceServiceEx extends JacksonGoogleHttpClientSpiceService {

    protected static final int DEFAULT_THREAD_COUNT = 3;


    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        CacheManager cacheManager = new CacheManager();
        cacheManager.addPersister(new JObjectPersisterFactory(application));
        return cacheManager;
    }

    @Override
    public int getCoreThreadCount() {
        return 2;
    }

    @Override
    public int getThreadCount() {
        return 3;
    }

    @Override
    public int getMaximumThreadCount() {
        return 3;
    }

    @Override
    public int getThreadPriority() {
        return Thread.NORM_PRIORITY;
    }
}

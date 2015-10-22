package wash.rocket.xor.rocketwash.services;

import android.app.Application;

import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.exception.CacheSavingException;

public class RobospiceService extends SpiceService {
    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {

        return new CacheManager() {
            @Override
            public <T> T saveDataToCacheAndReturnData(T data, Object cacheKey) throws CacheSavingException, CacheCreationException {
                return data;
            }
        };

        /*
        CacheManager cacheManager = new CacheManager();
        InFileStringObjectPersister inFileStringObjectPersister = new InFileStringObjectPersister(application);
        cacheManager.addPersister(inFileStringObjectPersister);
        return cacheManager;*/
    }

    @Override
    public int getThreadCount() {
        return 1;
    }

}

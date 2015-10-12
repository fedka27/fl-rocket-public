package wash.rocket.xor.rocketwash.services;

import android.app.Application;

import com.octo.android.robospice.GoogleHttpClientSpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.googlehttpclient.json.GsonObjectPersisterFactory;

/**
 * Created by aratj on 16.09.2015.
 */

public class GSonRocketWashApiService extends GoogleHttpClientSpiceService {

    private static final int THREAD_COUNT = 3;

    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        CacheManager cacheManager = new CacheManager();

        GsonObjectPersisterFactory gsonObjectPersisterFactory = new GsonObjectPersisterFactory(application);
        cacheManager.addPersister(gsonObjectPersisterFactory);


        return cacheManager;
    }

    @Override
    public int getThreadCount() {
        return THREAD_COUNT;
    }
}
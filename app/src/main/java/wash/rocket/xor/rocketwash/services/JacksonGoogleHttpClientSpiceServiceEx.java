package wash.rocket.xor.rocketwash.services;

import com.octo.android.robospice.JacksonGoogleHttpClientSpiceService;


public class JacksonGoogleHttpClientSpiceServiceEx extends JacksonGoogleHttpClientSpiceService {

    protected static final int DEFAULT_THREAD_COUNT = 3;


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

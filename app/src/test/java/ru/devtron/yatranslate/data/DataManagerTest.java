package ru.devtron.yatranslate.data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.devtron.yatranslate.data.managers.DbManager;
import ru.devtron.yatranslate.data.network.RestService;
import ru.devtron.yatranslate.data.network.response.TranslateRes;
import ru.devtron.yatranslate.resources.TestResponses;

public class DataManagerTest {
    private MockWebServer mMockWebServer;
    private RestService mRestService;
    private DataManager mDataManager;
    private Retrofit mRetrofit;
    private DbManager mDbManager;

    @Before
    public void setUp() throws Exception {
        prepareMockServer();
        mRestService = mRetrofit.create(RestService.class);
        prepareRxSchedulers(); // для переопределения Schedulers Rx (при subscribeOn/observeOn)
    }

    //region ==================== Prepare ====================
    private void prepareRxSchedulers() {
        //RxAndroidPlugins.reset();
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    private void prepareMockServer() {
        mMockWebServer = new MockWebServer();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(mMockWebServer.url("").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder().build())
                .build();
    }

    public void prepareDispatcher_200() {
        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                String path = request.getPath(); // получаем path запроса
                switch (path) {
                    case "/detect?key=trnsl.1.1.20170407T152430Z.e90a68b57a1961b7.2e46bfa0250b267f3b1e4402340b1b5273ed62cd&text=hello&hint=en,ru":
                        return new MockResponse()
                                .setResponseCode(200)
                                .setBody(TestResponses.SUCCESS_DETECT_LANG_RES);
                    case "/translate":  //RestService "/" + path
                        return new MockResponse()
                                .setResponseCode(200)
                                .setBody(TestResponses.SUCCESS_TRANSLATE_RES);
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        };

        mMockWebServer.setDispatcher(dispatcher);
    }
    //endregion


    @After
    public void tearDown() throws Exception {
        mMockWebServer.shutdown();
    }

    @Test
    public void detectLanguage_200_SUCCESS_TRANSLATE_RES() throws Exception {
        //given
        prepareDispatcher_200();

        mDataManager = new DataManager(mRestService, null);
        TestObserver<TranslateRes> subscriber;

        //when
        subscriber = mDataManager.detectLanguageTest("hello").test();
        subscriber.awaitTerminalEvent();

        //then
        subscriber.assertNoErrors();
        subscriber.assertValue(translateRes -> translateRes.getCode() == 200);
        subscriber.assertValue(translateRes -> translateRes.getLang().equals("en"));
        subscriber.assertValue(translateRes -> translateRes.getText() == null);
    }

}
package ru.devtron.yatranslate.ui.screens.translate;

import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.Provides;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import nl.nl2312.rxcupboard2.OnDatabaseChange;
import ru.devtron.yatranslate.base.BasePresenter;
import ru.devtron.yatranslate.data.network.error.NetworkAvailableError;
import ru.devtron.yatranslate.data.network.error.NoLanguageError;
import ru.devtron.yatranslate.data.network.response.DictionaryRes;
import ru.devtron.yatranslate.data.storage.Translation;
import ru.devtron.yatranslate.di.DaggerService;
import ru.devtron.yatranslate.ui.screens.root.RootActivity;
import ru.devtron.yatranslate.ui.screens.root.RootPresenter;
import ru.devtron.yatranslate.ui.screens.translate.dictionary.BaseItem;
import ru.devtron.yatranslate.ui.screens.translate.dictionary.PartOfSpeechItem;
import ru.devtron.yatranslate.ui.screens.translate.dictionary.Item;
import ru.yandex.speechkit.Error;
import ru.yandex.speechkit.Recognition;
import ru.yandex.speechkit.Recognizer;
import ru.yandex.speechkit.RecognizerListener;
import ru.yandex.speechkit.Synthesis;
import ru.yandex.speechkit.Vocalizer;
import ru.yandex.speechkit.VocalizerListener;


public class TranslatePresenter extends BasePresenter<ITranslateView>
        implements ITranslatePresenter, RecognizerListener, VocalizerListener {
    private static final String TAG = "TranslatePresenter";

	static final String RU_EN = "ru-en";
    private static final String EN_RU = "en-ru";

	@Inject
    TranslateModel mModel;

	@Inject
    RootPresenter mRootPresenter;

    private String currentText;
    private String currentLanguage = RU_EN;
    private Recognizer recognizer;
    private Vocalizer vocalizer;

	public TranslatePresenter() {
        createDaggerComponent();
    }

    @Override
    public void initView() {
        if (getView() != null) {
            mCoSubs.add(
                    getView().getTranslatePanel().getTranslateEt().observeChanges()
                            .doOnNext(charSequence -> getView().getTranslatePanel().showOrHideClearBtn(charSequence))
                            .doOnNext(charSequence -> getView().setIdleState())
                            .debounce(500, TimeUnit.MILLISECONDS)
                            .map(charSequence -> charSequence.toString().trim())
                            .filter(charSequence -> charSequence.length() > 0)
                            .toObservable()
                            .flatMap(text -> {
                                currentText = text;
                                return Observable.just(text);
                            })
                            .switchMap(s -> mModel.detectLanguageAndTranslate(s))
                            .subscribe(translateRes -> {
                                updateCurrentLanguage(translateRes.getLang());
                                getView().showTranslated(joinArrayToString(translateRes.getText()));
                                getView().setFavorite(translateRes.isFavorite());
                                requestDictionary(translateRes.getLang());
                            }, toastErrorAction)
            );
            observeDbChanges();
            subscribeOnTranslateAgainSubject();
        }
    }

    private void subscribeOnTranslateAgainSubject() {
        mCoSubs.add(
                mRootPresenter.getTranslateAgainSubject()
                        .toFlowable(BackpressureStrategy.LATEST)
                        .subscribe(translation -> {
                            if (getView() != null) {
                                mRootPresenter.showTranslateTab();
                                getView().getTranslatePanel().getTranslateEt().setText(translation.getToTranslate());
                            }
                        })
        );
    }

    private void observeDbChanges() {
        mCoSubs.add(
                mModel.observeChangesTranslationTbl()
                        .subscribe(new OnDatabaseChange<Translation>() {
                            @Override
                            public void onUpdate(Translation entity) {
                                if (entity.getToTranslate() != null && currentText != null &&
                                        currentText.equals(entity.getToTranslate()) &&
                                        getView() != null) {
                                    getView().setFavorite(entity.isFavorite());
                                }
                            }

                            @Override
                            public void onDelete(Translation entity) {
                                if (getView() != null) {
                                    getView().setFavorite(false);
                                }
                            }
                        })
        );
    }

    private void updateCurrentLanguage(String lang) {
        if (currentLanguage.equals(lang)) {
            return;
        }
        currentLanguage = lang;
        if (getView() != null) {
            getView().updateCurrentLang();
        }
    }

    @Override
    public void dropView() {
        super.dropView();
        resetRecognizer();
        resetVocalizer();
    }

    private Consumer<Throwable> toastErrorAction = throwable -> {
        if (getView() != null) {
            if (throwable instanceof NetworkAvailableError || throwable instanceof NoLanguageError) {
                getView().showMessage(throwable.getMessage());
            } else {
                getView().getRootActivity().showError(throwable);
            }
            getView().getTranslatePanel().showInvalidTextAnimation();
        }
    };

    private void requestDictionary(String lang) {
        mCoSubs.add(
                mModel.requestDictionary(lang, currentText)
                        .subscribe(dictionaryRes -> {
                            List<BaseItem> baseItems = createDictionary(dictionaryRes);
                            //noinspection ConstantConditions
                            getView().showDictionary(baseItems, dictionaryRes.getDef() != null &&
                                    !dictionaryRes.getDef().isEmpty() ?
                                    dictionaryRes.getDef().get(0).getTs() : null);
                        }, toastErrorAction)
        );
    }


    private List<BaseItem> createDictionary(DictionaryRes dictionaryRes) {
        List<BaseItem> baseItems = new ArrayList<>();

        for (DictionaryRes.Def def : dictionaryRes.getDef()) {
            baseItems.add(new PartOfSpeechItem(def.getPos()));
            for (int i = 0; i < def.getTr().size(); i++) {
                String mean = joinMeanings(def.getTr().get(i).getMean());
                String example = joinExamples(def.getTr().get(i).getEx());

                Item newItem = new Item(
                        i + 1, //pos
                        def.getTr().get(i).getText() + joinTranslates(def.getTr().get(i).getSyn()), //translate
                        mean,
                        example
                );
                baseItems.add(newItem);
            }
        }

        return baseItems;
    }

    private String joinExamples(List<DictionaryRes.Ex> ex) {
        StringBuilder example = new StringBuilder();
        if (ex != null) {
            for (int i = 0; i < ex.size(); i++) {
                List<DictionaryRes.Tr_> translate = ex.get(i).getTr();
                if (translate != null) {
                    for (DictionaryRes.Tr_ tr : translate) {
                        example.append(ex.get(i).getText())
                                .append(" - ")
                                .append(tr.getText());
                    }
                } else {
                    example.append(ex.get(i).getText());
                }
                if (i < ex.size() - 1) {
                    example.append("\n");
                }
            }
        }
        return example.toString();
    }

    private String joinMeanings(List<DictionaryRes.Mean> means) {
        StringBuilder mean = new StringBuilder();
        if (means != null) {
            for (int i = 0; i < means.size(); i++) {
                if (i == 0) {
                    mean.append("(");
                }
                mean.append(means.get(i).getText());
                if (i != means.size() - 1) {
                    mean.append(", ");
                } else {
                    mean.append(")");
                }
            }
        }
        return mean.toString();
    }

    private String joinTranslates(List<DictionaryRes.Syn> syn) {
        StringBuilder translate = new StringBuilder();
        if (syn != null) {
            for (int i = 0; i < syn.size(); i++) {
                translate.append(", ")
                        .append(syn.get(i).getText());
            }
        }
        return translate.toString();
    }

    private String joinArrayToString(List<String> words) {
        StringBuilder translatedString = new StringBuilder();
        for (int i = 0; i < words.size(); i++) {
            translatedString.append(words.get(i));
            if (i != words.size() - 1)
                translatedString.append(", ");
        }
        return translatedString.toString();
    }

    //region ==================== Recognition ====================
    @Override
    public void onRecordingBegin(Recognizer recognizer) {
        if (getView() != null) {
            getView().getTranslatePanel().setVoiceMode(true);
        }
    }

    @Override
    public void onSpeechDetected(Recognizer recognizer) {
    }

    @Override
    public void onSpeechEnds(Recognizer recognizer) {
    }

    @Override
    public void onRecordingDone(Recognizer recognizer) {
    }

    @Override
    public void onSoundDataRecorded(Recognizer recognizer, byte[] bytes) {
    }

    @Override
    public void onPowerUpdated(Recognizer recognizer, float v) {
    }

    @Override
    public void onPartialResults(Recognizer recognizer, Recognition recognition, boolean b) {
        if (getView() != null) {
            getView().getTranslatePanel().getTranslateEt()
                    .setText(recognition.getBestResultText());
        }
    }

    @Override
    public void onRecognitionDone(Recognizer recognizer, Recognition recognition) {
        if (getView() != null) {
            getView().getTranslatePanel().getTranslateEt().setText(recognition.getBestResultText());
            getView().getTranslatePanel().setVoiceMode(false);
        }
    }

    @Override
    public void onError(Recognizer recognizer, Error error) {
        if (!(error.getCode() == Error.ERROR_CANCELED) && getView() != null) {
            getView().showMessage("Произошла ошибка " + error.getString());
            resetRecognizer();
        }
    }

    private void resetRecognizer() {
        if (recognizer != null) {
            recognizer.cancel();
            recognizer = null;
        }
    }

	void playTextToSpeech() {
        resetVocalizer();
        // To create a new vocalizer, specify the language, the text to be vocalized, the auto play parameter
        // and the voice.
        vocalizer = Vocalizer.createVocalizer(currentLanguage.equals(RU_EN) ? Vocalizer.Language.RUSSIAN :
                Vocalizer.Language.ENGLISH, currentText, true, Vocalizer.Voice.JANE);
        // Set the listener.
        vocalizer.setListener(TranslatePresenter.this);
        // Don't forget to call start.
        vocalizer.start();
    }

	void playTextToSpeech(String text) {
        resetVocalizer();
        // To create a new vocalizer, specify the language, the text to be vocalized, the auto play parameter
        // and the voice.
        vocalizer = Vocalizer.createVocalizer(currentLanguage.equals(RU_EN) ? Vocalizer.Language.ENGLISH :
                Vocalizer.Language.RUSSIAN, text, true, Vocalizer.Voice.JANE);
        // Set the listener.
        vocalizer.setListener(TranslatePresenter.this);
        // Don't forget to call start.
        vocalizer.start();
    }

	void createAndStartRecognizer() {
        if (getView() != null && getView().checkAudioPermission()) {
            // Reset the current recognizer.
            resetRecognizer();
            // To create a new recognizer, specify the language, the model - a scope of recognition to get the most appropriate results,
            // mInvalidTextAnimatorSet the listener to handle the recognition events.
            recognizer = Recognizer.create(currentLanguage.equals(RU_EN) ? Recognizer.Language.RUSSIAN :
                            Recognizer.Language.ENGLISH,
                    Recognizer.Model.NOTES, TranslatePresenter.this);
            // Don't forget to call start on the created object.
            recognizer.start();
        }
    }

	void onRequestPermissionsResultHandler(int requestCode, int[] grantResults) {
        if (requestCode == TranslateFragment.REQUEST_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createAndStartRecognizer();
        }
    }
    //endregion

    //region ==================== VOCALIZER ====================
    @Override
    public void onSynthesisBegin(Vocalizer vocalizer) {
    }

    @Override
    public void onSynthesisDone(Vocalizer vocalizer, Synthesis synthesis) {
    }

    @Override
    public void onPlayingBegin(Vocalizer vocalizer) {
    }

    @Override
    public void onPlayingDone(Vocalizer vocalizer) {
    }

    @Override
    public void onVocalizerError(Vocalizer vocalizer, Error error) {
        if (!(error.getCode() == Error.ERROR_CANCELED) && getView() != null) {
            getView().showMessage("Произошла ошибка " + error.getString());
            resetVocalizer();
        }
    }

    private void resetVocalizer() {
        if (vocalizer != null) {
            vocalizer.cancel();
            vocalizer = null;
        }
    }

    @Override
    public void swapLanguage() {
        if (currentLanguage.equals(RU_EN)) {
            currentLanguage = EN_RU;
        } else {
            currentLanguage = RU_EN;
        }
    }

	String getCurrentLanguage() {
        return currentLanguage;
    }

	void addLastTranslateToFavorites() {
        mModel.updateLastItem();
    }

    //endregion


	//region ============================= DI =============================
    @dagger.Module
    class Module {
		@Provides
        @TranslateScope
        TranslateModel provideTranslateModel() {
            return new TranslateModel();
        }
    }

	@dagger.Component(modules = Module.class, dependencies = RootActivity.Component.class)
    @TranslateScope
    interface Component {
		void inject(TranslatePresenter presenter);
		RootPresenter getRootPresenter();
    }

    private void createDaggerComponent() {
        Component component = DaggerService.getComponent(Component.class);
        if (component == null) {
            component = DaggerTranslatePresenter_Component.builder()
                    .component(DaggerService.getComponent(RootActivity.Component.class))
                    .module(new Module())
                    .build();
            DaggerService.registerComponent(Component.class, component);
        }
        component.inject(this);
    }
    //endregion
}

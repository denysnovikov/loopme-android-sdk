package com.loopme.adview;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;

import com.loopme.common.Logging;
import com.loopme.common.StaticParams;
import com.loopme.constants.VideoState;
import com.loopme.constants.WebviewState;

/**
 * Custom ad view.
 * Communicate with javascript.
 */
public class AdView extends WebView implements
        BridgeInterface,
        Bridge.Listener {

    private static final String LOG_TAG = AdView.class.getSimpleName();
    public static final String DEFAULT_CHROME_USER_AGENT = "Dalvik/2.1.0 (Linux; U; Android 7.1.1; Nexus 6P Build/NMF26F)";
    public static final String CHROME_USER_AGENT = initUserAgent();

    private Bridge.Listener mBridgeListener;
    private volatile Bridge mBridge;

    private int mCurrentVideoState = VideoState.IDLE;
    private int mViewState = WebviewState.CLOSED;

    public AdView(Context context) {
        super(context);
        Logging.out(LOG_TAG, "AdView created");
        init();
    }

    private static String initUserAgent() {
        String agent = System.getProperty("http.agent");
        if (TextUtils.isEmpty(agent)) {
            agent = DEFAULT_CHROME_USER_AGENT;
        }
        return agent;
    }

    @Override
    public void scrollTo(int x, int y) {
        //nothing
    }

    @Override
    public void computeScroll() {
        //nothing
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY,
                                   int scrollRangeX, int scrollRangeY, int maxOverScrollX,
                                   int maxOverScrollY, boolean isTouchEvent) {
        return false;
    }

    public void addBridgeListener(Bridge.Listener listener) {
        mBridgeListener = listener;
    }

    private void init() {
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);

        webSettings.setPluginState(PluginState.ON);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        //added to tests
        if (Build.VERSION.SDK_INT >= 19 && StaticParams.DEBUG_MODE) {
            setWebContentsDebuggingEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSettings.setMediaPlaybackRequiresUserGesture(false);
        }
        webSettings.setSupportZoom(false);
        setWebChromeClient(new AdViewChromeClient());
        mBridge = new Bridge(this);
        setWebViewClient(mBridge);
    }

    public int getCurrentVideoState() {
        return mCurrentVideoState;
    }

    @Override
    public void setWebViewState(int state) {
        if (mViewState != state) {
            mViewState = state;
            String command = new BridgeCommandBuilder().webviewState(mViewState);
            loadUrl(command);
        }
    }

    @Override
    public void setFullscreenMode(boolean mode) {
        String command = new BridgeCommandBuilder().fullscreenMode(mode);
        loadUrl(command);
    }

    public int getCurrentWebViewState() {
        return mViewState;
    }

    @Override
    public void send360Event(String event) {
        String command = new BridgeCommandBuilder().event360(event);
        loadUrl(command);
    }

    @Override
    public void setVideoState(int state) {
        if (mCurrentVideoState != state) {
            mCurrentVideoState = state;
            Logging.out(LOG_TAG, "VIDEO : " + VideoState.toString(state));
            String command = new BridgeCommandBuilder().videoState(state);
            loadUrl(command);
        }
    }

    @Override
    public void setVideoDuration(int duration) {
        String command = new BridgeCommandBuilder().videoDuration(duration);
        loadUrl(command);
    }

    @Override
    public void setVideoCurrentTime(int currentTime) {
        String command = new BridgeCommandBuilder().videoCurrentTime(currentTime);
        loadUrl(command);
    }

    @Override
    public void setVideoMute(boolean mute) {
        Logging.out(LOG_TAG, "MUTE : " + mute);
        String command = new BridgeCommandBuilder().videoMute(mute);
        loadUrl(command);
    }

    public void shake() {
        Logging.out(LOG_TAG, "SHAKE");
        String command = new BridgeCommandBuilder().shake(true);
        loadUrl(command);
    }

    @Override
    public void sendNativeCallFinished() {
        String command = new BridgeCommandBuilder().isNativeCallFinished(true);
        loadUrl(command);
    }

    @Override
    public void onJsClose() {
        if (mBridgeListener != null) {
            mBridgeListener.onJsClose();
        }
    }

    @Override
    public void onJsLoadSuccess() {
        if (mBridgeListener != null) {
            mBridgeListener.onJsLoadSuccess();
        }
    }

    @Override
    public void onJsLoadFail(String mess) {
        if (mBridgeListener != null) {
            mBridgeListener.onJsLoadFail(mess);
        }
    }

    @Override
    public void onJsFullscreenMode(boolean isFullScreen) {
        if (mBridgeListener != null) {
            mBridgeListener.onJsFullscreenMode(isFullScreen);
        }
    }

    @Override
    public void onNonLoopMe(String url) {
        if (mBridgeListener != null) {
            mBridgeListener.onNonLoopMe(url);
        }
    }

    @Override
    public void onHtmlAdOpens() {
        if (mBridgeListener != null) {
            mBridgeListener.onHtmlAdOpens();
        }
    }

    @Override
    public void onJsVideoLoad(String videoUrl) {
        if (mBridgeListener != null) {
            mBridgeListener.onJsVideoLoad(videoUrl);
        }
    }

    @Override
    public void onJsVideoMute(boolean mute) {
        if (mBridgeListener != null) {
            mBridgeListener.onJsVideoMute(mute);
        }
    }

    @Override
    public void onJsVideoPlay(int time) {
        if (mBridgeListener != null) {
            mBridgeListener.onJsVideoPlay(time);
        }
    }

    @Override
    public void onJsVideoPause(int time) {
        if (mBridgeListener != null) {
            mBridgeListener.onJsVideoPause(time);
        }
    }

    @Override
    public void onJsVideoStretch(boolean b) {
        if (mBridgeListener != null) {
            mBridgeListener.onJsVideoStretch(b);
        }
    }
}
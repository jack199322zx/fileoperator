package test2;

import lombok.SneakyThrows;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author ss
 * @date 2018/8/24 14:42
 */
public class UrlOperator extends AbstractFileOperator {

    private String remoteUrl;

    public UrlOperator(Scheduler scheduler) {
        super(scheduler);
        this.remoteUrl = scheduler.getUrl();
    }

    @Override
    @SneakyThrows
    protected long getFileSize() {
        URL url = new URL(remoteUrl);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setRequestProperty("User-Agent", "NetFox");
        int responseCode = httpConnection.getResponseCode();
        if (responseCode >= 400) {
            processErrorCode(responseCode);
            //represent access is error
            return -1;
        }
        return httpConnection.getContentLength();
    }

    private void processErrorCode(int repCode) {

    }

}

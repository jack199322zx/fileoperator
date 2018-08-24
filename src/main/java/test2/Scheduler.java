package test2;

import lombok.Data;

import java.io.File;

/**
 * @author ss
 * @date 2018/8/24 14:26
 */
@Data
public class Scheduler {

    private String url; // 下载地址
    private String path; // 保存路径
    private String fileName; // 保存文件的名字
    private int splitNum; // 文件分成几段，默认是5段
    private DownloadType downloadType; // 如果为FALSE则是本地下载,为TRUE则URL下载
    private File downFile;

    public Scheduler(String url, String path, String fileName, int splitNum, DownloadType downloadType, File downFile) {
        this.url = url;
        this.path = path;
        this.fileName = fileName;
        this.splitNum = splitNum;
        this.downloadType = downloadType;
        this.downFile = downFile;
    }

    public enum DownloadType {
        LOCAL, URL
    }
}

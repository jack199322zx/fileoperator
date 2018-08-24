package utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;


@Slf4j
public class FileUtil {
    public static final String DATEFMT = "yyyy-MM-dd HH:mm:ss";

    public static File createFile(String destFileName) {
        File file = new File(destFileName);
        if (file.exists()) {
            log.debug("创建单个文件" + destFileName + "失败，目标文件已存在！");
            return null;
        }
        if (destFileName.endsWith(File.separator)) {
            log.debug("创建单个文件" + destFileName + "失败，目标文件不能为目录！");
            return null;
        }
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                log.debug("创建目标文件所在目录失败！");
                return null;
            }
        }
        try {
            if (file.createNewFile()) {
                log.debug("创建单个文件" + destFileName + "成功！");
                return file;
            } else {
                log.debug("创建单个文件" + destFileName + "失败！");
                return null;
            }
        } catch (IOException e) {
            log.debug("创建单个文件" + destFileName + "失败！" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static boolean deleteDirRecursively(File path) {
        if (path.exists() && path.isDirectory())
            for (File file : path.listFiles())
                if (file.isDirectory())
                    deleteDirRecursively(file);
                else
                    file.delete();

        return path.delete();
    }

    /**
     * 获取目录所属磁盘剩余容量
     */
    public static long getDiskFreeSize(String path) {
        File file = new File(path);
        return file.getFreeSpace();
    }

    /**
     * 查看文件或者文件夹大小
     */
    public static long getFileSize(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isFile()) {
                return file.length();
            } else {
                long size = 0;
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (File temp : files) {
                        if (temp.isFile()) {
                            size += temp.length();
                        }
                    }
                }
                return size;
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        createFile("aa/bb/cc/dd.txt");
    }
}

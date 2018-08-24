package test2;

import com.google.common.collect.Lists;
import demo.DownFileUtility;
import lombok.Cleanup;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.*;
import java.util.List;

/**
 * @author ss
 * @date 2018/8/24 14:17
 */
@Slf4j
public abstract class AbstractFileOperator {

    protected File temp;
    @Getter
    protected String tempFileName;
    protected long fileLength; // 文件长度
    private final String tempSubFix = ".info";
    @Getter
    protected int splitNum; // 分段数量

    public AbstractFileOperator(Scheduler scheduler) {
        this.tempFileName = scheduler.getPath() + File.separator + scheduler.getFileName();
        this.temp = new File(tempFileName + tempSubFix);
        this.splitNum = scheduler.getSplitNum();
    }
    protected abstract long getFileSize();

    /**
     * 保存下载信息（文件指针位置）
     */
    @SneakyThrows
    public void writePos(List<Pos> posList) {
        if (posList == null || posList.isEmpty()) return;
        @Cleanup DataOutputStream output = new DataOutputStream(new FileOutputStream(this.temp));
        int size;
        output.writeInt((size = posList.size()));
        for (int i = 0; i < size; i++) {
            Pos pos = posList.get(i);
            output.writeLong(pos.getStartPos());
            output.writeLong(pos.getStartPos());
        }
    }

    /**
     * 读取保存的下载信息（文件指针位置）
     */
    @SneakyThrows
    public List<Pos> readPos() {
        @Cleanup DataInputStream input = new DataInputStream(new FileInputStream(temp));
        int nCount = input.readInt();
        List<Pos> posList = Lists.newArrayList();
        while (nCount-- > 0) {
            Pos pos = new Pos(input.readLong(), input.readLong());
            posList.add(pos);
        }
        return posList;
    }

    public List<Pos> initPosWhenFirstRead() {
        List<Pos> posList = Lists.newArrayList();
        this.fileLength = getFileSize();
        if (fileLength == -1) {
            log.info("can not connect to remote file!");
            return null;
        }
        long splitPos = fileLength / splitNum;
        for (int i = 0; i < splitNum; i++) {
            Pos pos;
            if (i < splitNum - 1) {
                pos = new Pos(i * splitPos, (i + 1) * splitPos);
            } else {
                pos = new Pos(i * splitPos, fileLength);
            }
            posList.add(pos);
        }
        return posList;
    }

    public boolean ifExistTempFile() {
        return this.temp.exists();
    }

    /**
     * 合并文件
     */
    public void mergeFile(){
        try{
            File file = new File(tempFileName);
            if(file.exists()){
                file.delete();
            }
            RandomAccessFile saveInput = new RandomAccessFile(file,"rw");
            for (int i = 0; i < splitNum;i++){
                try {
                    RandomAccessFile input = new RandomAccessFile (new File(tempFileName + "_" + i),"r");
                    byte[] b = new byte[1024];
                    int nRead;
                    while ((nRead = input.read(b, 0, 1024)) > 0) {
                        saveInput.seek(saveInput.length());
                        saveInput.write(b, 0, nRead);
                    }
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//            DownFileUtility.log("file size is "+ saveInput.length());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}

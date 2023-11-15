package cn.abyss4393.utils.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className FTPUtils
 * @description TODO
 * @date 14/11/2023
 */
public class FTPUtils {

    private static final String FTP_ADDRESS = "47.109.22.92";

    private static final int FTP_PORT = 21;

    private static final String FTP_USERNAME = "ftp";

            private static final String FTP_PASSWORD = "c2r46bWcTPzBwrHw";

    private static final String FTP_BASE_PATH = "/www/wwwroot/ftp/audio";

    public static boolean upload(String fileName, InputStream is) throws IOException {
        boolean success = false;

        FTPClient ftpClient = new FTPClient();
        String type = fileName.substring(fileName.lastIndexOf(".") + 1);

        try {
            int reply;
            ftpClient.connect(FTP_ADDRESS, FTP_PORT);
            ftpClient.login(FTP_USERNAME, FTP_PASSWORD);
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                return false;
            }
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.setControlEncoding("UTF-8");
            if (!ftpClient.changeWorkingDirectory(FTP_BASE_PATH)) {
                ftpClient.makeDirectory(FTP_BASE_PATH);
            }
            ftpClient.changeWorkingDirectory(FTP_BASE_PATH);
            ftpClient.enterLocalPassiveMode();
            success = ftpClient.storeFile(fileName, is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                is.close();
                ftpClient.logout();
                ftpClient.disconnect();
            }
        }
        return success;
    }


}

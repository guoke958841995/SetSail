package com.sxhalo.PullCoal.ui;


import org.apache.commons.codec1.binary.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by amoldZhang on 2019/3/11.
 */

public class GZIPUtils {
    /**
     * 字符串压缩为GZIP字节数组
     *
     * @param bytes
     * @return
     */
    public static byte[] compress(byte[] bytes) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(bytes);
            gzip.close();
        } catch (IOException e) {
            throw new RuntimeException("GZIP压缩error", e);
        }
        return out.toByteArray();
    }

    /**
     * GZIP解压缩
     *
     * @param in
     * @return
     */
    public static byte[] uncompress(InputStream in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (Exception e) {
            throw new RuntimeException("GZIP解压error", e);
        }
        return out.toByteArray();
    }

    /**
     * GZIP解压缩
     *
     * @param bytes
     * @return
     */
    public static String uncompress(byte[] bytes) {
        return StringUtils.newString(uncompress(new ByteArrayInputStream(bytes)),"UTF-8");
    }

}

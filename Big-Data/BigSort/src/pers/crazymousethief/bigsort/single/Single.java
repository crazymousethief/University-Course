package pers.crazymousethief.bigsort.single;

import pers.crazymousethief.bigsort.io.OrderedInputStream;
import pers.crazymousethief.bigsort.io.SortOutputStream;
import pers.crazymousethief.bigsort.io.util.Helper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class Single {
    public static int id = 0;
    public static int prefix;

    public static void main(String[] args) throws IOException {
        String sourceFileName = args[0];
        String targetFileName = args[1];
        long splitSize = Integer.parseInt(args[2]);
        separate(splitSize, new FileInputStream(sourceFileName), 0);
        merge(targetFileName);
    }

    public static int separate(long splitSize, InputStream inputStream, int p) throws IOException {
        prefix = p;
        Helper.separate(splitSize, inputStream, () -> {
            OutputStream stream = null;
            try {
                var fileStream = new FileOutputStream(prefix + "_" + id++ + ".txt");
                stream = new SortOutputStream(splitSize, fileStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return stream;
        });
        return id;
    }

    public static void merge(String fileName) throws IOException {
        var v = new Vector<InputStream>();
        for (int i = 0; i < id; i++)
            v.add(new FileInputStream(prefix + "_" + i + ".txt"));
        var stream = new OrderedInputStream(v);
        Helper.inputStreamToWriter(stream, Helper.getFileWriter(fileName));
        for (int i = 0; i < id; i++)
            new File(i + ".txt").delete();
    }
}

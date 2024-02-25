package org.example;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileFinder {
    public List<File> findFile(File folder) throws Exception {
        List<File> resultFiles = new ArrayList<>();
        File[] files = folder.listFiles();
        if (files.length != 0){
            for (int i = 0; i < files.length; i++) {
//                if (files[i].isFile())
                    resultFiles.add(files[i]);
            }
            return resultFiles;
        }
        throw new Exception("Файлов в папке нет");
    }
}
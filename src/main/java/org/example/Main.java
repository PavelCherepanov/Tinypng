package org.example;

import java.io.*;
import java.util.List;

public class Main {
    private static final Tinify tinify = new Tinify("");
    private static final FileFinder fileFinder = new FileFinder();

    private static void walkingByDirs(String dir) throws Exception {
        List<File> files = fileFinder.findFile(new File(dir));
        String path = "";
        File currentFolder = new File(dir, "tiny");
        currentFolder.mkdir();
        for (int i = 0; i < files.size(); i++) {
            path = dir + "\\" + files.get(i).getName();
            if (new File(path).isFile()) {
                String uploadedLink = tinify.uploadFile(files.get(i));
                tinify.downloadFile(uploadedLink, new File(path).getParent() + "\\tiny\\" + files.get(i).getName());
            } else {
                walkingByDirs(path);
            }
        }
    }


    public static void main(String[] args) throws Exception {
//        walkingByDirs(args[0]);
        List<File> files = fileFinder.findFile(new File(args[0]));

        File currentFolder = new File(args[0], "tiny");
        currentFolder.mkdir();
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).isFile()) {
                String uploadedLink = tinify.uploadFile(files.get(i));
                tinify.downloadFile(uploadedLink, files.get(i).getParent() + "\\tiny\\" + files.get(i).getName());
            }

        }


    }
}

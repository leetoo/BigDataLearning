package com.hzgc.hbase.putdata;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PutDataUtil {
    public static Map<String,byte[]> getPhotoByName(String photoPath){
        String path = photoPath;
        File f = new File(path);
        Map<String,byte[]> map = new HashMap<>();
        if (!f.exists()){
            System.out.println(path + " not exists!");
        }
        File fa[] = f.listFiles();
        for (int i = 0; i < fa.length; i ++ ){
            File fs = fa[i];
            if (fs.isDirectory()){
                System.out.println(fs.getName() + "，这是个目录！！！");
            } else {
                String a = fs.getName().split(".jpg")[0];
                if (i % 2 == 0) {
                    a = "device-test-1-" + a;
                } else if (i % 3 == 0) {
                    a = "device-test-2-" + a;
                } else if (i % 5 ==0) {
                    a = "device-test-3-" + a;
                } else {
                    a = "device-test-5-" + a;
                }
                try {
                    byte[] photo = ImageToByte.image2byte(fs.getAbsolutePath());
                    map.put(a,photo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }
}

package com.sydney.dream.facecomdemo;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;

import java.io.File;

public class GetAllObjectFromInputKeys {

    public static void main(String[] args) {
        if (args.length != 6) {
            System.out.print("输入参数有误， args[0]: localObjectPath, 图片取出后放到的根路径。 \n" +
                    "args[1]: 需要取出的对象key， \n" +
                    "args[2]: cephGWBalanceNode ceph 对象网关负载均衡节点，haproxy 节点。 \n" +
                    "args[3]: accessKey 用于用户认证。 \n" +
                    "args[4]: secretKey 用于用户认证。 \n" +
                    "args[5]: bucketName bucketName。 \n");
            System.exit(0);
        }
        String localObjectPath = args[0];
        String key = args[1];
        String cephGWBalanceNode = args[2];
        String accessKey = args[3];
        String secretKey = args[4];
        String bucketName = args[5];
        // 链接对象
        AmazonS3 conn = AmazonS3Util.getAmazonS3Connection(accessKey,secretKey, cephGWBalanceNode);
        // 判断目录是否存在，不存在则创建目录
        File dir = new File(localObjectPath);
        if (!dir.exists()) {
            dir.mkdir();
        } else if (dir.exists() && dir.isFile()) {
            System.out.println("传入的路径是一个文件的绝对目录，请确认输入的是一个目录。");
            System.exit(0);
        }

        Bucket bucket = new Bucket(bucketName);
        if (AmazonS3Util.checkBucketExists(conn, bucketName)) {
            System.out.println("输入的bucketName: " + bucketName + ", 在ceph 集群中不存在。");
        }
        //获取对象，并且保存到localObjectPath 中;
        AmazonS3Util.getObject(conn, bucket, key, localObjectPath + key);


    }
}

package com.hzgc.hbase.putdata;

import com.hzgc.jni.FaceFunction;
import com.hzgc.jni.NativeFunction;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class CreateDataForDynamic {

    static {
        NativeFunction.init();
    }
    public static void main(String[] args) {
        if (args.length != 1){
            System.out.println("please pass one args(the root path of " +
                    " the pictures's path) to the main function.");
        }

        Properties props = new Properties();
        props.put("bootstrap.servers", "s103:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<String, String>(props);

        String photoPath = args[0];
        Map<String, byte[]> photoes = PutDataUtil.getPhotoByName(photoPath);
        Set<String> rowkeys = photoes.keySet();
        Iterator<String> rowkeysIt = rowkeys.iterator();
        while (rowkeysIt.hasNext()){
            String rowkey = rowkeysIt.next();
            byte[] photo = photoes.get(rowkey);
            String feature = "";
            if (photo != null && photo.length != 0 ){
                feature = FaceFunction.floatArray2string(FaceFunction.featureExtract(photo));
                if ("".equals(feature)){
                    continue;
                }
                producer.send(new ProducerRecord<String, String>("dynamic-demo", rowkey, feature));
            }
        }
    }
}

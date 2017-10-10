package com.hzgc.phoenix_suite;

import com.sydney.dream.HBaseHelper;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

public class PhonenixSuite {
    @Test
    public void testUTF8ToHbase(){
        Table table = HBaseHelper.getTable("objectinfo_test");
        Put put = new Put(Bytes.toBytes("00010001"));
        put.addColumn(Bytes.toBytes("person"), Bytes.toBytes("demotest"), Bytes.toBytes("demo"));
    }
}

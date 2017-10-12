package com.sydney.dream;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class EsApiSuite {

    public  TransportClient client = null;
    @Before
    public void inintClientEnv() throws UnknownHostException {
        System.out.println("====================call before testing===============================");
        Settings settings = Settings.builder()
                .put("cluster.name", "my-cluster").build();
        client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("ldl"), 9300));
    }
    @After
    public void closeClientOrOtherEnv(){
        System.out.println("====================call after testing===============================");
        client.close();
    }

    // IndexAPI
    @Test
    public void testIndexResponse() throws IOException {
//        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
//                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("s200"), 9300));
        IndexResponse response = client.prepareIndex("twitter", "tweet", "9")
                .setSource(jsonBuilder()
                        .startObject()
                        .field("user" ,"enhaye")
                        .field("postDate", new Date())
                        .endObject()).get();

        String _index = response.getIndex();
        String _type = response.getType();
        String _id = response.getId();
        long _version = response.getVersion();
        RestStatus status = response.status();
        System.out.println("_index: " + _index  + ", _type: " + _type + ", _id： " + _id + "_version: " + _version  + "status: " + status );
    }

    @Test
    public void testIndexResponse01(){
        Map<String, String> mapOfJson = new HashMap<String, String>();
        mapOfJson.put("name", "xiaohaong");
        mapOfJson.put("sex", "1");
        IndexResponse response1 = client.prepareIndex("objectinfo", "person", "row2").setSource(mapOfJson).get();
        System.out.println(response1.status());
    }
    @Test
    public void testGetResponse() throws UnknownHostException {
//        Settings settings = Settings.builder()
//                .put("cluster.name","my-cluster").build();
        GetResponse response = client.prepareGet("twitter", "tweet", "9").get();
//        System.out.println("user： " + response.getField("user") + " ,message： "
//                + response.getField("message") + " , postDate: " + response.getField("postDate"));
        System.out.println(response.isExists());
        Map<String, Object> source = response.getSource();
        if (source !=null ){
            Set<String> set = source.keySet();
            Iterator<String> iterator = set.iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                System.out.println("key: " + key + ", value: " + source.get(key));
            }
        }
    }

    // @Test
    public void testDeleteResponse01() throws UnknownHostException {
        DeleteResponse response = client.prepareDelete("index", "type", "1").get();
//        IndicesExistsRequest existsRequest = new IndicesExistsRequest("")
        System.out.println(response.status());
    }

    @Test
    public  void testUpdate01() throws ExecutionException, InterruptedException, IOException {
        IndexRequest indexRequest = new IndexRequest("index", "type", "2")
                .source(jsonBuilder()
                        .startObject()
                        .field("name", "Joe Smith")
                        .field("gender", "male")
                        .endObject());
        UpdateRequest updateRequest = new UpdateRequest("index", "type", "2")
                .doc(jsonBuilder()
                        .startObject()
                        .field("gender", "female")
                        .endObject())
                .upsert(indexRequest);
        client.update(updateRequest).get();
    }

    @Test
    public void testSearchByName(){
        SearchResponse response = client.prepareSearch("objectinfo")
                .setTypes("person")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.matchQuery("name", "z小王炸")).get();
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        System.out.println("length:" + searchHits.length);
        Map<String, Object> source = new HashMap<String, Object>();
        for (SearchHit searchHit: searchHits){
            System.out.println();
            source = searchHit.getSource();
            Set<String> keys = source.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                System.out.println("key: " + key + ",value: " + source.get(key));
            }
        }

    }

}
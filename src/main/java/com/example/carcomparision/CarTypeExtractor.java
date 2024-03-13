package com.example.carcomparision;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;

import com.couchbase.client.java.kv.GetResult;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Document;

public class CarTypeExtractor {
    private final Cluster cluster;
    private final Collection collection;

    public CarTypeExtractor(String connectionString, String username, String password, String bucketName) {
        cluster = Cluster.connect(connectionString, username, password);
        collection = cluster.bucket(bucketName).defaultCollection();
    }

    public void getDocument(String documentId) {
        try {
            GetResult getResult =collection.get(documentId) ;
            System.out.println("Document content: " + getResult.contentAsObject());
        } catch (CouchbaseException ex) {
            System.err.println("Error getting document: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        String connectionString = "couchbase://localhost:5984";
        String username = "shiva";
        String password = "shiva";
        String bucketName = "carlist";
        CarTypeExtractor example = new CarTypeExtractor(connectionString, username, password, bucketName);
        example.getDocument("abc");
    }
}
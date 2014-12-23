package com.bielu.oshift.service.rest.crime;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@Path("crime")
public class CrimeService {
  
  DBCollection collection;

  public CrimeService() throws UnknownHostException {
    MongoClient mongo = new MongoClient(
        new MongoClientURI(
            "mongodb://" 
                + getenv("OPENSHIFT_MONGODB_DB_HOST", "localhost") 
                + ":" + getenv("OPENSHIFT_MONGODB_DB_PORT", "27017")));
    
    collection = mongo.getDB("playground").getCollection("crimes");
  }

  @GET
  public List<Crime> getCrimeList() {
    List<Crime> list = new ArrayList<>();
    try (DBCursor cursor = collection.find()) {
      while (cursor.hasNext()) {
        DBObject res = cursor.next();
        Crime crime = new Crime();
        crime.id = UUID.fromString(res.get("id").toString());
        crime.title = res.get("title").toString();
        try {
          crime.date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(res.get("date").toString());
        } catch (ParseException e) {
          // ignore (?)
        }
        crime.solved = (Boolean) res.get("solved");
        list.add(crime);
      }
    }
    
    return list;
  }
  
  static String getenv(String variable, String defaultValue) {
    String result = System.getenv(variable);
    if (result == null) {
      return defaultValue;
    }
    
    return result;
  }
}
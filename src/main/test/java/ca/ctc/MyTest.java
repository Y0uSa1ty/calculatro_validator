package ca.ctc;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.io.FileReader;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import static org.testng.Assert.assertTrue;

public class MyTest {
    public String getJson(String id, String val) {
        return String.format("{\"%s\": \"%s\"}", id, val);
    }
 @BeforeTest

    @Test
    public void compareTest1(){
        assertTrue(false);
    }

    @Test
    public void compareTest2(){
        assertTrue(false);
    }

    @Test
    public void compareTest3(){
        assertTrue(false);
    }
}
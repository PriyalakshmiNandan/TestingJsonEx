import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class TestJson {
    static String path= System.getProperty("user.dir")+"//src//main//java//";
    static boolean flag=false;
    public static void main(String[] args) {
        testJsonAssesment();
    }
    private static void testJsonAssesment()
    {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(path+"test_results.json")) {
            JSONObject resultData = (JSONObject)jsonParser.parse(reader);
            System.out.println("Full data="+resultData);
            JSONArray testdetails= (JSONArray) resultData.get("test_suites");
            testdetails.forEach(data -> parseDataObject((JSONObject) data));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private static void parseDataObject(JSONObject data)
    {
        String suitename= (String) data.get("suite_name");
        String toBePrinted="******************************SUITE="+suitename+"*******************************************************";
        System.out.println(toBePrinted);
        writeLine(toBePrinted,flag);
        TestJson.flag=true;
        JSONArray testresults= (JSONArray) data.get("results");
        TreeMap<String, List<String>> finalPassResult= new TreeMap<String,List<String>>();
        TreeMap<String,List<String>> finalFailResult= new TreeMap<String,List<String>>();
        int blockedCount=0, timeTakenGT10=0;
        Double timeData = 0.0;
        for (int i=0; i<testresults.size();i++)
        {
           JSONObject d1= (JSONObject) testresults.get(i);
           if(d1.get("status").toString().equalsIgnoreCase("pass"))
           {
               ArrayList timeStatus= new ArrayList();
               timeStatus.add(d1.get("time"));
               timeStatus.add(d1.get("status"));
               finalPassResult.put(d1.get("test_name").toString(),timeStatus);
           }
           else if(d1.get("status").toString().equalsIgnoreCase("fail"))
           {
               ArrayList timeStatus= new ArrayList();
               timeStatus.add(d1.get("time"));
               timeStatus.add(d1.get("status"));
               finalFailResult.put(d1.get("test_name").toString(),timeStatus);
           }
           else if(d1.get("status").toString().equalsIgnoreCase("blocked"))
           {
               ++blockedCount;
           }
           try {
               timeData  = Double.parseDouble(d1.get("time").toString().trim());
           }
           catch(NumberFormatException e)
            {
                e.printStackTrace();
                timeData = 0.0;
            }
           if(timeData!= 0.0 && timeData >10.0)
           {
               ++timeTakenGT10;
           }
        }
        toBePrinted="-------------------------------------PASSED TEST CASES-------------------------------------------------";
        System.out.println(toBePrinted);
        writeLine(toBePrinted,flag);
        for (Map.Entry<String,List<String>> entry : finalPassResult.entrySet()) {
            toBePrinted="TestName: " + entry.getKey() + ", Time: " + entry.getValue().get(0)+", Status:"+entry.getValue().get(1);
            System.out.println(toBePrinted);
            writeLine(toBePrinted,flag);
        }
        toBePrinted="-------------------------------------FAILED TEST CASES--------------------------------------------------";
        System.out.println(toBePrinted);
        writeLine(toBePrinted,flag);
        for (Map.Entry<String,List<String>> entry : finalFailResult.entrySet()) {
            toBePrinted="TestName: " + entry.getKey() + ", Time: " + entry.getValue().get(0)+", Status:"+entry.getValue().get(1);
            System.out.println(toBePrinted);
            writeLine(toBePrinted,flag);
        }
        toBePrinted="-------------------------------------Total Blocked CASES------------------------------------------------";
        System.out.println(toBePrinted);
        writeLine(toBePrinted,flag);
        System.out.println(blockedCount);
        writeLine(""+blockedCount,flag);
        toBePrinted="-------------------------------------TimeTakenGreaterThan10Sec------------------------------------------";
        System.out.println(toBePrinted);
        writeLine(toBePrinted,flag);
        System.out.println(timeTakenGT10);
        writeLine(""+timeTakenGT10,flag);
    }
    private static void writeLine(String data,boolean flag)
    {
        FileOutputStream fos = null;
        try
        {
            fos= new FileOutputStream(new File(path+"parsed_results.txt"),flag);
            fos.write(data.getBytes());
            String lineSeparator = System.getProperty("line.separator");
            fos.write(lineSeparator.getBytes());
            fos.flush();
            fos.close();
        }
        catch(FileNotFoundException fe)
        {
            fe.printStackTrace();
        }
        catch (IOException ie)
        {
            ie.printStackTrace();
        }
    }
}

package org.KarateEjemplo;



import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        //String userDirectoryPath = System.getProperty("user.dir");
        //String userDirectoryPath2 =  userDirectoryPath + "\\src\\main\\java\\org\\KarateEjemplo\\jsonData\\archivo.json\"";
        //System.out.println(userDirectoryPath2);
        //System.out.println("Current Directory = \"" + userDirectoryPath + "\"" );



        /*String dataAevaluar = "pm.collectionVariables.set('bodys',getRandomInt(50))";
        if (dataAevaluar.contains("pm.collectionVariables.set")){
            String[] dataArray = dataAevaluar.split("pm.collectionVariables.set");
            String DataEval = dataArray[1];

            //asi mientras pille un regex
            String DataEval2 = DataEval.replace( "(","");
            String DataEval3 = DataEval2.replace(")","");
            System.out.println(DataEval3);
        };
*/
        String url_raw = "";
        JSONObject urlRequest = null;
        File archivo;
        String UrlEndpoint = null;
        String bodyRequestData = null;
        //JSONObject urlRequest_get = null;
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader("C:\\Users\\maxim\\IdeaProjects\\KarateEjemplo\\src\\main\\java\\org\\KarateEjemplo\\jsonData\\collection_example.postman_collection.json");
        FileWriter escritor =new FileWriter("C:\\Users\\maxim\\IdeaProjects\\KarateEjemplo\\src\\main\\java\\org\\KarateEjemplo\\jsonData\\collection_example.feature");
        //FileWriter fw=new FileWriter("C:\\Users\\maxim\\IdeaProjects\\KarateEjemplo\\src\\main\\java\\org\\KarateEjemplo\\jsonData\\jsondata.json");
        //Escribimos en el fichero un String y un caracter 97 (a)
        //fw.write("Esto es una prueb");
        //Cierro el stream
        //fw.close();
        Object obj=jsonParser.parse(reader);
        JSONObject empjsonobj = (JSONObject)obj;
        JSONArray JsonArrayInfo = (JSONArray) empjsonobj.get("item");
        JSONArray JsonArrayEvent = (JSONArray) empjsonobj.get("event");
        JSONArray JsonArrayVar = (JSONArray) empjsonobj.get("variable");
        JSONObject JsonInfoCollection = (JSONObject) empjsonobj.get("info");
        String nombreCollection = (String) JsonInfoCollection.get("name");

        try{
            archivo = new File(nombreCollection+".feature");
            if (archivo.createNewFile()){
                System.out.println("Se ha creado la feature");
            }
        }catch (IOException e){
            System.err.println("No se creo la feature");
        }

        escritor.write("Feature: "+nombreCollection+"\n");
        escritor.write("\n  Background:\n");
        for (int i = 0 ;i < JsonArrayVar.size(); i++){
            JSONObject jsonVar = (JSONObject) JsonArrayVar.get(i);
            Boolean estadoVar = (Boolean) jsonVar.get("disabled");
            String nombreVar = (String) jsonVar.get("key");
            String valorVar =  (String) jsonVar.get("value");
            if (estadoVar == null){
                escritor.write("* def "+nombreVar+" = '"+valorVar+"'\n");
            }
        }

        escritor.write("  * def preScript =\n\"\"\"\n");
        for (int i = 0 ;i < JsonArrayEvent.size(); i++){
            JSONObject jsonEvent = (JSONObject) JsonArrayEvent.get(i);
            String tipoEvento = (String) jsonEvent.get("listen");

            if (tipoEvento.equals("prerequest")){
                JSONObject scriptArray = (JSONObject) jsonEvent.get("script");
                JSONArray preScriptFuncion = (JSONArray) scriptArray.get("exec");
                for (int j = 0 ;j < preScriptFuncion.size(); j++){
                    escritor.write(preScriptFuncion.get(j).toString());
                }
            }
        }
        escritor.write("\n\"\"\"\n\n\n");


        for (int i = 0 ;i < JsonArrayInfo.size(); i++){

            JSONObject jsonData = (JSONObject) JsonArrayInfo.get(i);
            String nameScenario = (String) jsonData.get("name");
            //nombre del scenario o endpoint
            JSONObject jsonRequest = (JSONObject) jsonData.get("request");
            String requestMethod = (String) jsonRequest.get("method");
            //tipo de request
            JSONObject bodyRequest = (JSONObject) jsonRequest.get("body");

            var urlRequest_get = jsonRequest.get("url");


                if(urlRequest_get instanceof JSONObject){
                    System.out.println("tipoObjeto");
                    JSONObject url_obj = (JSONObject) jsonRequest.get("url");
                    url_raw = (String) url_obj.get("raw");
                    System.out.println(url_raw);
                }else if(urlRequest_get instanceof String){
                    System.out.println("Es de tipo String");
                    url_raw = (String) jsonRequest.get("url");
                    System.out.println(url_raw);
                }else{
                    System.out.println("Se escapa de las excepciones");
                }



            // if(requestMethod.equals("GET")){

            //}else{
               // 

            //}


            //REQUEST BODY MODE

            //BODY DATA
            //variables dentro del body



            escritor.write("  Scenario: "+nameScenario+"\n");
            if(requestMethod.equals("GET")){
                escritor.write("   Given url '"+url_raw+"'\n");
            }else{
                bodyRequestData = (String) bodyRequest.get("raw");
                String bodyRequestData1 = bodyRequestData.replace("{{","<");
                String bodyRequestData2 = bodyRequestData1.replace("}}",">");
                escritor.write("   * def bodyRequest =\n\"\"\"\n"+bodyRequestData2+"\n\"\"\"\n");
                escritor.write("   Given url '"+url_raw+"'\n");
                escritor.write("   And request bodyRequest\n");
            }
            escritor.write("   When method "+requestMethod+"\n");
            escritor.write("   Then status 200\n");
            escritor.write("   And print response\n");
            escritor.write("\n\n\n");


            System.out.println(nameScenario);
            System.out.println(requestMethod);
            System.out.println(bodyRequestData);
            //
        }
        escritor.close();

        //String name = (String) empjsonobj.get("name");



    }
}

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

        //FEATURE NAME
        escritor.write("Feature: "+nombreCollection+"\n");
        //COLLECTION VAR
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

//ESCRITURA DEL SCENARIO
        for (int i = 0 ;i < JsonArrayInfo.size(); i++){

//REASIGNAR LAS VARIABLES , INICIALIZARLAS AL PRINCIPIO
            JSONObject jsonData = (JSONObject) JsonArrayInfo.get(i);
            String nameScenario = (String) jsonData.get("name");
            //nombre del scenario
            JSONObject jsonRequest = (JSONObject) jsonData.get("request");

            JSONArray jsonEventRequest = (JSONArray) jsonData.get("event");

            String requestMethod = (String) jsonRequest.get("method");
            boolean metodosActualesConBody = requestMethod.equals("POST") || requestMethod.equals("PATCH") || requestMethod.equals("PUT");
            boolean contieneBody = true;
            JSONObject bodyRequest = (JSONObject) jsonRequest.get("body");
            var urlRequest_get = jsonRequest.get("url");
            System.out.println(bodyRequest);


            if (bodyRequest == null){
                contieneBody=false;
                System.out.println("No contiene body por lo cual o esta recien inicializado o es un get");
            }



            //NOMBRE SCENARIO
            escritor.write("  Scenario: "+nameScenario+"\n");

            //Condicionar PreScript

           /* escritor.write("  * def preRequestScript =\n\"\"\"\n");
            for (int l = 0 ;l < jsonEventRequest.size(); l++){
                JSONObject jsonEventR = (JSONObject) JsonArrayEvent.get(l);
                String tipoEvento = (String) jsonEventR.get("listen");

                if (tipoEvento.equals("prerequest")){
                    JSONObject scriptArray = (JSONObject) jsonEventR.get("script");
                    JSONArray preScriptFuncion = (JSONArray) scriptArray.get("exec");
                    for (int j = 0 ;j < preScriptFuncion.size(); j++){
                        escritor.write(preScriptFuncion.get(j).toString());
                    }
                }
            }*/
            //ARREGLAR

            //Headers

            JSONArray headerRequest = (JSONArray) jsonRequest.get("header");
            for (int j = 0 ;j < headerRequest.size(); j++){
                JSONObject jsonHeader = (JSONObject) headerRequest.get(j);
                Boolean estadoHeader = (Boolean) jsonHeader.get("disabled");
                String nombreHeader = (String) jsonHeader.get("key");
                String valorHeader =  (String) jsonHeader.get("value");
                if (estadoHeader == null) {
                    escritor.write("  * header "+nombreHeader+" = '"+valorHeader+"'\n");
                    System.out.println(nombreHeader+valorHeader);
                }
            }

            //SI EL REQUEST ES POST,GET,PUT,PATH SE GENERA EL BODY Y SE REMPLAZA LOS PARAMETROS PARA ASIGNAR LAS VARIABLES DENTRO DEL BODY
            if(contieneBody){
            if(metodosActualesConBody){

                bodyRequestData = (String) bodyRequest.get("raw");

                //pendiente a cambio , poca validacion del dato
                String bodyRequestData1 = bodyRequestData.replace("{{","<");
                String bodyRequestData2 = bodyRequestData1.replace("}}",">");
                escritor.write("   * def bodyRequest =\n\"\"\"\n"+bodyRequestData2+"\n\"\"\"\n");
            }
                System.out.println("REQUEST GET INGRESADA CON BODY VACIO O NULO , SE IGNORA BODY");
            }



            //SIEMPRE QUE LA URL SEA UN OBJETO, DADO QUE PUEDE VENIR EN TEXTO PLANO O EN UN OBJETO
            if(urlRequest_get instanceof JSONObject){
                System.out.println("tipoObjeto");
                JSONObject url_obj = (JSONObject) jsonRequest.get("url");
                url_raw = (String) url_obj.get("raw");
                String url_raw_mod = url_raw.replace("{{","'+");
                String url_raw_mod2 = url_raw_mod.replace("}}","+'");

                //ESCRITURA URL
                escritor.write("   Given url '"+url_raw_mod2+"'\n");

                JSONArray paramsArray = (JSONArray) url_obj.get("query");
                //En caso de necesitar params , aca esta el code pero la url los trae
                /*for (int j = 0 ;j < paramsArray.size(); j++){
                    JSONObject JsonParam = (JSONObject) paramsArray.get(j);
                    Boolean estadoParam = (Boolean) JsonParam.get("disabled");
                    String nombreParam = (String) JsonParam.get("key");
                    String valorParam =  (String) JsonParam.get("value");
                    if (estadoParam == null) {
                        escritor.write("  And param "+nombreParam+" = '"+valorParam+"'\n");
                        System.out.println(nombreParam+valorParam);
                    }
                }*/
            }else if(urlRequest_get instanceof String){
                //REVISAR Y LIMPIAR
                System.out.println("Es de tipo String");
                url_raw = (String) jsonRequest.get("url");
                String url_raw_mod = url_raw.replace("{{","'+");
                String url_raw_mod2 = url_raw_mod.replace("}}","'+");
                escritor.write("   Given url '"+url_raw_mod2+"'\n");
            }else{
                System.out.println("Se escapa de las excepciones");
            }

            //pasando parametros de body
            if(metodosActualesConBody){
                escritor.write("   And request bodyRequest\n");
            }

            escritor.write("   When method "+requestMethod+"\n");
            escritor.write("   Then status 200\n");
            escritor.write("   And print response\n");
            escritor.write("\n\n\n");






        //String name = (String) empjsonobj.get("name");



    }
        escritor.close();
}
}

package SynonymTranslateProject;

import Model.TranslateJson;
import Model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;

public class SynonymAndTranslateMain
{
    public static void main(String args[]) throws Exception {
        String filePath = new File(".").getCanonicalPath() + "\\EsAnlam.xlsx";

        FileInputStream file = new FileInputStream(filePath);

        //Create Workbook instance holding reference to .xlsx file
        XSSFWorkbook workbook = new XSSFWorkbook(file);

        //Get first/desired sheet from the workbook
        XSSFSheet sheet = workbook.getSheetAt(0);

        //Iterate through each rows one by one
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext())
        {
            Row row = rowIterator.next();

            if(row.getRowNum() == 0) {
                // baslik satirini atla
                continue;
            }
            Cell cell1 = row.getCell(0);
            String word1 = cell1.getStringCellValue();
            Cell cell2 = row.getCell(1);
            String word2 = cell2.getStringCellValue();
            Cell cell3 = row.getCell(2);
            String word3 = cell3.getStringCellValue();
            Cell cell4 = row.getCell(3);
            String word4 = cell3.getStringCellValue();
            Cell cell5 = row.getCell(4);
            String word5 = cell3.getStringCellValue();
            String translateWord1 = TranslateWord(word1.toLowerCase(Locale.ROOT));
            List<String> synonymList = SynoynmList(translateWord1.toLowerCase(Locale.ROOT));
            if(!IsContainsWordInSynonymList(word2,translateWord1, synonymList)){
                cell2.setCellValue("");
            }
            if(!IsContainsWordInSynonymList(word3,translateWord1, synonymList)){
                cell3.setCellValue("");
            }
            if(!IsContainsWordInSynonymList(word4,translateWord1, synonymList)){
                cell4.setCellValue("");
            }
            if(!IsContainsWordInSynonymList(word5,translateWord1, synonymList)){
                cell5.setCellValue("");
            }
            Cell cell6 = row.getCell(5);
            if(cell6 == null){
                row.createCell(5);
                cell6 = row.getCell(5);
                cell6.setCellValue(translateWord1);
            }else{
                cell6.setCellValue(translateWord1);
            }

        }
        file.close();
        FileOutputStream outputStream = new FileOutputStream(filePath);
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();

    }

    private static boolean IsContainsWordInSynonymList(String word2, String translatedWord1, List<String> synonymList) throws Exception {
        String translatedWord2 = TranslateWord(word2);
        if(translatedWord1.equals(translatedWord2) || synonymList.contains(translatedWord2)){
            return true;
        }
        return false;
    }

    static String TranslateWord(String word) throws Exception {
       /* HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://google-translate1.p.rapidapi.com/language/translate/v2"))
                .header("content-type", "application/x-www-form-urlencoded")
                .header("accept-encoding", "application/gzip")
                .header("x-rapidapi-host", "google-translate1.p.rapidapi.com")
                .header("x-rapidapi-key", "4eea85ba3amsh18b5a0c2d412680p1bca3cjsn7d5ea7ef2ca7")
                .method("POST", HttpRequest.BodyPublishers.ofString("q="+word+"&target=en&source=tr"))
                .build();*/
        /*HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://text-translator2.p.rapidapi.com/translate"))
                .header("content-type", "application/x-www-form-urlencoded")
                .header("x-rapidapi-host", "text-translator2.p.rapidapi.com")
                .header("x-rapidapi-key", "4eea85ba3amsh18b5a0c2d412680p1bca3cjsn7d5ea7ef2ca7")
                .method("POST", HttpRequest.BodyPublishers.ofString("source_language=tr&target_language=en&text="+ word))
                .build();*/
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://deep-translate1.p.rapidapi.com/language/translate/v2"))
                .header("content-type", "application/json")
                .header("x-rapidapi-host", "deep-translate1.p.rapidapi.com")
                .header("x-rapidapi-key", "4eea85ba3amsh18b5a0c2d412680p1bca3cjsn7d5ea7ef2ca7")
                .method("POST", HttpRequest.BodyPublishers.ofString("{\r\"q\": \""+word +"\",\r\"source\": \"tr\",\r\"target\": \"en\"\r}"))
        .build();
    HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    System.out.println(response.body());
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new com.howtodoinjava.demo.gson.LocalDateAdapter())
                .create();

        TranslateJson translateJson = gson.fromJson(response.body(), TranslateJson.class);
        if(translateJson != null && translateJson.data != null && translateJson.data.translations != null)
            return translateJson.data.translations.translatedText;
        else
            throw new Exception("Eş anlamlı sözcük yoktur.");
    }

    static List<String> SynoynmList(String word) throws IOException, InterruptedException {
        //String uri = "https://synonyms-word-info.p.rapidapi.com/v1/synonyms?str=%3C+" +word +"%3E";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://synonyms-word-info.p.rapidapi.com/v1/synonyms?str=%3C+" + word +"%3E"))
                .header("x-rapidapi-host", "synonyms-word-info.p.rapidapi.com")
                .header("x-rapidapi-key", "4eea85ba3amsh18b5a0c2d412680p1bca3cjsn7d5ea7ef2ca7")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new com.howtodoinjava.demo.gson.LocalDateAdapter())
                .create();

        User user = gson.fromJson(response.body(), User.class);
        List<String> synonymList = new ArrayList<String>();
        for(int i = 0 ; i< user.data.synonyms.length; i++) {
            synonymList.add(user.data.synonyms[i][0].toLowerCase(Locale.ROOT));
        }
        return synonymList;
    }
} // end of the class

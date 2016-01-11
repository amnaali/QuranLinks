
package dilp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
public static int[] chapters = new int[114];

    public static String html2text(String html) {
      Document d = Jsoup.parse(html);
      return d.text();
    }
    
    public static void LinkFirstCategory(File folder) throws IOException, Exception{
      for (File textFile : folder.listFiles()) {
        StringBuilder text = new StringBuilder(readDataFromTextFile(textFile));
        Pattern pattern = Pattern.compile("\\d+:\\d+(\\-\\d+)*");
        Matcher matcher = pattern.matcher(text);
        int start =0;
        while (matcher.find(start)) {
          String replacement = CreateLink(matcher.group());
          text.replace(matcher.start(), matcher.end(), replacement);  
          start=matcher.start()+replacement.length();    
        }
        writeDataOnTextFile(textFile.getName(), text.toString());
      }
    }
    
    public static String CreateLink(String reference){
      String referenceLink = reference;
      String[] numbers = reference.split(":");
      int chapter = Integer.parseInt(numbers[0]);
      String[] verseNumbers = numbers[1].split("\\-");
      int verseStart = Integer.parseInt(verseNumbers[0]);
      int verseEnd = 0; 
      if (verseNumbers.length==2){
        verseEnd = Integer.parseInt(verseNumbers[1]);
      }
      if (verifyVerse(chapter,verseStart, verseEnd)){   
        String verseEndInLink = verseEnd==0 ? "":"-"+verseEnd;
        referenceLink = "<a href=\"quran/"+chapter+"/"+verseStart+
                verseEndInLink+"\">"+reference+"</a>";
      }
      return referenceLink;
    }
    
    public static boolean verifyVerse(int chapter, int verseStart, int verseEnd) {
    if (chapter<=0 || chapter>chapters.length)
      return false;
    if (verseStart>chapters[chapter-1] || verseStart<=0 || verseEnd>chapters[chapter-1] || verseEnd<0)
      return false;
    return true;
  }
  
  public static String readDataFromTextFile(File textFile) throws Exception{
        FileInputStream fis = new FileInputStream(textFile);
        byte[] data = new byte[(int) textFile.length()];
        fis.read(data);
        fis.close();
        return new String(data, "UTF-8");
    }
    
    public static void writeDataOnTextFile(String textFileName, String text) throws FileNotFoundException, UnsupportedEncodingException{
        PrintWriter writer = new PrintWriter("D:\\files\\output\\"+textFileName, "UTF-8");
        writer.println(text);
        writer.close();
    }
        
    public static void insertLinkSentenceInDatabase(File folder) throws FileNotFoundException, UnsupportedEncodingException, Exception{
      Connection conn;
      conn = DriverManager.getConnection("jdbc:mysql://localhost:3308/test?characterEncoding=UTF-8", "root", "");
      for (File textFile : folder.listFiles()) {
         String text = readDataFromTextFile(textFile);
         String[] str = SentenceBoundaryDemo.main(text);
         for (int i=0; i<str.length;i++){
           if (str[i]==null) break;
           if (str[i].contains("a href=\"quran")){
               Statement stmt = conn.createStatement();
               String sql = "INSERT INTO `links1`(`id`, `linkText`) VALUES (?,?);";
               PreparedStatement pstmt = conn.prepareStatement(sql);
               pstmt.setInt(1,Integer.parseInt(textFile.getName().replace(".txt", "")));
               pstmt.setString(2, str[i]);
               pstmt.execute();
            }          
          }
        }
      }
    
    public static void main(String[] args1) throws Exception {
      BufferedReader br = new BufferedReader(new FileReader("D:/files/quranlinks.csv"));
      String line;
      while ((line = br.readLine()) != null) {
       int ch = Integer.parseInt(line.split(",")[0]); 
       int v = Integer.parseInt(line.split(",")[1]);
       chapters[ch-1]=v;
      }
    LinkFirstCategory(new File("D:/files/emStrongRegexp"));
    insertLinkSentenceInDatabase(new File("D:/files/output"));
  }

}

//26137
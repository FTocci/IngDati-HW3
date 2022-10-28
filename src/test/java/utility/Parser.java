package utility;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class Parser {

    private FileReader f;
    private BufferedReader b;
    private String s;
    private org.apache.lucene.index.IndexWriter writer;
    private int count;

    public Parser(String file, org.apache.lucene.index.IndexWriter writer) throws IOException {
        this.f = new FileReader(file);
        this.b = new BufferedReader(f);
        this.s = new String();
        this.writer = writer;
    }

	public void parse() throws IOException {

        /**
         * parsing
         */
		this.count=0;
		for (this.count = 0;; this.count++) {
        	s = b.readLine();

            if (s == null)
                break;

            JsonElement jsonTree = JsonParser.parseString(s);
            JsonObject table = jsonTree.getAsJsonObject();

            Document doc = new Document();
            doc.add(new TextField("index", String.valueOf(this.count), Field.Store.YES));

            JsonArray cells = table.getAsJsonArray("cells");
            int length = cells.size();
            for (int j = 0; j < length; j++) {
                JsonObject jsonobject = cells.get(j).getAsJsonObject();
                if (jsonobject.get("isHeader").getAsBoolean() == false){
                    String cell = jsonobject.get("cleanedText").getAsString();
                    if (cell.equals("") == false)
                        doc.add(new StringField("contenuto", cell, Field.Store.NO));
                }
                
            }

            writer.addDocument(doc);
            if (this.count % 100000 == 0) {
            	System.out.println("Documento indicizzato # " + this.count);
            	writer.commit();
            }
                
        }
        System.out.println("Documento indicizzato # " + this.count);
        writeCount(this.count);
        b.close();
        f.close();
    }

    private void writeCount(int count2) throws IOException {
        FileOutputStream f = new FileOutputStream("files/parsedSize.luc");
        f.write(count2);
        f.close();
    }
}

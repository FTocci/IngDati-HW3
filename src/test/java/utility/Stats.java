package utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Stats {

    private String inputFile;

    public Stats(String inputFile) throws IOException {
        this.inputFile = inputFile;
    }

    private void collectNumberOfRowsAndColumns() throws IOException {

        FileReader f = new FileReader(this.inputFile);
        BufferedReader b = new BufferedReader(f);
        String s = new String();

        int i, sommaRighe=0, sommaColonne=0;
        System.out.println("Calcolo stats in corso.");
        for (i = 0;; i++) {

            s = b.readLine();
            if (s == null)
                break;

            if (i == 300000)
                System.out.println("Attendere...");
            JsonElement jsonTree = JsonParser.parseString(s);
            JsonObject table = jsonTree.getAsJsonObject();
            JsonObject dimensioni = table.getAsJsonObject("maxDimensions");
            // JsonArray cells = table.getAsJsonArray("cells");

            int righe = dimensioni.get("row").getAsInt();
            int colonne = dimensioni.get("column").getAsInt();

            sommaRighe += righe;
            sommaColonne += colonne;
        }
        System.out.println("Terminato!");
        System.out.println("\n===== Stats =====");
        System.out.println("numero totale di tabelle = " + i);
        System.out.println("\n===== valori medi =====");
        System.out.println("numero medio di righe= " + sommaRighe / i);
        System.out.println("numero medio di colonne= " + sommaColonne / i);

        b.close();
        f.close();
    }

    private void collectNumberOfNullValues() throws IOException {

        FileReader f = new FileReader(this.inputFile);
        BufferedReader b = new BufferedReader(f);
        String s = new String();

        int i, sommaNulli=0;
        System.out.println("Calcolo null values stats. Attendere...");
        for (i = 0;; i++) {

            s = b.readLine();
            if (s == null)
                break;

            if (i == 300000)
                System.out.println("Attendere...");
            JsonElement jsonTree = JsonParser.parseString(s);
            JsonObject table = jsonTree.getAsJsonObject();
            JsonArray cells = table.getAsJsonArray("cells");

            for (int j = 0; j < cells.size(); j++) {
                if (cells.get(j).getAsJsonObject().get("cleanedText").getAsString().equals(""))
                    sommaNulli++;
            }

        }
        b.close();
        f.close();
        System.out.println("numero medio nulli = " + sommaNulli / i);
    }

    private void collectDistributionOfRowsAndColumns() throws IOException {

        FileReader f = new FileReader(this.inputFile);
        BufferedReader b = new BufferedReader(f);
        String s = new String();

        HashMap<Integer, Integer> distRow = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> distCol = new HashMap<Integer, Integer>();

        int i;
        System.out.println("Calcolo stats su righe e colonne. Attendere...");
        for (i = 0;; i++) {

            s = b.readLine();
            if (s == null)
                break;

            if (i == 300000)
                System.out.println("Attendere...");

            JsonElement jsonTree = JsonParser.parseString(s);
            JsonObject table = jsonTree.getAsJsonObject();
            JsonObject dimensioni = table.getAsJsonObject("maxDimensions");

            int righe = dimensioni.get("row").getAsInt();
            int colonne = dimensioni.get("column").getAsInt();

            if (distRow.containsKey(righe))
                distRow.put(righe, distRow.get(righe) + 1);
            else
                distRow.put(righe, 1);

            if (distCol.containsKey(colonne))
                distCol.put(colonne, distCol.get(colonne) + 1);
            else
                distCol.put(colonne, 1);
        }
        System.out.println("Done!");

        System.out.println("\n===== Distribuzione numero di righe =====");
        distRow.forEach((k, v) -> System.out.println(v + " tabelle hanno " + k + " righe."));
        System.out.println("\n===== Distribuzione numero di colonne =====");
        distCol.forEach((k, v) -> System.out.println(v + " tabelle hanno " + k + " colonne."));

        b.close();
        f.close();
    }

    private void collectNumberOfDistinctValuesForColumn() throws IOException {

        FileReader f = new FileReader(this.inputFile);
        BufferedReader b = new BufferedReader(f);
        String s = new String();

        HashMap<Integer, Integer> differenti = new HashMap<Integer, Integer>();

        int i;
        System.out.println("Calcolo numero valori distinti per colonna. Attendere...");
        for (i = 0;; i++) {
            s = b.readLine();
            if (s == null)
                break;

            if (i == 300000)
                System.out.println("Attendere...");

            JsonElement jsonTree = JsonParser.parseString(s);
            JsonObject table = jsonTree.getAsJsonObject();

            JsonArray cells = table.getAsJsonArray("cells");

            int colonne = table.getAsJsonObject("maxDimensions").get("column").getAsInt();

            HashMap<Integer, HashSet<String>> contaColonne = new HashMap<Integer, HashSet<String>>();
            for (int j = 0; j <= colonne; j++) {
                contaColonne.put(j, new HashSet<String>());
            }

            int length = cells.size();
            for (int j = 0; j < length; j++) {
                JsonObject cell = cells.get(j).getAsJsonObject();
                int colonna = cell.getAsJsonObject("Coordinates").get("column").getAsInt();
                if (!cell.get("isHeader").getAsBoolean() && !cell.get("cleanedText").getAsString().equals("")) {
                    contaColonne.get(colonna).add(cell.get("cleanedText").getAsString());
                }
            }
            contaColonne.forEach((k, v) -> {
                int size = v.size();
                if (size != 0) {
                    if (differenti.containsKey(size))
                        differenti.put(size, differenti.get(size) + 1);
                    else
                        differenti.put(size, 1);
                }
            });
        }
        b.close();
        f.close();
        System.out.println("\n===== Distribuzione numero di valori differenti per colonna =====");
        differenti.forEach((k, v) -> System.out.println(v + " colonne hanno " + k + " valori distinti."));

    }

    public void calculateStats() throws IOException {
        collectNumberOfRowsAndColumns();
        collectNumberOfNullValues();
        collectDistributionOfRowsAndColumns();
        collectNumberOfDistinctValuesForColumn();
        System.out.println("Stats terminato");
    }
}

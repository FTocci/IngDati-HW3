package utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Statistiche extends Thread {

	private String inputFile;
	private CollectNumberOfRowsAndColumns collect1;
	private CollectNumberOfNullValues collect2;
	private CollectDistributionOfRowsAndColumns collect3;
	private collectNumberOfDistinctValuesForColumn collect4;

	public Statistiche(String inputFile) throws IOException {
		this.inputFile = inputFile;
		this.collect1=new CollectNumberOfRowsAndColumns(inputFile);
		this.collect2=new CollectNumberOfNullValues(inputFile);
		this.collect3=new CollectDistributionOfRowsAndColumns(inputFile);
		this.collect4=new collectNumberOfDistinctValuesForColumn(inputFile);
	}
	
	public void calculateStats() throws IOException {
        new Thread(collect1).start();
        new Thread(collect2).start();
        new Thread(collect3).start();
        new Thread(collect4).start();
    }

	static class CollectNumberOfRowsAndColumns implements Runnable{

		private String inputFile;
		private BufferedReader b;
		private FileReader f;

		public CollectNumberOfRowsAndColumns(String inputFile) throws FileNotFoundException {
			this.inputFile = inputFile;
			f = new FileReader(this.inputFile);
			b = new BufferedReader(f);
		}

		@Override
		public void run() {
			long timeA = System.currentTimeMillis();
			String s = new String();

			int i, sommaRighe=0, sommaColonne=0;
			for (i = 0;; i++) {

				try {
					s = b.readLine();
					if (s == null)
						break;

					JsonElement jsonTree = JsonParser.parseString(s);
					JsonObject table = jsonTree.getAsJsonObject();
					JsonObject dimensioni = table.getAsJsonObject("maxDimensions");
					// JsonArray cells = table.getAsJsonArray("cells");

					int righe = dimensioni.get("row").getAsInt();
					int colonne = dimensioni.get("column").getAsInt();

					sommaRighe += righe;
					sommaColonne += colonne;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			System.out.println("\n===== Numero di tabelle =====");
			System.out.println("numero totale di tabelle = " + i);
			System.out.println("\n===== numero medio di righe e colonne =====");
			System.out.println("numero medio di righe= " + sommaRighe / i);
			System.out.println("numero medio di colonne= " + sommaColonne / i);

			try {
				b.close();
				f.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			long timeB = System.currentTimeMillis();
			System.out.println("\n-----------------------------Fine elaborazione. CollectNumberOfRowsAndColumns: " + (timeB - timeA) + "ms");

		}	
	}

	static class CollectNumberOfNullValues implements Runnable{

		private String inputFile;
		private BufferedReader b;
		private FileReader f;

		public CollectNumberOfNullValues(String inputFile) throws FileNotFoundException {
			this.inputFile = inputFile;
			f = new FileReader(this.inputFile);
			b = new BufferedReader(f);
		}

		@Override
		public void run() {
			long timeA = System.currentTimeMillis();
			String s = new String();

			int i, sommaNulli=0;
			for (i = 0;; i++) {

				try {
					s = b.readLine();
					if (s == null)
						break;

					JsonElement jsonTree = JsonParser.parseString(s);
					JsonObject table = jsonTree.getAsJsonObject();
					JsonArray cells = table.getAsJsonArray("cells");

					for (int j = 0; j < cells.size(); j++) {
						if (cells.get(j).getAsJsonObject().get("cleanedText").getAsString().equals(""))
							sommaNulli++;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}


			}
			try {
				b.close();
				f.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("\n===== valori medi nulli =====");
			System.out.println("numero medio nulli = " + sommaNulli / i);

			long timeB = System.currentTimeMillis();
			System.out.println("\n---------------------------------Fine elaborazione. CollectNumberOfNullValues: " + (timeB - timeA) + "ms");
		}

	}

	static class CollectDistributionOfRowsAndColumns implements Runnable{

		private String inputFile;
		private BufferedReader b;
		private FileReader f;

		public CollectDistributionOfRowsAndColumns(String inputFile) throws FileNotFoundException {
			this.inputFile = inputFile;
			f = new FileReader(this.inputFile);
			b = new BufferedReader(f);
		}
		
		@Override
		public void run() {
			long timeA = System.currentTimeMillis();
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			String s = new String();

	        HashMap<Integer, Integer> distRow = new HashMap<Integer, Integer>();
	        HashMap<Integer, Integer> distCol = new HashMap<Integer, Integer>();

	        int i;
	        for (i = 0;; i++) {

	            try {
					s = b.readLine();
					if (s == null)
		                break;


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
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
	        }

	        System.out.println("\n===== Distribuzione numero di righe =====");
	        distRow.forEach((k, v) -> System.out.println(v + " tabelle hanno " + k + " righe."));
	        System.out.println("\n===== Distribuzione numero di colonne =====");
	        distCol.forEach((k, v) -> System.out.println(v + " tabelle hanno " + k + " colonne."));

	        try {
				b.close();
		        f.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        long timeB = System.currentTimeMillis();
			System.out.println("\n----------------------------Fine elaborazione. CollectDistributionOfRowsAndColumns: " + (timeB - timeA) + "ms");
			
		}
		
	}

	static class collectNumberOfDistinctValuesForColumn implements Runnable{

		private String inputFile;
		private BufferedReader b;
		private FileReader f;

		public collectNumberOfDistinctValuesForColumn(String inputFile) throws FileNotFoundException {
			this.inputFile = inputFile;
			f = new FileReader(this.inputFile);
			b = new BufferedReader(f);
		}
		
		@Override
		public void run() {
			long timeA = System.currentTimeMillis();
//			try {
//				Thread.sleep(1500);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			String s = new String();

	        HashMap<Integer, Integer> differenti = new HashMap<Integer, Integer>();

	        int i;
	        for (i = 0;; i++) {
	            try {
					s = b.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            if (s == null)
	                break;

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
	        try {
				b.close();
				f.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        System.out.println("\n===== Distribuzione numero di valori differenti per colonna =====");
	        differenti.forEach((k, v) -> System.out.println(v + " colonne hanno " + k + " valori distinti."));
		
	        long timeB = System.currentTimeMillis();
			System.out.println("\n----------------------------Fine elaborazione. collectNumberOfDistinctValuesForColumn: " + (timeB - timeA) + "ms");
		}
		
	}
}

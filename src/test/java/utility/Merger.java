package utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.Map.*;

import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;

public class Merger {

    private IndexReader reader;
    private IndexSearcher searcher;
    private Map<String, Integer> set2count;

    public Merger(Directory directory) throws IOException {
        this.reader = DirectoryReader.open(directory);
        this.searcher = new IndexSearcher(reader);
        this.set2count = new HashMap<String, Integer>();
    }

    public void merge(LinkedList<String> ls) throws IOException {

        System.out.println("\nMerging in corso\n");

        int size = 0;

        try {
            FileInputStream f = new FileInputStream("files/parsedSize.luc");
            size = f.read();
            f.close();            
        } catch (Exception e) {
            System.out.println("Attento, non esiste nessun file che contenga la dimensione. Forse non hai effettuato la fase di parsing," +
            "oppure vuoi inserire una dimensione a mano?");
            Scanner scanner = new Scanner(System.in);
            size = scanner.nextInt();
            scanner.close();
        }

        System.out.println("Sto cercando " + ls.size() + " elementi.\n");

        for (String s : ls) {

            System.out.println("Sto cercando " + "\"" + s + "\".\n");

            Query query = new TermQuery(new Term("contenuto", s));
            TopDocs hits = searcher.search(query,size);
            for (int i = 0; i < hits.scoreDocs.length; i++) {
                ScoreDoc scoreDoc = hits.scoreDocs[i];
                Document doc = searcher.doc(scoreDoc.doc);
                String index = String.valueOf(doc.get("index"));

                if (this.set2count.containsKey(index)) {
                    this.set2count.replace(index, this.set2count.get(index) + 1);
                    System.out.println("Ho trovato di nuovo l'elemento " + s);
                }
                else
                    set2count.put(index, 1);
            }
        }

        List<Entry<String, Integer>> list = new LinkedList<>(this.set2count.entrySet());

        Collections.sort(list, new Comparator<Object>() {

            @SuppressWarnings("unchecked")
            public int compare(Object o2, Object o1) {
                return ((Comparable<Integer>) ((Map.Entry<String, Integer>) (o1)).getValue())
                        .compareTo(((Map.Entry<String, Integer>) (o2)).getValue());
            }
        });
        
        int i=0;

        System.out.println("\nRisultati dell'algoritmo di merge:");
        for (Iterator<Entry<String, Integer>> it = list.iterator(); it.hasNext()&& i<10;i++) {
            Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) it.next();
            System.out.println("doc" + entry.getKey() + " = " + entry.getValue());
        }
    }
}

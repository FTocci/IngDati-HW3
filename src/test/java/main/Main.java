package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Scanner;

import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import utility.Merger;
import utility.Parser;
import utility.Statistiche;

public class Main {

	public static void main(String[] args) throws IOException {

		/**
		 * dichiarazione variabili
		 */
		long timeA = System.currentTimeMillis();
		Path path = Paths.get("target/index");
		Directory directory = FSDirectory.open(path);
		IndexWriterConfig config = new IndexWriterConfig();
		config.setCodec(new SimpleTextCodec());

		IndexWriter writer = new IndexWriter(directory, config);

		/**
		 * lettura dal file di config
		 */
		FileReader f = new FileReader("config.txt");
		BufferedReader b = new BufferedReader(f);

		LinkedList<String> ls = new LinkedList<String>();
		String s = b.readLine();
		do {
			ls.add(s);
			s = b.readLine();
		} while (s != null);

		f.close();
		b.close();

		String confPath = ls.poll();

		Scanner scanner = new Scanner(System.in);
		System.out.println("Premi y per eseguire parsing, altrimenti qualsiasi carattere");
		String c = scanner.next();

		if (c.equals("y")) {
			System.out.println("Inizio parsing");
			Parser parser = new Parser(confPath, writer);
			parser.parse();
			
		}

		System.out.println("Premi y per elaborare le statistiche, altrimenti qualsiasi carattere");
		c = scanner.next();

		if (c.equals("y") ) {
			Statistiche stats = new Statistiche(confPath);
			stats.calculateStats();
		} 
		
		scanner.close();

		/**
		 * chiusura del buffer
		 */
		writer.commit();
		writer.close();

		Merger merger = new Merger(directory);
		merger.merge(ls);

		directory.close();

		long timeB = System.currentTimeMillis();
		System.out.println("\nFine elaborazione. Tempo trascorso: " + (timeB - timeA) + "ms");
	}
}
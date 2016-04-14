package com.secoo;

import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searcher {

	public void search(String indexDir,String q) throws Exception{
		Directory dir = FSDirectory.open(Paths.get(indexDir));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher search = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer();
		QueryParser parser = new QueryParser("contents", analyzer);
		Query query = parser.parse(q);
		long start = System.currentTimeMillis();
		TopDocs hits = search.search(query, 10);
		long end = System.currentTimeMillis();
		System.out.println("查询到:" + hits.totalHits + "个文档;花费时间为:" + (end - start));
		
		for(ScoreDoc scoreDoc : hits.scoreDocs){
			Document doc = search.doc(scoreDoc.doc);
			System.out.println(doc.get("fullPath"));
		}
	}
	
	public static void main(String[] args) {
		String indexDir = "D:\\data\\Lucene\\index";
		String q = "Please";
		Searcher search = new Searcher();
		try {
			search.search(indexDir, q);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

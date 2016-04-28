package com.secoo;

import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

public class SearchTest {
	
	private String ids[]={"1","2","3","4"};
	private String authors[]={"Jack","Marry","John","qiuwanchi"};
	private String positions[]={"accounting","technician","salesperson","boss"};
	private String titles[]={"Java is a good language.","Java is a cross platform language","Java powerful","You should learn java"};
	private String contents[]={
			"If possible, use the same JRE major version at both index and search time.",
			"When upgrading to a different JRE major version, consider re-indexing. ",
			"Different JRE major versions may implement different versions of Unicode,",
			"For example: with Java 1.4, `LetterTokenizer` will split around the character U+02C6,"
	};
	
	@Test
	public void index() throws Exception{
		Directory dir = FSDirectory.open(Paths.get("D:\\data\\lucene3"));
		
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter writer = new IndexWriter(dir, config);
		
		for(int i =0 ; i < ids.length; i++){
			Document doc = new Document();
			doc.add(new TextField("id", ids[i], Store.YES));
			doc.add(new TextField("author", authors[i], Store.YES));
			doc.add(new TextField("position", positions[i], Store.YES));
			
			Field titleField = new TextField("title", titles[i], Store.YES);
			if("boss".equals(positions[i])){
				titleField.setBoost(1.5f);
			}
			doc.add(titleField);
			doc.add(new TextField("content", contents[i], Store.YES));
			
			writer.addDocument(doc);
		}
		
		writer.close();
	}
	
	@Test
	public void search() throws Exception{
		Directory dir = FSDirectory.open(Paths.get("D:\\data\\lucene3"));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher search = new IndexSearcher(reader);
		
		String searchFiled = "title";
		String searchWord = "java";
		Term t = new Term(searchFiled, searchWord);
		
		Query query = new TermQuery(t);
		
		TopDocs hits = search.search(query, 10);
		
		System.out.println("匹配[" + searchWord + "],总共查询到" + hits.totalHits + "个文档.");
		for(ScoreDoc doc : hits.scoreDocs){
			Document document = search.doc(doc.doc);
			System.out.println(document.get("author"));
		}
		Query q = new TermQuery(new Term("fieldName", "queryWord"));
		search.search(q,10);
		reader.close();
	}
	
}

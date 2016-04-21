package com.secoo;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * 索引文件
 * @author qiuwanchi<br/>
 * @date: 2016年4月18日 <br/>
 */
public class Indexer {

	private IndexWriter writer;// 写索引对象

	/**
	 * 初始化写索引对象
	 * @param indexDir 索引写到indexDir目录中
	 * @throws Exception
	 */
	public Indexer(String indexDir) throws Exception {
		Directory dir = FSDirectory.open(Paths.get(indexDir));

		Analyzer analyzer = new StandardAnalyzer();// 标准分词器
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		writer = new IndexWriter(dir, config);
	}

	/**
	 * 关闭资源 <br/>
	 * @author qiuwanchi<br/>
	 * @date: 2016年4月12日 <br/>
	 */
	public void close() throws Exception{
		writer.close();
	}
	
	public int index(String dataDir) throws Exception{
		File[] files = new File(dataDir).listFiles();

		for (File file : files) {
			indexFile(file);
		}
		return writer.numDocs();
	}

	/**
	 * 索引指定文件
	 * @param file<br/>
	 * @author qiuwanchi<br/>
	 * @date: 2016年4月12日 <br/>
	 */
	private void indexFile(File file) throws Exception {
		System.out.println("正在索引的源文件:" + file.getCanonicalPath());
		Document doc = getDocument(file);
		writer.addDocument(doc);
	}

	/**
	 * 获取文档
	 * @param file
	 * @return
	 * @throws Exception<br/>
	 * @author qiuwanchi<br/>
	 * @date: 2016年4月18日 <br/>
	 */
	private Document getDocument(File file) throws Exception{
		Document doc = new Document();
		
		doc.add(new TextField("contents", new FileReader(file)));
		doc.add(new TextField("fileName", file.getName(), Store.YES));
		doc.add(new TextField("fullPath", file.getCanonicalPath(), Store.YES));
		return doc;
	}
	
	private static void addDoc(IndexWriter w, String title, String isbn) throws Exception {
	  Document doc = new Document();
	  doc.add(new TextField("title", title, Store.YES));
	  doc.add(new StringField("isbn", isbn, Store.YES));
	  w.addDocument(doc);
	}
	
	public static void main(String[] args) {
		String indexDir = "D:\\data\\Lucene\\index";
		String dataDir = "D:\\data\\Lucene\\source";
		Indexer indexer = null;
		int num = 0;
		long start = System.currentTimeMillis();
		try {
			indexer = new Indexer(indexDir);
			num = indexer.index(dataDir);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(indexer != null){
				try {
					indexer.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("索引:" + num + "花费时间为:" + (end - start));
		
	}
}

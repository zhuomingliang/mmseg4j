package com.chenlb.mmseg4j.solr;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.lucene.analysis.TokenStream;
import org.apache.solr.analysis.BaseTokenizerFactory;
import org.apache.solr.common.ResourceLoader;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.util.plugin.ResourceLoaderAware;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.SimpleSeg;
import com.chenlb.mmseg4j.analysis.MMSegTokenizer;

public class MMSegTokenizerFactory extends BaseTokenizerFactory implements ResourceLoaderAware {

	static final Logger log = Logger.getLogger(MMSegTokenizerFactory.class.getName());
	/* 线程内共享 */
	private ThreadLocal<MMSegTokenizer> tokenizerLocal = new ThreadLocal<MMSegTokenizer>();
	private Dictionary dic = null;
	
	private Seg newSeg(Map<String, String> args) {
		Seg seg = null;
		log.info("create new Seg ...");
		//default complex
		String mode = args.get("mode");
		if("simple".equals(mode)) {
			log.info("use simple mode");
			seg = new SimpleSeg(dic);
		} else {
			log.info("use complex mode");
			seg = new ComplexSeg(dic);
		}
		return seg;
	}
	
	public TokenStream create(Reader input) {
		MMSegTokenizer tokenizer = tokenizerLocal.get();
		if(tokenizer == null) {
			tokenizer = newTokenizer(input);
		} else {
			try {
				tokenizer.reset(input);
			} catch (IOException e) {
				tokenizer = newTokenizer(input);
				log.info("MMSegTokenizer.reset i/o error by:"+e.getMessage());
			}
		}

		return tokenizer;
	}

	private MMSegTokenizer newTokenizer(Reader input) {
		MMSegTokenizer tokenizer = new MMSegTokenizer(newSeg(getArgs()), input);
		tokenizerLocal.set(tokenizer);
		return tokenizer;
	}
	
	public void inform(ResourceLoader loader) {
		String dicPath = args.get("dicPath");
		
		if(dicPath != null) {
			File f = new File(dicPath);
			if(!f.isAbsolute() && loader instanceof SolrResourceLoader) {	//相对目录
				SolrResourceLoader srl = (SolrResourceLoader) loader;
				dicPath = srl.getInstanceDir()+dicPath;
				f = new File(dicPath);
			}
			log.info("dic load... in="+dicPath);
			dic = new Dictionary(f);
		} else {
			dic = new Dictionary();
		}
		
	}

	
}

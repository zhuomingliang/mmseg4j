package com.chenlb.mmseg4j.solr;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.solr.analysis.BaseTokenizerFactory;
import org.apache.solr.common.ResourceLoader;
import org.apache.solr.util.plugin.ResourceLoaderAware;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MaxWordSeg;
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
		//default max-word
		String mode = args.get("mode");
		if("simple".equals(mode)) {
			log.info("use simple mode");
			seg = new SimpleSeg(dic);
		} else if("complex".equals(mode)) {
			log.info("use complex mode");
			seg = new ComplexSeg(dic);
		} else {
			log.info("use max-word mode");
			seg = new MaxWordSeg(dic);
		}
		return seg;
	}
	
	public Tokenizer create(Reader input) {
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
		
		dic = Utils.getDict(dicPath, loader);
		
		log.info("dic load... in="+dic.getDicPath().toURI());
	}

	
}

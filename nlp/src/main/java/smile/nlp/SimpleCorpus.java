/*
 * Copyright (c) 2010-2020 Haifeng Li. All rights reserved.
 *
 * Smile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Smile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Smile.  If not, see <https://www.gnu.org/licenses/>.
 */

package smile.nlp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import smile.nlp.dictionary.EnglishPunctuations;
import smile.nlp.dictionary.EnglishStopWords;
import smile.nlp.dictionary.Punctuations;
import smile.nlp.dictionary.StopWords;
import smile.nlp.relevance.Relevance;
import smile.nlp.relevance.RelevanceRanker;
import smile.nlp.tokenizer.SentenceSplitter;
import smile.nlp.tokenizer.SimpleSentenceSplitter;
import smile.nlp.tokenizer.SimpleTokenizer;
import smile.nlp.tokenizer.Tokenizer;
import smile.util.MutableInt;

/**
 * An in-memory text corpus. Useful for text feature engineering.
 *
 * @author Haifeng Li
 */
public class SimpleCorpus implements Corpus {

    /**
     * The number of terms in the corpus.
     */
    private long size;
    /**
     * The set of documents.
     */
    private final List<SimpleText> docs = new ArrayList<>();
    /**
     * Frequency of single tokens.
     */
    private final HashMap<String, MutableInt> freq = new HashMap<>();
    /**
     * Frequency of bigrams.
     */
    private final HashMap<Bigram, MutableInt> freq2 = new HashMap<>();
    /**
     * Inverted file storing a mapping from terms to the documents containing it.
     */
    private final HashMap<String, List<SimpleText>> invertedFile = new HashMap<>();
    /**
     * Sentence splitter.
     */
    private final SentenceSplitter splitter;
    /**
     * Tokenizer.
     */
    private final Tokenizer tokenizer;
    /**
     * The set of stop words.
     */
    private final StopWords stopWords;

    /**
     * The set of punctuations marks.
     */
    private final Punctuations punctuations;
    
    /**
     * Constructor.
     */
    public SimpleCorpus() {
        this(SimpleSentenceSplitter.getInstance(), new SimpleTokenizer(), EnglishStopWords.DEFAULT, EnglishPunctuations.getInstance());
    }

    /**
     * Constructor.
     *
     * @param splitter the sentence splitter.
     * @param tokenizer the word tokenizer.
     * @param stopWords the set of stop words to exclude.
     * @param punctuations the set of punctuation marks to exclude. Set to null to keep all punctuation marks.
     */
    public SimpleCorpus(SentenceSplitter splitter, Tokenizer tokenizer, StopWords stopWords, Punctuations punctuations) {
        this.splitter = splitter;
        this.tokenizer = tokenizer;
        this.stopWords = stopWords;
        this.punctuations = punctuations;
    }

    /**
     * Adds a document to the corpus.
     * @param text the document text.
     * @return the document.
     */
    public Text add(Text text) {
        ArrayList<String> bag = new ArrayList<>();
        
        for (String sentence : splitter.split(text.body)) {
            String[] tokens = tokenizer.split(sentence);
            for (int i = 0; i < tokens.length; i++) {
                tokens[i] = tokens[i].toLowerCase();
            }

            for (String w : tokens) {
                boolean keep = true;
                if (punctuations != null && punctuations.contains(w)) {
                    keep = false;
                } else if (stopWords != null && stopWords.contains(w)) {
                    keep = false;
                }

                if (keep) {
                    size++;
                    bag.add(w);
                    
                    MutableInt count = freq.get(w);
                    if (count == null) {
                        freq.put(w, new MutableInt(1));
                    } else {
                        count.increment();
                    }
                }
            }

            for (int i = 0; i < tokens.length - 1; i++) {
                String w1 = tokens[i];
                String w2 = tokens[i + 1];
                
                if (freq.containsKey(w1) && freq.containsKey(w2)) {
                    Bigram bigram = new Bigram(w1, w2);
                    MutableInt count = freq2.get(bigram);
                    if (count == null) {
                        freq2.put(bigram, new MutableInt(1));
                    } else {
                        count.increment();
                    }
                }
            }
        }

        String[] words = new String[bag.size()];
        for (int i = 0; i < words.length; i++) {
            words[i] = bag.get(i);
        }

        SimpleText doc = new SimpleText(text.id, text.title, text.body, words);
        docs.add(doc);

        for (String term : doc.unique()) {
            List<SimpleText> hit = invertedFile.computeIfAbsent(term, k -> new ArrayList<>());
            hit.add(doc);
        }

        return doc;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public int ndoc() {
        return docs.size();
    }

    @Override
    public int nterm() {
        return freq.size();
    }

    @Override
    public long nbigram() {
        return freq2.size();
    }

    @Override
    public int avgDocSize() {
        return (int) (size / docs.size());
    }

    @Override
    public int count(String term) {
        MutableInt count = freq.get(term);
        return count == null ? 0 : count.value;
    }

    @Override
    public int count(Bigram bigram) {
        MutableInt count = freq2.get(bigram);
        return count == null ? 0 : count.value;
    }

    @Override
    public Iterator<String> terms() {
        return freq.keySet().iterator();
    }

    @Override
    public Iterator<Bigram> bigrams() {
        return freq2.keySet().iterator();
    }

    @Override
    public Iterator<Text> search(String term) {
        if (invertedFile.containsKey(term)) {
            ArrayList<Text> hits = new ArrayList<>(invertedFile.get(term));
            return hits.iterator();
        } else {
            return Collections.emptyIterator();
        }
    }

    @Override
    public Iterator<Relevance> search(RelevanceRanker ranker, String term) {
        if (invertedFile.containsKey(term)) {
            List<SimpleText> hits = invertedFile.get(term);

            int n = hits.size();

            ArrayList<Relevance> rank = new ArrayList<>(n);
            for (SimpleText doc : hits) {
                int tf = doc.tf(term);
                rank.add(new Relevance(doc, ranker.rank(this, doc, term, tf, n)));
            }

            rank.sort(Collections.reverseOrder());
            return rank.iterator();
        } else {
            return Collections.emptyIterator();
        }
    }

    @Override
    public Iterator<Relevance> search(RelevanceRanker ranker, String[] terms) {
        Set<SimpleText> hits = new HashSet<>();

        for (String term : terms) {
            if (invertedFile.containsKey(term)) {
                hits.addAll(invertedFile.get(term));
            }
        }

        int n = hits.size();
        if (n == 0) {
            return Collections.emptyIterator();
        }
        
        ArrayList<Relevance> rank = new ArrayList<>(n);
        for (SimpleText doc : hits) {
            double r = 0.0;
            for (String term : terms) {
                int tf = doc.tf(term);
                r += ranker.rank(this, doc, term, tf, n);
            }

            rank.add(new Relevance(doc, r));
        }

        rank.sort(Collections.reverseOrder());
        return rank.iterator();
    }
}

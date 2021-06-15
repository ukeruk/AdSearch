import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class Keyword implements Comparable<Keyword>, Serializable {

    private final String stem;
    private final Set<String> terms = new HashSet<String>();
    private int frequency = 0;
    private File file;
    private static HashSet<String> dict;

    public Keyword(String stem) throws IOException {
        this.stem = stem;
    }

    public Keyword() throws IOException {
        this.stem = null;
        dict = new HashSet<>();
        file = new File(Path.of("").toAbsolutePath().toString() + "\\src\\words.txt");

        readFile(file, dict);
    }

    public void add(String term) {
        terms.add(term);
        frequency++;
    }

    @Override
    public int compareTo(Keyword o) {
        // descending order
        return Integer.valueOf(o.frequency).compareTo(frequency);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Keyword)) {
            return false;
        } else {
            return stem.equals(((Keyword) obj).stem);
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{stem});
    }

    public String getStem() {
        return stem;
    }

    public Set<String> getTerms() {
        return terms;
    }

    public int getFrequency() {
        return frequency;
    }

    public static <T> T find(Collection<T> collection, T example) {
        for (T element : collection) {
            if (element.equals(example)) {
                return element;
            }
        }
        collection.add(example);
        return example;
    }

    public static String stem(String term) throws IOException {
        String temp = term;
        if (!dict.contains(temp))
            temp = null;

        TokenStream tokenStream = null;
        try {
            Analyzer analyzer = new StandardAnalyzer();
            // tokenize
            tokenStream = analyzer.tokenStream("Ads", new StringReader(term));
            // stem
            tokenStream = new PorterStemFilter(tokenStream);

            // add each token in a set, so that duplicates are removed
            Set<String> stems = new HashSet<String>();
            CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                stems.add(token.toString());
            }

            // if no stem or 2+ stems have been found, return null
            if (stems.size() != 1) {
                if (temp != null)
                    return temp;
                return null;
            }
            String stem = stems.iterator().next();
            // if the stem has non-alphanumerical chars, return null
            if (!stem.matches("[a-zA-Z0-9-]+")) {
                if (temp != null)
                    return temp;
                return null;
            }

            if (!dict.contains(stem)) {
                if (temp == null)
                {
                    return null;
                }
                else if (temp != null)
                {
                    return temp;
                }
            }
            return stem;

        } finally {
            if (tokenStream != null) {
                tokenStream.close();
            }
        }

    }

    private void readFile(File file, HashSet<String> myDict) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                myDict.add(line);
            }

        } finally {
            reader.close();
        }
    }

    public static List<Keyword> guessFromString(String input) throws IOException {


        TokenStream tokenStream = null;
        try {
            input = input.replaceAll("'s", "");
            input = input.replaceAll("'", "");
            input = input.replaceAll("-", "");
            // replace any punctuation char but apostrophes and dashes by a space
            input = input.replaceAll("[\\p{Punct}&&[^'-]]+", " ");
            // replace most common english contractions
            input = input.replaceAll("(?:'(?:[tdsm]|[vr]e|ll))+\\b", "");

            Analyzer analyzer = new StandardAnalyzer();
            // tokenize
            tokenStream = analyzer.tokenStream("Ads", new StringReader(input));
            // to lowercase
            tokenStream = new LowerCaseFilter(tokenStream);
            // remove dots from acronyms (and "'s" but already done manually above)
            tokenStream = new ClassicFilter(tokenStream);
            // convert any char to ASCII
            tokenStream = new ASCIIFoldingFilter(tokenStream);
            // remove english stop words
            tokenStream = new StopFilter(tokenStream, EnglishAnalyzer.getDefaultStopSet());

            List<Keyword> keywords = new LinkedList<Keyword>();
            CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);

            tokenStream.reset();

            while (tokenStream.incrementToken()) {
                String term = token.toString();
                // stem each term
                String stem = stem(term);
                if (stem != null) {
                    // create the keyword or get the existing one if any
                    Keyword keyword = find(keywords, new Keyword(stem));
                    // add its corresponding initial token
                    keyword.add(term);
                }
            }

            // reverse sort by frequency
            Collections.sort(keywords);

            return keywords;

        } finally {
            if (tokenStream != null) {
                tokenStream.close();
            }
        }

    }

}
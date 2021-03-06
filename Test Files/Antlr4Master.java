import javax.swing.*;
import javax.transaction.Transaction;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Antlr4Master {

    private static final String LEXER_START_RULE_NAME = "";
    private Logger log;
    private String sourceDirectory;
    private Grammar grammars;
    private String grammarName;
    private String startRuleName;
    private String encoding;
    private List<String> inputFiles;
    private int n;
    private T[][] buckets;
    private Comparador comparator;

    public class GrammarDependencies {
        // org.antlr.mojo.antlr4.GrammarDependencies.analyze(java.util.Set<java.io.File>,java.util.Set<java.io.File>,org.antlr.v4.Tool)

        /**
         * Performs dependency analysis for the given grammar files.
         *
         * @param grammarFiles       the grammar files.
         * @param importGrammarFiles the import grammar files.
         * @param tool               the tool to use.
         * @return self-reference.
         */
        public GrammarDependencies analyze(Set<File> grammarFiles,
                                           Set<File> importGrammarFiles, Tool tool) throws IOException {
            log.debug("Analysing grammar dependencies " + sourceDirectory);

            // for dependency analysis we require all grammars
            Collection<File> grammarsAndTokens = new HashSet<File>();
            grammarsAndTokens.addAll(importGrammarFiles);
            grammarsAndTokens.addAll(grammarFiles);

            for (File grammarFile : grammarsAndTokens) {
                // .tokens files must not be parsed, they can just be referenced
                if (!grammarFile.getName().endsWith(".tokens"))
                    analyse(grammarFile, grammarsAndTokens, tool);
            }

            for (File grammarFile : grammarFiles) {
                Collection<String> usages = findUsages(getRelativePath(grammarFile));

                if (!usages.isEmpty()) {
                    grammars.put(grammarFile,
                            new AbstractMap.SimpleImmutableEntry<byte[], Collection<String>>(
                                    MojoUtils.checksum(grammarFile), usages));

                    log.debug("  " + getRelativePath(grammarFile) + " used by " + usages);
                }
            }

            for (File grammarFile : importGrammarFiles) {
                // imported files are not allowed to be qualified
                Collection<String> usages = findUsages(grammarFile.getName());

                if (!usages.isEmpty()) {
                    grammars.put(grammarFile,
                            new AbstractMap.SimpleImmutableEntry<byte[], Collection<String>>(
                                    MojoUtils.checksum(grammarFile), usages));

                    log.debug("  " + grammarFile.getName() + " imported by " + usages);
                }
            }

            return this;
        }
    }

    // org.antlr.v4.gui.TestRig.process()
    public void process() throws Exception {
//		System.out.println("exec "+grammarName+"."+startRuleName);
        String lexerName = grammarName+"Lexer";
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Class<? extends Lexer> lexerClass = null;
        try {
            lexerClass = cl.loadClass(lexerName).asSubclass(Lexer.class);
        }
        catch (java.lang.ClassNotFoundException cnfe) {
            // might be pure lexer grammar; no Lexer suffix then
            lexerName = grammarName;
            try {
                lexerClass = cl.loadClass(lexerName).asSubclass(Lexer.class);
            }
            catch (ClassNotFoundException cnfe2) {
                System.err.println("Can't load "+lexerName+" as lexer or parser");
                return;
            }
        }

        Constructor<? extends Lexer> lexerCtor = lexerClass.getConstructor(CharStream.class);
        Lexer lexer = lexerCtor.newInstance((CharStream)null);

        Class<? extends Parser> parserClass = null;
        Parser parser = null;
        if ( !startRuleName.equals(LEXER_START_RULE_NAME) ) {
            String parserName = grammarName+"Parser";
            parserClass = cl.loadClass(parserName).asSubclass(Parser.class);
            Constructor<? extends Parser> parserCtor = parserClass.getConstructor(TokenStream.class);
            parser = parserCtor.newInstance((TokenStream)null);
        }

        Charset charset = ( encoding == null ? Charset.defaultCharset () : Charset.forName(encoding) );
        if ( inputFiles.size()==0 ) {
            CharStream charStream;
            if ( charset.equals(StandardCharsets.UTF_8)) {
                charStream = CharStreams.createWithUTF8Stream(System.in);
            } else {
                try ( InputStreamReader r = new InputStreamReader(System.in, charset) ) {
                    charStream = new ANTLRInputStream(r);
                }
            }
            process(lexer, parserClass, parser, charStream);
            return;
        }
        for (String inputFile : inputFiles) {
            CharStream charStream;
            if ( charset.equals(StandardCharsets.UTF_8) ) {
                charStream = CharStreams.createWithUTF8(Paths.get(inputFile));
            } else {
                try ( InputStreamReader r = new InputStreamReader(System.in, charset) ) {
                    charStream = new ANTLRInputStream(r);
                }
            }
            if ( inputFiles.size()>1 ) {
                System.err.println(inputFile);
            }
            process(lexer, parserClass, parser, charStream);
        }
    }

    // org.antlr.v4.runtime.atn.PredictionMode.hasSLLConflictTerminatingPrediction(org.antlr.v4.runtime.atn.PredictionMode,org.antlr.v4.runtime.atn.ATNConfigSet)
    public static boolean hasSLLConflictTerminatingPrediction(PredictionMode mode, ATNConfigSet configs) {
        /* Configs in rule stop states indicate reaching the end of the decision
         * rule (local context) or end of start rule (full context). If all
         * configs meet this condition, then none of the configurations is able
         * to match additional input so we terminate prediction.
         */
        if (allConfigsInRuleStopStates(configs)) {
            return true;
        }

        // pure SLL mode parsing
        if ( mode == PredictionMode.SLL ) {
            // Don't bother with combining configs from different semantic
            // contexts if we can fail over to full LL; costs more time
            // since we'll often fail over anyway.
            if ( configs.hasSemanticContext ) {
                // dup configs, tossing out semantic predicates
                ATNConfigSet dup = new ATNConfigSet();
                for (ATNConfig c : configs) {
                    c = new ATNConfig(c,SemanticContext.NONE);
                    dup.add(c);
                }
                configs = dup;
            }
            // now we have combined contexts for configs with dissimilar preds
        }

        // pure SLL or combined SLL+LL mode parsing

        Collection<BitSet> altsets = getConflictingAltSubsets(configs);
        boolean heuristic =
                hasConflictingAltSet(altsets) && !hasStateAssociatedWithOneAlt(configs);
        return heuristic;
    }

    // org.antlr.v4.runtime.misc.Array2DHashSet.removeFast(T)
    public boolean removeFast(T obj) {
        if (obj == null) {
            return false;
        }

        int b = getBucket(obj);
        T[] bucket = buckets[b];
        if ( bucket==null ) {
            // no bucket
            return false;
        }

        for (int i=0; i<bucket.length; i++) {
            T e = bucket[i];
            if ( e==null ) {
                // empty slot; not there
                return false;
            }

            if ( comparator.equals(e, obj) ) {          // found it
                // shift all elements to the right down one
                System.arraycopy(bucket, i+1, bucket, i, bucket.length-i-1);
                bucket[bucket.length - 1] = null;
                n--;
                return true;
            }
        }

        return false;
    }
    // org.antlr.v4.test.runtime.java.api.TestTokenStreamRewriter.testToStringStartStop2()
//    @Test // Removed to allow compilation
    public void testToStringStartStop2() throws Exception {
        LexerGrammar g = new LexerGrammar(
                "lexer grammar T;\n"+
                        "ID : 'a'..'z'+;\n" +
                        "INT : '0'..'9'+;\n" +
                        "SEMI : ';';\n" +
                        "ASSIGN : '=';\n" +
                        "PLUS : '+';\n" +
                        "MULT : '*';\n" +
                        "WS : ' '+;\n");
        // Tokens: 012345678901234567
        // Input:  x = 3 * 0 + 2 * 0;
        String input = "x = 3 * 0 + 2 * 0;";
        LexerInterpreter lexEngine = g.createLexerInterpreter(new ANTLRInputStream(input));
        CommonTokenStream stream = new CommonTokenStream(lexEngine);
        stream.fill();
        TokenStreamRewriter tokens = new TokenStreamRewriter(stream);

        String result = tokens.getTokenStream().getText();
        String expecting = "x = 3 * 0 + 2 * 0;";
        assertEquals(expecting, result);

        tokens.replace(4, 8, "0");
        stream.fill();
// replace 3 * 0 with 0
        result = tokens.getText();
        expecting = "x = 0 + 2 * 0;";
        assertEquals(expecting, result);

        result = tokens.getText(Interval.of(0, 17));
        expecting = "x = 0 + 2 * 0;";
        assertEquals(expecting, result);

        result = tokens.getText(Interval.of(4, 8));
        expecting = "0";
        assertEquals(expecting, result);

        result = tokens.getText(Interval.of(0, 8));
        expecting = "x = 0";
        assertEquals(expecting, result);

        result = tokens.getText(Interval.of(12, 16));
        expecting = "2 * 0";
        assertEquals(expecting, result);

        tokens.insertAfter(17, "// comment");
        result = tokens.getText(Interval.of(12, 18));
        expecting = "2 * 0;// comment";
        assertEquals(expecting, result);

        result = tokens.getText(Interval.of(0, 8));
        stream.fill();
// try again after insert at end
        expecting = "x = 0";
        assertEquals(expecting, result);
    }

    private void assertEquals(String expecting, String result) {

    }

    public void process(Lexer l, Class c, Parser p, CharStream cs) {

    }

    private class Logger {

        public void debug(String s) {

        }
    }

    private class Tool {
    }

    private static class MojoUtils {
        public static byte[] checksum(File grammarFile) {
            return new byte[0];
        }
    }

    private class Grammar {
        public void put(File grammarFile, AbstractMap.SimpleImmutableEntry<byte[], Collection<String>> collectionSimpleImmutableEntry) {

        }
    }

    private Collection<String> findUsages(String relativePath) {
        return null;
    }

    private String getRelativePath(File grammarFile) {
        return null;
    }

    private void analyse(File grammarFile, Collection<File> grammarsAndTokens, Tool tool) {

    }

    private class Lexer {
    }

    private class CharStream {
    }

    private class Parser {
    }

    private class TokenStream {
    }

    private static class CharStreams {
        public static CharStream createWithUTF8Stream(InputStream in) {
            return null;
        }

        public static CharStream createWithUTF8(Path path) {
            return null;
        }
    }

    private class ANTLRInputStream extends CharStream {
        public ANTLRInputStream(InputStreamReader r) {
            super();
        }

        public ANTLRInputStream(String input) {

        }
    }

    public static class ATNConfigSet implements Set<ATNConfig> {
        public boolean hasSemanticContext;

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<ATNConfig> iterator() {
            return null;
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return null;
        }

        @Override
        public boolean add(ATNConfig atnConfig) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends ATNConfig> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }

        public void add(String s) {

        }

        public Transaction add(Object newInstanceWithBundle, MyExpenses.Dummy asyncTag) {
            return null;
        }
    }

    private static class PredictionMode {
        public static final PredictionMode SLL = null;
    }

    private static class ATNConfig {
        public ATNConfig(ATNConfig c, Object none) {

        }
    }

    private static class SemanticContext {
        public static final Object NONE = null;
    }

    private static boolean allConfigsInRuleStopStates(ATNConfigSet configs) {
        return false;
    }

    private static Collection<BitSet> getConflictingAltSubsets(ATNConfigSet configs) {
        return null;
    }

    private static boolean hasConflictingAltSet(Collection<BitSet> altsets) {
        return false;
    }

    private static boolean hasStateAssociatedWithOneAlt(ATNConfigSet configs) {
        return false;
    }

    private class T {
    }

    private class Comparador {
        public boolean equals(T e, T obj) {
            return false;
        }
    }

    private int getBucket(T obj) {
        return 0;
    }

    private class LexerGrammar {
        public LexerGrammar(String s) {

        }

        public LexerInterpreter createLexerInterpreter(ANTLRInputStream antlrInputStream) {
            return null;
        }
    }

    private class LexerInterpreter {
    }

    private class CommonTokenStream {
        public CommonTokenStream(LexerInterpreter lexEngine) {

        }

        public void fill() {

        }
    }

    private class TokenStreamRewriter {
        public TokenStreamRewriter(CommonTokenStream stream) {

        }

        public JLabel getTokenStream() {
            return null;
        }

        public void replace(int i, int i1, String s) {

        }

        public String getText() {
            return null;
        }

        public String getText(String of) {
            return null;
        }

        public void insertAfter(int i, String s) {

        }
    }

    private static class Interval {
        public static String of(int i, int i1) {
            return null;
        }
    }
}

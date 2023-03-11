import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private static final int QUOTE_CHARACTER = '\'';
    private static final int DOUBLE_QUOTE_CHARACTER = '"';
    private static final char EQUALS_CHARACTER = '=';
    private static final char MOD_CHARACTER = '%';
    private static final char COLON_CHARACTER = ':';
    private static final char LSQUARE_CHARACTER = '[';
    private static final char RSQUARE_CHARACTER = ']';
    private static final char COMMA_CHARACTER = ',';

    public static List<LexToken> streamTokenizerWithDefaultConfiguration(Reader reader) throws IOException {
        StreamTokenizer streamTokenizer = new StreamTokenizer(reader);
        List<Object> tokens = new ArrayList<Object>();
        List<LexToken> lexTokens = new ArrayList<>();
        int currentToken = streamTokenizer.nextToken();
        while (currentToken != StreamTokenizer.TT_EOF) {
            LexToken lexToken = new LexToken();
            if (streamTokenizer.ttype == StreamTokenizer.TT_NUMBER) {
                lexToken.type = NodeType.NUMBER;
                lexToken.value = String.valueOf(streamTokenizer.nval);
                tokens.add(streamTokenizer.nval);
            } else if (streamTokenizer.ttype == StreamTokenizer.TT_WORD
                    || streamTokenizer.ttype == QUOTE_CHARACTER
                    || streamTokenizer.ttype == DOUBLE_QUOTE_CHARACTER){
                lexToken.type = NodeType.ATOM;
                lexToken.value = streamTokenizer.sval;
                tokens.add(streamTokenizer.sval);
            } else {
                if((char)currentToken ==  LSQUARE_CHARACTER)
                    lexToken.type = NodeType.LSQUARE;
                else if ((char)currentToken == RSQUARE_CHARACTER)
                    lexToken.type = NodeType.RSQUARE;
                else if ((char)currentToken == COLON_CHARACTER)
                    lexToken.type = NodeType.COLON;
                else if ((char)currentToken == COMMA_CHARACTER)
                    lexToken.type = NodeType.COMMA;
                else if ((char)currentToken == MOD_CHARACTER)
                    lexToken.type = NodeType.MOD;
                else if ((char)currentToken == EQUALS_CHARACTER)
                    lexToken.type = NodeType.EQUALS;
                lexToken.value = String.valueOf(currentToken);
                lexToken.operator = (char)currentToken;
                tokens.add((char) currentToken);
            }
            lexTokens.add(lexToken);
            currentToken = streamTokenizer.nextToken();
        }

        return lexTokens;
    }

    public static List<String> formatLines(List<String> lines){
        List<String> fmt_lines =  new ArrayList<>();
        for(String s : lines){
            s = s.trim();
            if(s.equals("") || s.charAt(0) =='#')
                continue;
            String fmt_s = s.replace("\n","").trim();
            fmt_lines.add(fmt_s);
        }
        System.out.println();
        return fmt_lines;
    }

    public static List<List<LexToken>> createTokens(List<String> fmt_lines){
        List<List<LexToken>> tokenList = new ArrayList<>();
        try {
            for(String s : fmt_lines) {
                Reader target = new StringReader(s);
                List<LexToken> ret = Parser.streamTokenizerWithDefaultConfiguration(target);
                tokenList.add(ret);
                target.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return tokenList;
    }
}

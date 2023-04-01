public class ParseResult {
    private String source;
    private String html;
    public ParseResult(String source, String html){
        this.source = source;
        this.html = html;
    }
    public String getSource() {
        return source;
    }

    public String getHtml() {
        return html;
    }
}
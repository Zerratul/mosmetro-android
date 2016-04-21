package pw.thedrhax.mosmetro.httpclient;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;

public abstract class Client {
    protected Document document;

    protected Client() {}

    // Settings methods
    public abstract Client followRedirects(boolean follow);

    // IO methods
    public abstract Client get(String link, Map<String,String> params) throws Exception;
    public abstract Client post(String link, Map<String,String> params) throws Exception;

    // Parse methods
    public Document getPageContent() throws Exception {
        return document;
    }

    public String parseLinkRedirect() throws Exception {
        String link = document.getElementsByTag("a").first().attr("href");

        if (link == null || link.isEmpty())
            throw new Exception ("Link not found");

        return link;
    }

    public String parseMetaRedirect() throws Exception {
        String link = null;

        for (Element element : document.getElementsByTag("meta")) {
            if (element.attr("http-equiv").equalsIgnoreCase("refresh")) {
                String attr = element.attr("content");
                link = attr.substring(attr.indexOf("=") + 1);
            }
        }

        if (link == null || link.isEmpty())
            throw new Exception ("Meta redirect not found");

        // Check protocol of the URL
        if (!(link.contains("http://") || link.contains("https://")))
            link = "http://" + link;

        return link;
    }

    public static Map<String,String> parseForm (Element form) throws Exception {
        Map<String,String> result = new HashMap<String,String>();

        for (Element input : form.getElementsByTag("input")) {
            result.put(input.attr("name"), input.attr("value"));
        }

        return result;
    }

    // Convert methods
    protected static String requestToString (Map<String,String> params) {
        if (params == null) return "";

        StringBuilder params_string = new StringBuilder();

        for (Map.Entry<String,String> entry : params.entrySet()) {
            if (params_string.length() == 0) {
                params_string.append("?");
            } else {
                params_string.append("&");
            }

            params_string
                    .append(entry.getKey())
                    .append("=")
                    .append(entry.getValue());
        }

        return params_string.toString();
    }
}
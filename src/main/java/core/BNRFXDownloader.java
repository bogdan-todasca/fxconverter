package core;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public abstract class BNRFXDownloader extends FXDownloader {

    public void parsePage() {
        final ExecutorService s = Executors.newSingleThreadExecutor();
        s.submit(this::download);
    }

    private void download() {
        try {
            showProgress("Connecting");
            final Document doc = Jsoup.connect(getURL()).get();
            showProgress("Parsing");
            final Elements date = doc.select("body > div:nth-child(5) > div:nth-child(1) > div > main > div.main-center.pull-right > div:nth-child(6) > div > p > strong");
            updateDate(date.get(0).text());

            final Elements rates = doc.select("#table-currencies > tbody");
            final List<Node> entries = rates.get(0).childNodes();
            final List<Currency> result = entries.stream().
                    filter(node -> node.childNodeSize() > 0).
                    map(node -> Arrays.asList(
                            node.childNode(1).childNode(0).toString(),
                            node.childNode(3).childNode(2).childNode(0).toString(),
                            node.childNode(4).childNode(0).toString()
                    )).
                    map(t -> new Currency(t.get(0), t.get(1), t.get(2))).
                    collect(Collectors.toList());

            onDone(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String getURL() {
        return "https://www.cursbnr.ro/";
    }

}

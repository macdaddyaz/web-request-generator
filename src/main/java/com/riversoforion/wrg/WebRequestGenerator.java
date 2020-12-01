package com.riversoforion.wrg;


import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;


@Command(name = "wrg", mixinStandardHelpOptions = true, description = "Generates a random web request")
public class WebRequestGenerator implements Callable<Integer> {

    @Parameters(index = "0", description = "The URL, which must accept POST requests")
    private URL url;
    @Option(names = { "-l", "--length" }, description = "The number of characters to include in the request body")
    @SuppressWarnings("FieldMayBeFinal")
    private long length = 1_000L;
    @Option(names = { "-c", "--continuous" }, description = "Continuously send data in the request body")
    private boolean continuous;
    @SuppressWarnings("FieldMayBeFinal")
    @Option(names = { "-t", "--threads" }, description = "The number of simultaneous requests to send")
    private int threads = 1;


    @Override
    public Integer call() throws Exception {

        System.out.printf("Sending %s over %d simultaneous request(s) to %s%n",
                          continuous ? "a continuous stream of data" : length + " characters",
                          threads,
                          url);
        var client = HttpClient.newBuilder().version(Version.HTTP_1_1).followRedirects(Redirect.NORMAL).build();
        var request = HttpRequest
                .newBuilder(url.toURI())
                .header("Content-Type", "text/plain")
                .header("User-Agent", "WebRequestGenerator/1.0")
                .POST(BodyPublishers.ofInputStream(() -> new RandomInputStream(continuous, length)))
                .build();
        var futures = new CompletableFuture[threads];
        for (int i = 0; i < threads; i++) {
            futures[i] = client
                    .sendAsync(request, BodyHandlers.ofString())
                    .thenAccept((response) -> System.out.printf("%d%n%s%n", response.statusCode(), response.body()));
        }
        CompletableFuture.allOf(futures).join();
        return 0;
    }


    public static void main(String[] args) {

        System.exit(new CommandLine(new WebRequestGenerator()).execute(args));
    }
}

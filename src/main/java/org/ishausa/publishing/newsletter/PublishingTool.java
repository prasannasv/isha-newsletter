package org.ishausa.publishing.newsletter;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import org.ishausa.publishing.newsletter.renderer.SoyRenderer;
import spark.Request;
import spark.Response;
import spark.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;


/**
 * Created by tosri on 12/3/2016.
 */
public class PublishingTool {
    private static final Logger log = Logger.getLogger(PublishingTool.class.getName());

    private static final NewsletterCreator NEWSLETTER_CREATOR = new NewsletterCreator();

    private static final String CONTENT_FILE_NAME = "content.txt";
    private static final String WORDPRESS_FILE_NAME = "wordpress.html";
    private static final String EMAIL_FILE_NAME = "email.html";

    public static void main(final String[] args) throws Exception {
        port(Integer.parseInt(System.getenv("PORT")));
        staticFiles.location("/static");

        get("/", (req, res) ->
                SoyRenderer.INSTANCE.render(SoyRenderer.PublishingToolTemplate.INDEX, buildIndexDataMap()));

        get(WORDPRESS_FILE_NAME, (req, res) -> IOUtils.toString(new FileInputStream(new File(WORDPRESS_FILE_NAME))));
        get(EMAIL_FILE_NAME, (req, res) -> IOUtils.toString(new FileInputStream(new File(EMAIL_FILE_NAME))));

        post("/", PublishingTool::handlePost);

        exception(Exception.class, ((exception, request, response) -> {
            log.info("Exception: " + exception + " stack: " + Throwables.getStackTraceAsString(exception));
            response.status(500);
            response.body("Exception: " + exception + " stack: " + Throwables.getStackTraceAsString(exception));
        }));
    }

    private static Map<String, ?> buildIndexDataMap() throws Exception {
        final Map<String, ?> indexDataMap;

        final File contentFile = new File(CONTENT_FILE_NAME);
        if (contentFile.exists()) {
            final String content = IOUtils.toString(new FileInputStream(contentFile));
            final Map<String, List<String>> contentDataMap = NEWSLETTER_CREATOR.parseContentToMap(content);
            indexDataMap = StandardSection.getSoyMapData(contentDataMap);
            log.info("Built index data map with saved contents as: " + indexDataMap);
        } else {
            indexDataMap = StandardSection.getSoyMapData();
        }

        return indexDataMap;
    }

    private static String handlePost(final Request request, final Response response) throws Exception {
        final String content = request.body();
        final File contentFile = new File(CONTENT_FILE_NAME);
        try (final PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(contentFile)))) {
            writer.println(content);
        }

        // Build the Newsletter from the paramsMap by building out each section
        final Newsletter newsletter = NEWSLETTER_CREATOR.parseToNewsletter(content);

        // Dump the html content of the Newsletter for wordpress
        final File wordpressFile = new File(WORDPRESS_FILE_NAME);
        try (final PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(wordpressFile)))) {
            newsletter.printForWordpress(writer);
        }
        log.info("Wordpress html written to " + wordpressFile.getAbsolutePath());

        // Dump it for email
        final File emailFile = new File(EMAIL_FILE_NAME);
        try (final PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(emailFile)))) {
            newsletter.printForEmail(writer);
        }
        log.info("Email html written to " + emailFile.getAbsolutePath());

        return SoyRenderer.INSTANCE.render(SoyRenderer.PublishingToolTemplate.OUTPUT_LINK,
                ImmutableMap.of("wordpressFile", wordpressFile.getName(),
                        "emailFile", emailFile.getName()));
    }
}

package org.ishausa.publishing.newsletter;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import org.ishausa.publishing.newsletter.renderer.SoyRenderer;
import spark.Request;
import spark.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
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

    public static void main(final String[] args) {
        port(Integer.parseInt(System.getenv("PORT")));
        staticFiles.location("/static");

        get("/", (req, res) -> SoyRenderer.INSTANCE.render(SoyRenderer.PublishingToolTemplate.INDEX, new HashMap<>()));

        post("/", PublishingTool::handlePost);

        exception(Exception.class, ((exception, request, response) -> {
            log.info("Exception: " + exception + " stack: " + Throwables.getStackTraceAsString(exception));
        }));
    }

    private static String handlePost(final Request request, final Response response) throws Exception {
        final String content = request.body();

        // Build the Newsletter from the paramsMap by building out each section
        final Newsletter newsletter = NEWSLETTER_CREATOR.parseToNewsletter(content);

        // Dump the html content of the Newsletter for wordpress
        final File wordpressFile = new File("wordpress.html");
        {
            final PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(wordpressFile)));
            newsletter.printForWordpress(writer);
            writer.close();
        }
        log.info("Wordpress html written to " + wordpressFile.getAbsolutePath());

        // Dump it for email
        final File emailFile = new File("email.html");
        {
            final PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(emailFile)));
            newsletter.printForEmail(writer);
            writer.close();
        }
        log.info("Email html written to " + emailFile.getAbsolutePath());

        // for debugging
        final StringWriter buffer = new StringWriter();
        newsletter.printForWordpress(new PrintWriter(buffer));
        log.info("Wordpress Contents: " + buffer);

        return SoyRenderer.INSTANCE.render(SoyRenderer.PublishingToolTemplate.OUTPUT_LINK,
                ImmutableMap.of("wordpressFile", wordpressFile.getAbsolutePath(),
                        "emailFile", emailFile.getAbsolutePath()));
    }
}

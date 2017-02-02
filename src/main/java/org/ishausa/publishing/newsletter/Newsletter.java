package org.ishausa.publishing.newsletter;

import com.google.common.collect.ImmutableMap;
import org.ishausa.publishing.newsletter.renderer.SoyRenderer;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a Newsletter. Once we have a model of it, we can translate it to wordpress or email format easily.
 *
 * Created by tosri on 12/4/2016.
 */
class Newsletter {
    private final String title;
    private final String date;
    private final List<Section> sections = new LinkedList<>();

    Newsletter(final String title, final String date) {
        this.title = title;
        this.date = date;
    }

    String getWordpressLink() {
        // this is the link where the published newsletter will live.
        return "http://www.ishafoundation.org/us/newsletter/" + date;
    }

    void addSection(final Section section) {
        sections.add(section);
    }

    void printForWordpress(final PrintWriter writer) {
        writer.println("[su_accordion]");

        for (final Section section : sections) {
            if (!section.shouldSkipForWeb()) {
                section.printForWordpress(writer);
            }
        }

        writer.println("[/su_accordion]");

        writer.println(SoyRenderer.INSTANCE.renderWordpress(SoyRenderer.WordpressTemplate.FOOTER, new HashMap<>()));
        writer.flush();
    }

    void printForEmail(final PrintWriter writer) {
        writer.print(SoyRenderer.INSTANCE.renderResponsiveEmail(SoyRenderer.EmailTemplate.HTML_OPEN, new HashMap<>()));
        writer.print(SoyRenderer.INSTANCE.renderResponsiveEmail(SoyRenderer.EmailTemplate.HEAD,
                ImmutableMap.of(NewsletterCreator.TITLE_PARAM, title)));
        writer.print(SoyRenderer.INSTANCE.renderResponsiveEmail(SoyRenderer.EmailTemplate.BODY_OPEN, new HashMap<>()));
        writer.print(SoyRenderer.INSTANCE.renderResponsiveEmail(SoyRenderer.EmailTemplate.CONTENT_OPEN, new HashMap<>()));
        writer.print(SoyRenderer.INSTANCE.renderResponsiveEmail(SoyRenderer.EmailTemplate.HEADER_LOGO_AND_TITLE,
                ImmutableMap.of(NewsletterCreator.TITLE_PARAM, title)));

        final String newsletterLink = getWordpressLink();
        boolean isWhiteBackground = true;
        for (final Section section : sections) {
            if (!section.shouldSkipForEmail()) {
                section.printForEmail(writer, isWhiteBackground, newsletterLink);
                isWhiteBackground = !isWhiteBackground;
            }
        }

        writer.print(SoyRenderer.INSTANCE.renderResponsiveEmail(SoyRenderer.EmailTemplate.FOOTER_LOGO_AND_UNSUBSCRIBE,
                new HashMap<>()));

        writer.print(SoyRenderer.INSTANCE.renderResponsiveEmail(SoyRenderer.EmailTemplate.CONTENT_BODY_HTML_CLOSE,
                new HashMap<>()));
        writer.flush();
    }
}

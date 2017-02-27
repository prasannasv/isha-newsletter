package org.ishausa.publishing.newsletter;

import com.google.common.collect.ImmutableMap;
import org.ishausa.publishing.newsletter.renderer.SoyRenderer;

import javax.annotation.concurrent.Immutable;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * An independent news item that belongs to a particular section.
 *
 * Created by tosri on 12/4/2016.
 */
@Immutable
class Item {
    private final Section section;
    private final boolean isVideoItem;
    private final String title;
    private final String htmlContent;
    private final String htmlSummary;

    Item(final Section section,
         final String title,
         final String htmlContent,
         final String htmlSummary) {
        this(section, title, htmlContent, htmlSummary, false);
    }

    Item(final Section section,
         final String title,
         final String htmlContent,
         final String htmlSummary,
         final boolean isVideoItem) {
        this.section = section;
        this.title = title;
        this.htmlContent = htmlContent;
        this.htmlSummary = htmlSummary;
        this.isVideoItem = isVideoItem;
    }

    void printForWordpress(final PrintWriter writer, final boolean isAccordion) {
        if (isAccordion) {
            writer.println(SoyRenderer.INSTANCE.renderWordpress(SoyRenderer.WordpressTemplate.SPOILER,
                    ImmutableMap.of("title", title, "htmlContent", htmlContent)));
        } else {
            writer.println(htmlContent);
        }
    }

    void printForEmail(final PrintWriter writer, final boolean isBackgroundWhite, final String newsletterLink) {
        if (isVideoItem) {
            writer.println(htmlSummary);
        } else {
            final Map<String, Object> data = new HashMap<>();
            data.put("contentHtml", htmlSummary);
            data.put("isWhiteBackground", isBackgroundWhite);
            final boolean shouldSkipReadMoreLink = section.shouldSkipForWeb() || htmlContent.equals(htmlSummary);
            if (!shouldSkipReadMoreLink) {
                data.put("readMoreLink", newsletterLink);
            }
            writer.println(SoyRenderer.INSTANCE.renderResponsiveEmail(SoyRenderer.EmailTemplate.CONTENT_STANDARD, data));
        }
    }
}

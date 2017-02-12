package org.ishausa.publishing.newsletter;

import com.google.common.collect.ImmutableMap;
import org.ishausa.publishing.newsletter.renderer.SoyRenderer;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * Broad category of items in a newsletter.
 * Created by tosri on 12/4/2016.
 */
class Section {
    private final StandardSection sectionType;
    private final List<Item> items = new LinkedList<>();

    Section(final StandardSection sectionType) {
        this.sectionType = sectionType;
    }

    void addItem(final Item item) {
        items.add(item);
    }

    boolean isEmpty() {
        return items.isEmpty();
    }

    boolean shouldSkipForWeb() {
        return sectionType.isEmailOnly();
    }

    boolean shouldSkipForEmail() {
        return sectionType.isWebOnly();
    }

    boolean hasNoTitle() {
        return sectionType.hasNoTitle();
    }

    void printForWordpress(final PrintWriter writer) {
        writeSectionTitle(writer);
        for (final Item item : items) {
            item.printForWordpress(writer, sectionType.isAccordion());
        }
    }

    private void writeSectionTitle(final PrintWriter writer) {
        writer.println(SoyRenderer.INSTANCE.renderWordpress(SoyRenderer.WordpressTemplate.SECTION_HEADING,
                ImmutableMap.of("anchorId", sectionType.getAnchorId(),
                        "heading", sectionType.getHeading())));
    }

    void printForEmail(final PrintWriter writer, final boolean isWhiteBackground, final String newsletterLink) {
        writeSectionTitleForEmail(writer, isWhiteBackground);
        for (final Item item : items) {
            item.printForEmail(writer, isWhiteBackground, getLinkWithAnchor(newsletterLink));
        }
    }

    private void writeSectionTitleForEmail(final PrintWriter writer, final boolean isWhiteBackground) {
        writer.println(SoyRenderer.INSTANCE.renderResponsiveEmail(SoyRenderer.EmailTemplate.CONTENT_SECTION_TITLE,
                ImmutableMap.of("sectionTitle", sectionType.getHeading(),
                        "isWhiteBackground", isWhiteBackground)));
    }

    private String getLinkWithAnchor(final String newsletterLink) {
        return newsletterLink + "#" + sectionType.getAnchorId();
    }
}

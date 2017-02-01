package org.ishausa.publishing.newsletter;

/**
 * Created by tosri on 12/4/2016.
 */
public enum StandardSection {
    /*
     * The names here have a reference in the publishing_tool.soy in lower case.
     * If you rename this, be sure to update there as well.
     */
    VIDEO("This weeks video", "video", false /* isAccordion */),
    ABODE_HAPPENINGS("Abode Happenings", "adiyogi"),
    ARTICLE("Article", "article"),
    NEWS("New Announcements", "news"),
    VOLUNTEERING("Volunteering Opportunities", "volunteer"),
    PROGRAMS("Program Schedule", "programs"),
    SHARING("Sharing", "sharing"),
    LIFESTYLE("Life Style", "lifestyle");

    private final String heading;
    private final String anchorId;
    private final boolean isAccordion;

    StandardSection(final String heading, final String anchorId) {
        this(heading, anchorId, true);
    }

    StandardSection(final String heading, final String anchorId, final boolean isAccordion) {
        this.heading = heading;
        this.anchorId = anchorId;
        this.isAccordion = isAccordion;
    }

    public String getHeading() {
        return heading;
    }

    public String getAnchorId() {
        return anchorId;
    }

    public boolean isAccordion() {
        return isAccordion;
    }
}

package org.ishausa.publishing.newsletter;

import com.google.common.collect.ImmutableMap;
import com.google.template.soy.data.SoyListData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by tosri on 12/4/2016.
 */
public enum StandardSection {

    /*
     * The names here have a reference in the publishing_tool.soy in lower case.
     * If you rename this, be sure to update there as well.
     */
    VIDEO("Video", "video", Tag.NON_ACCORDION, Tag.EMAIL_AND_WEB_SAME),
    FEATURE_ARTICLE("Feature Article", "article"),
    PROGRAM_HIGHLIGHT("Program Highlight", "program_highlight"),
    PROGRAMS("Program Schedule", "programs", Tag.WEB_ONLY),
    ABODE_HAPPENINGS("Abode Happenings", "abode"),
    ANNOUNCEMENTS("Announcements", "announcements"),
    VOLUNTEERING("Volunteering Opportunities", "volunteer"),
    PHOTO_HIGHLIGHTS("Photo Highlights", "photos"),
    ISHA_IN_THE_NEWS("Isha in the News", "news"),
    SHARING("Experiences & Expressions", "sharing"),
    BANNER_AD("Banner Ad", "unused", Tag.EMAIL_ONLY, Tag.NO_TITLE),
    LIFESTYLE("Life Style", "lifestyle");

    enum Tag {
        NON_ACCORDION,
        EMAIL_AND_WEB_SAME,
        EMAIL_ONLY,
        WEB_ONLY,
        NO_TITLE,
    }

    /** This is the default heading for the section. */
    private final String heading;
    private final String anchorId;
    private final Set<Tag> tags = new HashSet<>();

    StandardSection(final String heading, final String anchorId, final Tag... tags) {
        this.heading = heading;
        this.anchorId = anchorId;
        if (tags != null) {
            this.tags.addAll(Arrays.asList(tags));
        }
    }

    public String getHeading() {
        return heading;
    }

    public String getAnchorId() {
        return anchorId;
    }

    public boolean isAccordion() {
        return !tags.contains(Tag.NON_ACCORDION);
    }

    public boolean hasNoTitle() {
        return tags.contains(Tag.NO_TITLE);
    }

    public boolean isEmailOnly() {
        return tags.contains(Tag.EMAIL_ONLY);
    }

    public boolean isWebOnly() {
        return tags.contains(Tag.WEB_ONLY);
    }

    public boolean isEmailAndWebSame() {
        return tags.contains(Tag.EMAIL_AND_WEB_SAME);
    }

    // {name: string, heading: string, hasNoTitle: bool, isEmailOnly: bool, isWebOnly: bool, isEmailAndWebSame: bool}
    private Map<String, ?> toSoyRecord() {
        final Map<String, Object> map = new HashMap<>();

        map.put("name", toString().toLowerCase());
        map.put("heading", getHeading());
        map.put("hasNoTitle", hasNoTitle());
        map.put("isEmailOnly", isEmailOnly());
        map.put("isWebOnly", isWebOnly());
        map.put("isEmailAndWebSame", isEmailAndWebSame());

        return map;
    }

    public static Map<String, ?> getSoyMapData() {
        final SoyListData soyListData = new SoyListData();

        for (final StandardSection section : values()) {
            soyListData.add(section.toSoyRecord());
        }

        return ImmutableMap.of("standardSections", soyListData);
    }
}

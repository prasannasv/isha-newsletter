package org.ishausa.publishing.newsletter;

import com.google.common.collect.ImmutableMap;
import com.google.template.soy.data.SoyListData;
import com.google.template.soy.data.SoyMapData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by tosri on 12/4/2016.
 */
public enum StandardSection {

    VIDEO("Video", "video", Tag.NON_ACCORDION, Tag.EMAIL_AND_WEB_SAME),
    FEATURED_ARTICLE("Featured Article", "article"),
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
    private Map<String, ?> toSoyRecord(final String title, final String summary, final String full) {
        final Map<String, Object> map = new HashMap<>();

        map.put("name", toString().toLowerCase());
        map.put("heading", getHeading());
        map.put("hasNoTitle", hasNoTitle());
        map.put("isEmailOnly", isEmailOnly());
        map.put("isWebOnly", isWebOnly());
        map.put("isEmailAndWebSame", isEmailAndWebSame());
        map.put("title", title);
        map.put("summary", summary);
        map.put("full", full);

        return map;
    }

    public static Map<String, ?> getSoyMapData() {
        return getSoyMapData(new HashMap<>());
    }

    public static Map<String, ?> getSoyMapData(final Map<String, List<String>> priorContents) {
        //Newsletter metadata
        final SoyMapData metadata = new SoyMapData();
        metadata.put("title", getFirstOrDefault(priorContents, "title", "Newsletter Title"));
        metadata.put("date", getFirstOrDefault(priorContents, "date", "yyyy-mm-dd"));

        //Newsletter section data
        final SoyListData soyListData = new SoyListData();

        for (final StandardSection section : values()) {
            final String title = getFirstOrDefault(priorContents,
                    section.toString().toLowerCase() + "-title", section.getHeading());
            final String summary = getFirstOrDefault(priorContents,
                    section.toString().toLowerCase() + "-summary", "Paste email contents here");
            final String fullContents = getFirstOrDefault(priorContents,
                    section.toString().toLowerCase() + "-full", "Paste full contents here");
            soyListData.add(section.toSoyRecord(title, summary, fullContents));
        }

        return ImmutableMap.of("metadata", metadata,
                "standardSections", soyListData);
    }

    private static String getFirstOrDefault(final Map<String, List<String>> priorContents,
                                            final String key,
                                            final String defaultValue) {
        final List<String> values = priorContents.get(key);
        return values != null && values.size() > 0 ? values.get(0) : defaultValue;
    }
}

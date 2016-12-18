package org.ishausa.publishing.newsletter;

import org.ishausa.publishing.newsletter.renderer.SoyRenderer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tosri on 12/4/2016.
 */
public class VideoLinks {
    static Section createSection(final String videoLink, final String videoContent) {
        if (videoLink.contains("youtube")) {
            return createYoutubeVideoSection(videoLink, videoContent);
        } else if (videoLink.contains("facebook")) {
            return createFacebookVideoSection(videoLink, videoContent);
        }
        throw new IllegalArgumentException("Don't know how to parse given videoLink: " + videoLink);
    }

    private static Section createYoutubeVideoSection(final String link, final String videoContent) {
        final String videoId = link.split("=")[1];

        final Map<String, Object> soyMapData = new HashMap<>(); //any map that supports null values
        soyMapData.put("videoId", videoId);
        soyMapData.put("htmlContent", videoContent); //videoContent could be null

        final String htmlContent = SoyRenderer.INSTANCE.renderWordpress(
                SoyRenderer.Template.VIDEO_YOUTUBE, soyMapData);

        final Item item = new Item("Unused", htmlContent, htmlContent);
        final Section section = new Section(StandardSection.VIDEO);
        section.addItem(item);

        return section;
    }

    private static Section createFacebookVideoSection(final String videoLink, final String videoContent) {
        try {
            final Map<String, Object> soyMapData = new HashMap<>();
            soyMapData.put("encodedUrl", URLEncoder.encode(videoLink, "UTF-8"));
            soyMapData.put("htmlContent", videoContent);

            final String htmlContent = SoyRenderer.INSTANCE.renderWordpress(
                    SoyRenderer.Template.VIDEO_FACEBOOK, soyMapData);

            final Item item = new Item("Unused", htmlContent, htmlContent);
            final Section section = new Section(StandardSection.VIDEO);
            section.addItem(item);

            return section;
        } catch (final UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}

package org.ishausa.publishing.newsletter;

import org.ishausa.publishing.newsletter.renderer.SoyRenderer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to take care of everything related to the video we have at the top of the newsletter.
 *
 * Created by tosri on 12/4/2016.
 */
class VideoLinks {
    static Section createSection(final String videoLink, final String videoContent, final String wordpressLink) {
        if (videoLink.contains("youtube") || videoLink.contains("youtu.be")) {
            return createYoutubeVideoSection(videoLink, videoContent, wordpressLink);
        } else if (videoLink.contains("facebook")) {
            return createFacebookVideoSection(videoLink, videoContent);
        }
        throw new IllegalArgumentException("Don't know how to parse given videoLink: " + videoLink);
    }

    private static Section createYoutubeVideoSection(final String link,
                                                     final String videoContent,
                                                     final String wordpressLink) {
        final String videoId;
        if (link.contains("=")) {
            videoId = link.split("=")[1];
        } else {
            // of the form: https://youtu.be/QtzHtP3YdHA
            videoId = link.split("/")[3];
        }

        final Map<String, Object> soyMapData = new HashMap<>(); //any map that supports null values
        soyMapData.put("videoId", videoId);
        soyMapData.put("htmlContent", videoContent); //videoContent could be null

        final String htmlContent = SoyRenderer.INSTANCE.renderWordpress(
                SoyRenderer.WordpressTemplate.VIDEO_YOUTUBE, soyMapData);

        soyMapData.put("newsletterLink", wordpressLink + "#video");
        final String htmlSummary = SoyRenderer.INSTANCE.renderResponsiveEmail(
                SoyRenderer.EmailTemplate.CONTENT_VIDEO, soyMapData);

        final Item item = new Item("Unused", htmlContent, htmlSummary, true);
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
                    SoyRenderer.WordpressTemplate.VIDEO_FACEBOOK, soyMapData);

            final Item item = new Item("Unused", htmlContent, htmlContent, true);
            final Section section = new Section(StandardSection.VIDEO);
            section.addItem(item);

            return section;
        } catch (final UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}

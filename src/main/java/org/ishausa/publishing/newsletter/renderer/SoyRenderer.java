package org.ishausa.publishing.newsletter.renderer;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.tofu.SoyTofu;

import java.io.File;
import java.util.Map;

/**
 * Helper that knows how to render content using the Soy templates.
 *
 * Created by tosri on 12/3/2016.
 */
public class SoyRenderer {

    public enum PublishingToolTemplate {
        INDEX,
        OUTPUT_LINK;

        @Override
        public String toString() {
            return "." + name().toLowerCase();
        }
    }

    public enum WordpressTemplate {
        SECTION_HEADING,
        SPOILER,
        VIDEO_YOUTUBE,
        VIDEO_FACEBOOK,
        FOOTER;

        @Override
        public String toString() {
            return "." + name().toLowerCase();
        }
    }

    public enum EmailTemplate {
        HTML_OPEN(".htmlOpen"),
        HEAD(".head"),
        BODY_OPEN(".bodyOpen"),
        HIDDEN_SNEAK_PREVIEW(".hiddenSneakPreview"),
        CONTENT_OPEN(".contentOpen"),
        HEADER_LOGO_AND_TITLE(".headerLogoAndTitle"),
        CONTENT_VIDEO(".contentVideo"),
        CONTENT_SECTION_TITLE(".contentSectionTitle"),
        CONTENT_STANDARD(".contentStandard"),
        FOOTER_LOGO_AND_UNSUBSCRIBE(".footerLogoAndUnsubscribe"),
        CONTENT_BODY_HTML_CLOSE(".contentBodyHtmlClose");

        private final String templateName;

        EmailTemplate(final String templateName) {
            this.templateName = templateName;
        }

        @Override
        public String toString() {
            return templateName;
        }
    }

    public static final SoyRenderer INSTANCE = new SoyRenderer();

    private final SoyTofu serviceTofu;
    private final SoyTofu wordpressTofu;
    private final SoyTofu responsiveEmailTofu;

    private SoyRenderer() {
        final SoyFileSet sfs = SoyFileSet.builder()
                .add(new File("./src/main/webapp/template/publishing_tool.soy"))
                .add(new File("./src/main/webapp/template/wordpress.soy"))
                .add(new File("./src/main/webapp/template/litmus_slate_email.soy"))
                .build();
        serviceTofu = sfs.compileToTofu().forNamespace("org.ishausa.publishing.newsletter");
        wordpressTofu = sfs.compileToTofu().forNamespace("org.ishausa.publishing.newsletter.wordpress");
        responsiveEmailTofu = sfs.compileToTofu().forNamespace("org.ishausa.publishing.newsletter.email.responsive");
    }

    public String render(final PublishingToolTemplate template, final Map<String, ?> data) {
        return serviceTofu.newRenderer(template.toString()).setData(data).render();
    }

    public String renderWordpress(final WordpressTemplate template, final Map<String, ?> data) {
        return wordpressTofu.newRenderer(template.toString()).setData(data).render();
    }

    public String renderResponsiveEmail(final EmailTemplate template, final Map<String, ?> data) {
        return responsiveEmailTofu.newRenderer(template.toString()).setData(data).render();
    }
}

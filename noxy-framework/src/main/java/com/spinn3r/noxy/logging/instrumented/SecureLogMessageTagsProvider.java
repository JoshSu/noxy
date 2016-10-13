package com.spinn3r.noxy.logging.instrumented;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.spinn3r.artemis.init.advertisements.Caller;
import com.spinn3r.artemis.metrics.tags.SimpleTagsProvider;
import com.spinn3r.artemis.metrics.tags.TagNameSets;
import com.spinn3r.artemis.metrics.tags.Tags;
import com.spinn3r.metrics.kairosdb.Tag;
import com.spinn3r.noxy.logging.LogMessage;
import com.spinn3r.noxy.logging.SecureLogMessage;

import java.util.List;

import static com.spinn3r.metrics.kairosdb.TaggedMetrics.*;

/**
 *
 */
@Singleton
public class SecureLogMessageTagsProvider extends SimpleTagsProvider<SecureLogMessage> {

    private static final String CALLER = "caller";
    private static final String METHOD = "method";

    private final Caller caller;

    @Inject
    SecureLogMessageTagsProvider(Caller caller) {
        this.caller = caller;

        setDenormalizedTagNames( TagNameSets.tagNameSet( CALLER ),
                                 TagNameSets.tagNameSet( CALLER, METHOD ).withSuffixLiteralTags( CALLER ) );

        setDenormalizedOnly( true );

    }

    @Override
    public List<Tag> getTags(SecureLogMessage logMessage ) {

        List<Tag> tags = Lists.newArrayList();

        tags.add( tag( CALLER, caller.get() ) );
        tags.add( tag( METHOD, logMessage.getHttpMethod().name() ) );

        // TODO: duration tokenized?
        // TODO: site and domain of the URL?
        // TODO: include host resolution informaiton?

        return tags;

    }

}

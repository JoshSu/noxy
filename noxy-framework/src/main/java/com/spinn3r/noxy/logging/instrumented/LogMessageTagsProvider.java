package com.spinn3r.noxy.logging.instrumented;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.spinn3r.artemis.init.advertisements.Caller;
import com.spinn3r.artemis.metrics.tags.SimpleTagsProvider;
import com.spinn3r.artemis.metrics.tags.TagNameSets;
import com.spinn3r.artemis.metrics.tags.Tags;
import com.spinn3r.metrics.kairosdb.Tag;
import com.spinn3r.noxy.logging.LogMessage;

import java.util.List;

import static com.spinn3r.metrics.kairosdb.TaggedMetrics.tag;

/**
 *
 */
public class LogMessageTagsProvider extends SimpleTagsProvider<LogMessage> {

    private static final String CALLER = "caller";
    private static final String METHOD = "method";
    private static final String STATUS = "status";
    private static final String STATUS_TOKENIZED = "status-tokenized";

    private final Caller caller;

    @Inject
    LogMessageTagsProvider(Caller caller) {
        this.caller = caller;

        setDenormalizedTagNames( TagNameSets.tagNameSet( CALLER ),
                                 TagNameSets.tagNameSet( CALLER, METHOD ).withSuffixLiteralTags( CALLER ),
                                 TagNameSets.tagNameSet( CALLER, STATUS ).withSuffixLiteralTags( CALLER ),
                                 TagNameSets.tagNameSet( CALLER, STATUS_TOKENIZED ).withSuffixLiteralTags( CALLER ) );

        setDenormalizedOnly( true );

    }

    @Override
    public List<Tag> getTags(LogMessage logMessage ) {

        List<Tag> tags = Lists.newArrayList();

        tags.add( tag( CALLER, caller.get() ) );
        tags.add( tag( METHOD, logMessage.getHttpMethod().name() ) );
        tags.add( tag( STATUS, logMessage.getHttpResponseStatus().code() ) );
        tags.add( tag( STATUS_TOKENIZED, Tags.tokenized(logMessage.getHttpResponseStatus().code(), 100, "xx" ) ) );

        // TODO: duration tokenized?
        // TODO: site and domain of the URL?
        // TODO: include host resolution informaiton?

        return tags;

    }

}

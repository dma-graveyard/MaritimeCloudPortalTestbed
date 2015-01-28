/* Copyright 2014 Danish Maritime Authority.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.maritimecloud.portal.domain.infrastructure.axon;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;
import org.axonframework.domain.DomainEventMessage;
import org.axonframework.domain.DomainEventStream;
import org.axonframework.eventstore.EventVisitor;
import org.axonframework.eventstore.fs.FileSystemEventStore;
import org.axonframework.eventstore.fs.SimpleEventFileResolver;
import org.axonframework.eventstore.management.Criteria;
import org.axonframework.eventstore.management.CriteriaBuilder;
import org.axonframework.eventstore.management.EventStoreManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Simpleminded extension of FileSystemEventStore that supports EventStoreManagement in the simplest variant (no CriteriaBuilder support!)
 * 
 * @author Christoffer Børrild
 */
public class ReplayableFileSystemEventStore extends FileSystemEventStore implements EventStoreManagement {
    private final static Logger LOG = LoggerFactory.getLogger(ReplayableFileSystemEventStore.class);

    private final File baseDir;

    private final SortedSet<DomainEventMessage> domainEventMessagesCache = new ConcurrentSkipListSet<>((DomainEventMessage m1, DomainEventMessage m2) -> {
        // Compare:
        long r = m1.getTimestamp().getMillis() - m2.getTimestamp().getMillis();
        int i = r < 0 ? -1 : r == 0 ? 0 : 1;
        
        if(i == 0){
            if(m1.getAggregateIdentifier().equals(m2.getAggregateIdentifier())){
                // same aggregaste at same time are ordered by sequence number
               long r2 = m1.getSequenceNumber() - m2.getSequenceNumber();
               return r2 < 0 ? -1 : r2 == 0 ? 0 : 1;
            } else {
               // some arbitrary but conscequent ordering
               long r2 = m1.getIdentifier().compareTo(m2.getIdentifier());
            };
        }
        
        return i;
    });

    public ReplayableFileSystemEventStore(File baseDir) {
        super(new SimpleEventFileResolver(baseDir));
        this.baseDir = baseDir;
    }

    @Override
    public void visitEvents(EventVisitor visitor) {
        
        if(!baseDir.exists()){
            // skipping since no file store
            LOG.info("No event store found at {}", baseDir.getAbsoluteFile());
            return;            
        }

        resetEventCache();
        

        // scan event store for types
        List<File> types = scanForTypes();

        // for each type scan for aggregates
        types.stream().forEach((type) -> {
            List<File> aggregateFiles = scanForAggregates(type);
            LOG.info("indexing {} aggregates of type '{}'", aggregateFiles.size(), type.getName());

            // for each aggregate register event messages in a big sorted set ordered by timestamp
            aggregateFiles.stream().forEach((aggregateFile) -> {
                readAndRegisterAggregateEvents(aggregateFile);
            });
        });

        // finally, call visitor for each message in sequence 
        replayEvents(visitor);
        LOG.info("Replayed {} events from {} aggregates.", domainEventMessagesCache.size(), types.size());

        resetEventCache();
    }

    @Override
    public void visitEvents(Criteria criteria, EventVisitor visitor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CriteriaBuilder newCriteriaBuilder() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private List<File> scanForTypes() {
        List<File> types = new ArrayList<>();
        for (File file : baseDir.listFiles()) {
            if (file.isDirectory()) {
                types.add(file);
            }
        }
        return types;
    }

    private List<File> scanForAggregates(File typeDir) {
        List<File> aggregateFiles = new ArrayList<>();
        for (File file : typeDir.listFiles()) {
            if (file.isFile()) {
                aggregateFiles.add(file);
            }
        }
        return aggregateFiles;
    }

    private void readAndRegisterAggregateEvents(File aggregateFile) {

        DomainEventStream eventStream = readEvents(getType(aggregateFile), getIdentifier(aggregateFile));

        while (eventStream.hasNext()) {
            register(eventStream.next());
        }
    }

    public static String getType(File aggregateFile) {
        return aggregateFile.getParentFile().getName();
    }

    public static String getIdentifier(File aggregateIndentifier) {
        try {
            return URLDecoder.decode(aggregateIndentifier.getName(), "UTF-8").replace(".events", "");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void resetEventCache() {
        domainEventMessagesCache.clear();
    }

    private void register(DomainEventMessage domainEventMessage) {
        domainEventMessagesCache.add(domainEventMessage);
    }

    private void replayEvents(EventVisitor visitor) {
        domainEventMessagesCache.stream().forEach((domainEventMessage) -> {
            visitor.doWithEvent(domainEventMessage);
        });
    }

}

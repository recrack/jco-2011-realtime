package org.springsprout.realtime.oxquiz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springsprout.realtime.streamhub.Subscription;

import com.streamhub.api.Client;
import com.streamhub.api.JsonPayload;
import com.streamhub.api.Payload;
import com.streamhub.api.PushServer;

@Component
public class EntryInOutSubscription implements Subscription {

    private static final Logger logger = LoggerFactory.getLogger(EntryInOutSubscription.class);
    
    @Autowired
    private PushServer server;
    
    @Autowired
    private EntryStore clientStore;

    @Override
    public String getTopic() {
        return "entry";
    }

    @Override
    public void onSubscribe(Client client) {
        clientStore.rememberEntry(client);
        
        Payload entry = new JsonPayload(getTopic());
        entry.addField("state", "entryIn");
        entry.addField("entryId", client.getUid());
        
        server.publish(getTopic(), entry);
        
        logger.info("new entry connection: {}", client.getUid());        
    }

    @Override
    public void onUnSubscribe(Client client) {
        Payload entry = new JsonPayload(getTopic());
        entry.addField("state", "entryOut");
        entry.addField("entryId", client.getUid());
        
        server.publish(getTopic(), entry);
        
        clientStore.forgetEntry(client);
        
        logger.info("disconnection entry: {}", client.getUid());
        
    }

}
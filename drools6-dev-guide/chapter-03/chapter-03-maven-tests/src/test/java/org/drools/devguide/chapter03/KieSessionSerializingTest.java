package org.drools.devguide.chapter03;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class KieSessionSerializingTest {

    @Test
    public void loadingRulesFromExistingBlob() throws Exception {
        System.out.println("### Running loadingRulesFromExistingBlob() Test ###");
        KieServices ks = KieServices.Factory.get();
        final ReleaseId releaseId = ks.newReleaseId("org.drools.devguide", "chapter-03-kjar-simple-discounts", "0.1-SNAPSHOT");
        KieContainer kContainer = ks.newKieContainer(releaseId);
        Results results = kContainer.verify();
        results.getMessages().stream().forEach((message) -> {
            System.out.println(">> Message ( "+message.getLevel()+" ): "+message.getText());
        });
        assertThat(false, equalTo(results.hasMessages(Message.Level.ERROR)));
        KieSession kieSession = kContainer.newKieSession("rules.simple.discount");
        KieBase kieBase = kContainer.getKieBase("rules.simple");

        ByteArrayOutputStream output1 = new ByteArrayOutputStream();
        final Marshaller marshaller = ks.getMarshallers().newMarshaller(kieBase);
        marshaller.marshall(output1, kieSession);
        byte[] kieSessionData1 = output1.toByteArray();
        KieSession clonedKieSession1 = marshaller.unmarshall(new ByteArrayInputStream(kieSessionData1));
        assertThat(clonedKieSession1, notNullValue());
        assertThat(clonedKieSession1.getIdentifier(), greaterThan(kieSession.getIdentifier()));
        
        assertThat(kieSession.getFactCount(), equalTo(0L));
        assertThat(clonedKieSession1.getFactCount(), equalTo(0L));
        
        clonedKieSession1.insert("New piece of data");
        assertThat(kieSession.getFactCount(), equalTo(0L));
        assertThat(clonedKieSession1.getFactCount(), equalTo(1L));
        
        ByteArrayOutputStream output2 = new ByteArrayOutputStream();
        marshaller.marshall(output2, clonedKieSession1);
        byte[] kieSessionData2 = output2.toByteArray();
        KieSession clonedKieSession2 = marshaller.unmarshall(new ByteArrayInputStream(kieSessionData2));
        assertThat(clonedKieSession2, notNullValue());
        assertThat(clonedKieSession2.getIdentifier(), greaterThan(clonedKieSession1.getIdentifier()));
        
        assertThat(kieSession.getFactCount(), equalTo(0L));
        assertThat(clonedKieSession1.getFactCount(), equalTo(1L));
        assertThat(clonedKieSession2.getFactCount(), equalTo(1L));

        clonedKieSession2.insert("Another piece of data");
        assertThat(kieSession.getFactCount(), equalTo(0L));
        assertThat(clonedKieSession1.getFactCount(), equalTo(1L));
        assertThat(clonedKieSession2.getFactCount(), equalTo(2L));
    }
}

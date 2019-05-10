package org.drools6.book;

import org.drools.devguide.eshop.model.Item;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Bootstrapping the Rule Engine ..." );
        // Bootstrapping a Rule Engine Session
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        KieSession kSession =  kContainer.newKieSession();
        
        Item item = new Item("A", 123.0,234.0);
        System.out.println( "Item Category: " + item.getCategory()); 
        kSession.insert(item);
        int fired = kSession.fireAllRules();
        System.out.println( "Number of Rules executed = " + fired );
        System.out.println( "Item Category: " + item.getCategory()); 

    }
}

package org.drools6.book;

import java.io.PrintStream;
import javax.inject.Inject;
import org.drools.devguide.eshop.model.Item;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieSession;

public class App 
{
    
    @Inject
    @KSession("") // This is not working track: https://issues.jboss.org/browse/DROOLS-755 
                   // it should be @KSession() for the default session
    KieSession kSession;
    
    public void go(PrintStream out){
        Item item = new Item("A", 123.0,234.0);
        out.println( "Item Category: " + item.getCategory()); 
        kSession.insert(item);
        int fired = kSession.fireAllRules();
        out.println( "Number of Rules executed = " + fired );
        out.println( "Item Category: " + item.getCategory()); 
    }
    
    public static void main( String[] args )
    {
        Weld w = new Weld();

        WeldContainer wc = w.initialize();
        App bean = wc.instance().select(App.class).get();
        bean.go(System.out);

        w.shutdown();
        
        

    }
}

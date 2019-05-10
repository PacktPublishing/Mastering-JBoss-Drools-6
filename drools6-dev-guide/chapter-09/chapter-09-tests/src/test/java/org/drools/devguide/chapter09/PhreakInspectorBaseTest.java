/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.devguide.chapter09;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.drools.devguide.BaseTest;
import org.drools.devguide.phreakinspector.model.PhreakInspector;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author esteban
 */
public abstract class PhreakInspectorBaseTest extends BaseTest{
    
    protected static File targetDirectory = new File("target/viz");
    
    protected PhreakInspector inspector;
    
    @BeforeClass
    public static void doBeforeClass() throws IOException{
        if (!targetDirectory.exists()){
            targetDirectory.mkdir();
        }
    }
    
    @Before
    public void doBefore(){
        this.inspector = new PhreakInspector();
    }
    
    protected void writeFile(String targetFileName, InputStream is) throws IOException {
        try (FileWriter fileWriter = new FileWriter(new File(targetDirectory, targetFileName))){
            IOUtils.write(IOUtils.toByteArray(is), fileWriter);
        }
    }
    
}

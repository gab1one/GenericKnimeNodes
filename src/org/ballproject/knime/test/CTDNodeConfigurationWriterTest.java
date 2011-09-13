package org.ballproject.knime.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;

import org.ballproject.knime.base.config.CTDNodeConfigurationReader;
import org.ballproject.knime.base.config.CTDNodeConfigurationWriter;
import org.ballproject.knime.base.config.NodeConfiguration;
import org.ballproject.knime.test.data.TestDataSource;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.junit.Test;

public class CTDNodeConfigurationWriterTest
{

	@Test
	public void test1() throws Exception
	{
		CTDNodeConfigurationReader reader = new CTDNodeConfigurationReader();
		NodeConfiguration config = reader.read(TestDataSource.class.getResourceAsStream("test.ctd"));
		
		CTDNodeConfigurationWriter writer = new CTDNodeConfigurationWriter(config.getXML());
		
		writer.setParameterValue("x", "22.4");
		writer.setParameterValue("1.end_id", "2204");
		writer.setParameterValue("1.2.z", "1979");
		writer.setParameterValue("1.o", "filename");
		
		File out = File.createTempFile("CTDWriter", "TEST");
		out.deleteOnExit();
		
		writer.write(out.getAbsolutePath());
		
		SAXReader rd = new SAXReader();
		
        Document doc = rd.read(new FileInputStream(out));
        
        
        String value = doc.valueOf("/tool/PARAMETERS/ITEM[@name='x']/@value");
        assertEquals("22.4",value);
        
        value = doc.valueOf("/tool/PARAMETERS/NODE[@name='1']/ITEM[@name='end_id']/@value");
        assertEquals("2204",value);
        
        value = doc.valueOf("/tool/PARAMETERS/NODE[@name='1']/NODE[@name='2']/ITEM[@name='z']/@value");
        assertEquals("1979",value);
        
        value = doc.valueOf("/tool/PARAMETERS/NODE[@name='1']/ITEM[@name='o']/@value");
        assertEquals("filename",value);
	}
	
	
}

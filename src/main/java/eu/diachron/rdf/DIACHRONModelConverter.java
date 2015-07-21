package eu.diachron.rdf;

import eu.diacron.crawlservice.config.Configuration;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.athena.imis.diachron.archive.datamapping.MultidimensionalConverter;
import org.athena.imis.diachron.archive.datamapping.OntologyConverter;

public class DIACHRONModelConverter
{    
    public void convert(String filename, String outfilename)
    {
        System.out.println("DIACHRON Converting file " + filename);
        FileInputStream fis = null;
        File outputFile = new File(outfilename);
        FileOutputStream fos = null;
        OntologyConverter converter = null;

        try
        {
            fis = new FileInputStream(filename);
            fos = new FileOutputStream(outputFile);
            converter = new OntologyConverter();
            converter.convert(fis, fos, "test");

            //MultidimensionalConverter converter = new MultidimensionalConverter();
            //converter.convert(fis, fos, file.getName().substring(file.getName().lastIndexOf(".")+1), "test_qb_data");

            fis.close();
            fos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
    }
}

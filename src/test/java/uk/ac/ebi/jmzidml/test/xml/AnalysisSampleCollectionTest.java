package uk.ac.ebi.jmzidml.test.xml;

import junit.framework.TestCase;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.mzidml.AbstractContact;
import uk.ac.ebi.jmzidml.model.mzidml.AnalysisSampleCollection;
import uk.ac.ebi.jmzidml.model.mzidml.ContactRole;
import uk.ac.ebi.jmzidml.model.mzidml.Sample;
import uk.ac.ebi.jmzidml.xml.io.MzIdentMLUnmarshaller;

import java.net.URL;

/**
 * Package  : uk.ac.ebi.jmzidml.test.xml
 * Author   : riteshk
 * Date     : Sep 18, 2010
 */
public class AnalysisSampleCollectionTest extends TestCase {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AnalysisSampleCollectionTest.class);


    public void testAnalysisSampleCollectionInformation() throws Exception {

        URL xmlFileURL = AnalysisSampleCollectionTest.class.getClassLoader().getResource("Mascot_MSMS_example.mzid");
        assertNotNull(xmlFileURL);

        MzIdentMLUnmarshaller unmarshaller = new MzIdentMLUnmarshaller(xmlFileURL, true);
        assertNotNull(unmarshaller);

        AnalysisSampleCollection asc =  unmarshaller.unmarshal(AnalysisSampleCollection.class);
        assertNotNull(asc);

        assertEquals(2, asc.getSample().size());
        Sample sample = asc.getSample().get(0);
        assertEquals(2, sample.getContactRole().size());
        assertEquals(2, sample.getParamGroup().size());
        ContactRole contactRole = sample.getContactRole().get(0);

        AbstractContact contact = contactRole.getContact();
        if (MzIdentMLElement.ContactRole.isAutoRefResolving()) {
            assertNotNull(contact);
            System.out.println("resolving");
        } else {
            assertNull(contact);
            System.out.println("not resolving");
        }
        assertEquals("MS:1001267", contactRole.getRole().getCvParam().getAccession());
/**

            assertEquals("We expect one CvParam.", 2, sample.getParamGroup().size());
            assertEquals("We expect one UserParam.", 1, sample.getParamGroup().size());
            CvParam param = (CvParam)sample.getParamGroup().get(0);
            assertNotNull(param);
            assertTrue(param instanceof SampleCvParam);

            // todo use facadelist here.

*/
/*
            sample.getCvParam().add(new CvParam());        // add a new CvParam
            assertEquals(2, sample.getCvParam().size());   // now there are two CvParams
            assertEquals(1, sample.getUserParam().size()); // still only one UserParam
            sample.getUserParam().add(new UserParam());    // add a new UserParam
            assertEquals(2, sample.getUserParam().size()); // now there are two UserParams
            assertEquals(2, sample.getCvParam().size());   // still only two CvParams
*//*

        }
*/
    }

}

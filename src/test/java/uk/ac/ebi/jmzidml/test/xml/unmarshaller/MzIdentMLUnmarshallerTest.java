package uk.ac.ebi.jmzidml.test.xml.unmarshaller;

import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.mzidml.*;
import uk.ac.ebi.jmzidml.model.mzidml.params.*;
import uk.ac.ebi.jmzidml.xml.io.MzIdentMLUnmarshaller;

import javax.naming.ConfigurationException;
import javax.xml.bind.JAXBException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


/**
 * @author Florian Reisinger
 *         Date: 02-Dec-2010
 * @since 1.0
 */
@Ignore
public class MzIdentMLUnmarshallerTest {

    private static MzIdentMLUnmarshaller unmarshaller;
    private static MzIdentMLUnmarshaller unmarshaller_1_2;

    static {
        URL xmlFileURL = MzIdentMLUnmarshallerTest.class.getClassLoader().getResource("Mascot_MSMS_example.mzid");
        assertNotNull(xmlFileURL);

        unmarshaller = new MzIdentMLUnmarshaller(xmlFileURL);
        assertNotNull(unmarshaller);
        
        URL xmlFileURL_1_2 = MzIdentMLUnmarshallerTest.class.getClassLoader().getResource("Mascot_MSMS_example_1.2.mzid");
        assertNotNull(xmlFileURL_1_2);
        
        unmarshaller_1_2 = new MzIdentMLUnmarshaller(xmlFileURL_1_2);
        assertNotNull(unmarshaller_1_2);
    }

    @Test
    public void testAttributeRetrieval() throws Exception {
        // Number of providers
        int total = unmarshaller.getObjectCountForXpath(MzIdentMLElement.AnalysisSoftware.getXpath());
        assertEquals(2, total);

        AnalysisSoftware software = unmarshaller.unmarshal(MzIdentMLElement.AnalysisSoftware);
        assertNotNull(software);
        String id = "AS_mascot_server";
        Map<String, String> attributes = unmarshaller.getElementAttributes(id, MzIdentMLElement.AnalysisSoftware.getClazz());
        assertNotNull(attributes);
        // at the very least the id attribute has to be present
        // (otherwise we would not have found the element!)
        assertEquals(id, attributes.get("id"));

        // there should also be 4 attributes in this case
        // the 'id' plus 'name', 'version' and 'URI' attributes
        assertEquals(4, attributes.keySet().size());
        assertEquals("Mascot Server", attributes.get("name"));
        assertEquals("2.2.03", attributes.get("version"));
        assertEquals("http://www.matrixscience.com/search_form_select.html", attributes.get("uri"));

        // test with another element
        Cv cv = unmarshaller.unmarshal(MzIdentMLElement.CV);
        assertNotNull(cv);

        id = "PSI-MS";
        attributes = unmarshaller.getElementAttributes(id, MzIdentMLElement.CV.getClazz());
        assertNotNull(attributes);
        // at the very least the id attribute has to be present
        // (otherwise we would not have found the element!)
        assertEquals(id, attributes.get("id"));

        // there should also be 4 attributes in this case
        // the 'id' plus 'fullName', 'version' and 'URI' attributes
        assertEquals(4, attributes.keySet().size());
        assertEquals("Proteomics Standards Initiative Mass Spectrometry Vocabularies", attributes.get("fullName"));
        assertEquals("2.25.0", attributes.get("version"));
        assertEquals("http://psidev.cvs.sourceforge.net/viewvc/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo", attributes.get("uri"));
    }
    
        @Test
    public void testAttributeRetrieval12() throws Exception {
        // Number of providers
        int total = unmarshaller_1_2.getObjectCountForXpath(MzIdentMLElement.AnalysisSoftware.getXpath());
        assertEquals(2, total);

        AnalysisSoftware software = unmarshaller_1_2.unmarshal(MzIdentMLElement.AnalysisSoftware);
        assertNotNull(software);
        String id = "mascot_server";
        Map<String, String> attributes = unmarshaller_1_2.getElementAttributes(id, MzIdentMLElement.AnalysisSoftware.getClazz());
        assertNotNull(attributes);
        // at the very least the id attribute has to be present
        // (otherwise we would not have found the element!)
        assertEquals(id, attributes.get("id"));

        // there should also be 4 attributes in this case
        // the 'id' plus 'name', 'version' and 'URI' attributes
        assertEquals(4, attributes.keySet().size());
        assertEquals("Mascot Server", attributes.get("name"));
        assertEquals("2.3", attributes.get("version"));
        assertEquals("http://www.matrixscience.com/search_form_select.html", attributes.get("uri"));

        // test with another element
        Cv cv = unmarshaller_1_2.unmarshal(MzIdentMLElement.CV);
        assertNotNull(cv);

        id = "PSI-MS";
        attributes = unmarshaller_1_2.getElementAttributes(id, MzIdentMLElement.CV.getClazz());
        assertNotNull(attributes);
        // at the very least the id attribute has to be present
        // (otherwise we would not have found the element!)
        assertEquals(id, attributes.get("id"));

        // there should also be 4 attributes in this case
        // the 'id' plus 'fullName', 'version' and 'URI' attributes
        assertEquals(4, attributes.keySet().size());
        assertEquals("Proteomics Standards Initiative Mass Spectrometry Vocabularies", attributes.get("fullName"));
        assertEquals("2.26.0", attributes.get("version"));
        assertEquals("http://psidev.cvs.sourceforge.net/viewvc/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo", attributes.get("uri"));
    }


    @Test
    public void testRootAttributeRetrieval() throws Exception {
        // check the root element (mzIdentML) attributes
        assertEquals("example_mzidentml_1", unmarshaller.getMzIdentMLId());
        assertEquals("1.1.0", unmarshaller.getMzIdentMLVersion());
        assertEquals("test-mzid", unmarshaller.getMzIdentMLName());

        // now check all the other attributes
        String id = "example_mzidentml_1";
        Map<String, String> attributes = unmarshaller.getElementAttributes(id, MzIdentMLElement.MzIdentML.getClazz());
        assertNotNull(attributes);
        assertEquals(7, attributes.keySet().size());
        assertEquals("2009-08-18T17:59:55", attributes.get("creationDate"));
        assertEquals("http://psidev.info/psi/pi/mzIdentML/1.1", attributes.get("xmlns"));
        assertEquals("http://psidev.info/psi/pi/mzIdentML/1.1 ../resources/mzIdentML1.1.0.xsd", attributes.get("xsi:schemaLocation"));
        assertEquals("http://www.w3.org/2001/XMLSchema-instance", attributes.get("xmlns:xsi"));
    }

    @Test
    public void testRootAttributeRetrieval12() throws Exception {
        // check the root element (mzIdentML) attributes
        assertEquals("example_mzidentml_2", unmarshaller_1_2.getMzIdentMLId());
        assertEquals("1.2.0", unmarshaller_1_2.getMzIdentMLVersion());
        assertEquals("test-mzid", unmarshaller_1_2.getMzIdentMLName());

        // now check all the other attributes
        String id = "example_mzidentml_2";
        Map<String, String> attributes = unmarshaller_1_2.getElementAttributes(id, MzIdentMLElement.MzIdentML.getClazz());
        assertNotNull(attributes);
        assertEquals(7, attributes.keySet().size());
        assertEquals("2019-08-18T17:59:55", attributes.get("creationDate"));
        assertEquals("http://psidev.info/psi/pi/mzIdentML/1.2", attributes.get("xmlns"));
        assertEquals("http://psidev.info/psi/pi/mzIdentML/1.2 ../resources/mzIdentML1.2.0.xsd", attributes.get("xsi:schemaLocation"));
        assertEquals("http://www.w3.org/2001/XMLSchema-instance", attributes.get("xmlns:xsi"));
    }
    
    @Test
    public void testEnzymes() throws JAXBException{
        SpectrumIdentificationProtocol spectrumIdentificactionProtocol = unmarshaller.unmarshal(SpectrumIdentificationProtocol.class, "SIP");
        Enzymes enzymes = spectrumIdentificactionProtocol.getEnzymes();
        assertEquals("ENZ_0", enzymes.getEnzyme().get(0).getId());
        assertEquals(2, enzymes.getEnzyme().size());
        Enzyme enzyme = enzymes.getEnzyme().get(0);
        assertEquals("ENZ_0", enzyme.getId());
        List<UserParam> enzymeNameUserParams = enzyme.getEnzymeName().getUserParam();
        assertTrue(enzymeNameUserParams.get(0) instanceof EnzymeNameUserParam);
        assertEquals("CNBr+Trypsin", enzymeNameUserParams.get(0).getValue());
        List<CvParam> enzymeNameCvParam = enzyme.getEnzymeName().getCvParam();
        assertEquals(0, enzymeNameCvParam.size());

        enzyme = enzymes.getEnzyme().get(1);
        assertEquals("ENZ_1", enzyme.getId());
        enzymeNameUserParams = enzyme.getEnzymeName().getUserParam();
        assertTrue(enzymeNameUserParams.get(0) instanceof EnzymeNameUserParam);
        assertEquals("CNBr+Trypsin", enzymeNameUserParams.get(0).getValue());
    }
    
    @Test
    public void testEnzymes12() throws JAXBException{
        SpectrumIdentificationProtocol spectrumIdentificactionProtocol = unmarshaller_1_2.unmarshal(SpectrumIdentificationProtocol.class, "SIP");
        Enzymes enzymes = spectrumIdentificactionProtocol.getEnzymes();
        assertEquals("ENZ_0", enzymes.getEnzyme().get(0).getId());
        assertEquals(2, enzymes.getEnzyme().size());
        Enzyme enzyme = enzymes.getEnzyme().get(0);
        assertEquals("ENZ_0", enzyme.getId());
        List<UserParam> enzymeNameUserParams = enzyme.getEnzymeName().getUserParam();
        assertTrue(enzymeNameUserParams.get(0) instanceof EnzymeNameUserParam);
        assertEquals("Trypsin", enzymeNameUserParams.get(0).getValue());
        List<CvParam> enzymeNameCvParam = enzyme.getEnzymeName().getCvParam();
        assertEquals(0, enzymeNameCvParam.size());

        enzyme = enzymes.getEnzyme().get(1);
        assertEquals("ENZ_1", enzyme.getId());
        enzymeNameUserParams = enzyme.getEnzymeName().getUserParam();
        assertTrue(enzymeNameUserParams.get(0) instanceof EnzymeNameUserParam);
        assertEquals("CNBr+Trypsin", enzymeNameUserParams.get(0).getValue());
    }

    /**
     * Unmarshal Inputs and confirm that FileFormat adapter has successfully converted the
     * FileFormat  property into a CvParam in SourceFile
     */
    @Test
    public void testInputsUnmarshalWithFileFormatAdapter() {
        Inputs inputs = unmarshaller.unmarshal(Inputs.class);
        assertEquals("MS:1001199", inputs.getSourceFile().get(0).getFileFormat().getCvParam().getAccession());
    }
    
    @Test
    public void testInputsUnmarshalWithFileFormatAdapter12() {
        Inputs inputs = unmarshaller_1_2.unmarshal(Inputs.class);
        assertEquals("MS:1001199", inputs.getSourceFile().get(0).getFileFormat().getCvParam().getAccession());
        assertEquals("MS:1001348", inputs.getSearchDatabase().get(0).getFileFormat().getCvParam().getAccession());
        assertEquals("file:///dyckall.asc", inputs.getSpectraData().get(0).getLocation());
    }

    /**
     * Unmarshal SpectraData and confirm that SpectrumIDFormat adapter has successfully converted the
     * SpectrumIDFormat property into a list of CvParams in SpectraData.
     */
    @Test
    public void testInputsUnmarshalWithSpectrumIDFormatAdapter() {
        SpectraData spectraData = unmarshaller.unmarshal(SpectraData.class);
        assertEquals("MS:1001528", spectraData.getSpectrumIDFormat().getCvParam().getAccession());
    }
    
    @Test
    public void testInputsUnmarshalWithSpectrumIDFormatAdapter12() {
        SpectraData spectraData = unmarshaller_1_2.unmarshal(SpectraData.class);
        assertEquals("MS:1001528", spectraData.getSpectrumIDFormat().getCvParam().getAccession());
    }

    @Test
    public void testDataCollection() {
        DataCollection dataCollection = unmarshaller.unmarshal(DataCollection.class);
        dataCollection.getInputs();
    }
    
    @Test
    public void testDataCollection12() {
        DataCollection dataCollection = unmarshaller_1_2.unmarshal(DataCollection.class);
        assertEquals("file:///../data/F001350.dat", dataCollection.getInputs().getSourceFile().get(0).getLocation());
    }

    @Test
    public void testOrganization() throws JAXBException {
        Organization organization = unmarshaller.unmarshal(Organization.class, "ORG_MSL");
        assertEquals(4, organization.getParamGroup().size());
        assertEquals("Matrix Science Limited", organization.getName());
        // Test facadelist
        List<CvParam> orgCvParams = organization.getCvParam();
        assertEquals(2, orgCvParams.size());
        assertEquals("MS:1000589", orgCvParams.get(0).getAccession());
        List<UserParam> orgUserParams = organization.getUserParam();
        assertEquals(2, orgUserParams.size());
        assertEquals("contact phone", orgUserParams.get(0).getName());

    }
    
    @Test
    public void testOrganization12() throws JAXBException {
        Organization organization = unmarshaller_1_2.unmarshal(Organization.class, "ORG_MSL");
        assertEquals(4, organization.getParamGroup().size());
        assertEquals("Matrix Science Limited", organization.getName());
        // Test facadelist
        List<CvParam> orgCvParams = organization.getCvParam();
        assertEquals(2, orgCvParams.size());
        assertEquals("MS:1000589", orgCvParams.get(0).getAccession());
        List<UserParam> orgUserParams = organization.getUserParam();
        assertEquals(2, orgUserParams.size());
        assertEquals("contact phone", orgUserParams.get(0).getName());

    }

    @Test
    public void testProvider() throws JAXBException {
        Provider provider = unmarshaller.unmarshal(Provider.class, "PROVIDER");
        assertEquals("MS:1001271", provider.getContactRole().getRole().getCvParam().getAccession());
    }
    
    @Test
    public void testProvider12() throws JAXBException {
        Provider provider = unmarshaller_1_2.unmarshal(Provider.class, "PROVIDER");
        assertEquals("MS:1001271", provider.getContactRole().getRole().getCvParam().getAccession());
    }

    @Test
    public void testAnalysisSampleCollection() {
        AnalysisSampleCollection analysisSampleCollection = unmarshaller.unmarshal(AnalysisSampleCollection.class);
        Sample sample = analysisSampleCollection.getSample().get(0);
        // Test facade list
        List<CvParam> sampleCvParams = sample.getCvParam();
        assertEquals(2, sample.getParamGroup().size());
        assertEquals(1, sampleCvParams.size());
        assertEquals("UO", sampleCvParams.get(0).getUnitCvRef());
        List<UserParam> sampleUserParams = sample.getUserParam();
        assertEquals(1, sampleUserParams.size());
        assertEquals("UO", sampleUserParams.get(0).getUnitCvRef());
    }

    @Test
    public void testAnalysisSampleCollection12() {
        AnalysisSampleCollection analysisSampleCollection = unmarshaller_1_2.unmarshal(AnalysisSampleCollection.class);
        Sample sample = analysisSampleCollection.getSample().get(0);
        // Test facade list
        List<CvParam> sampleCvParams = sample.getCvParam();
        assertEquals(2, sample.getParamGroup().size());
        assertEquals(1, sampleCvParams.size());
        assertEquals("UO", sampleCvParams.get(0).getUnitCvRef());
        List<UserParam> sampleUserParams = sample.getUserParam();
        assertEquals(1, sampleUserParams.size());
        assertEquals("UO", sampleUserParams.get(0).getUnitCvRef());
    }

    @Test
    public void testAuditCollection() throws JAXBException {
        Organization organization = unmarshaller.unmarshal(Organization.class, "ORG_MSL");
        List<CvParam> cvParams = organization.getCvParam();
        List<UserParam> userParams = organization.getUserParam();
        assertEquals(2, cvParams.size());
        assertEquals(2, userParams.size());
        CvParam cvParam = cvParams.get(0);
        assertEquals("MS:1000589", cvParam.getAccession());
        UserParam userParam = userParams.get(0);
        assertEquals("+44 (0)20 7486 1050", userParam.getValue());
    }
    
    @Test
    public void testAuditCollection12() throws JAXBException {
        Organization organization = unmarshaller_1_2.unmarshal(Organization.class, "ORG_MSL");
        List<CvParam> cvParams = organization.getCvParam();
        List<UserParam> userParams = organization.getUserParam();
        assertEquals(2, cvParams.size());
        assertEquals(2, userParams.size());
        CvParam cvParam = cvParams.get(0);
        assertEquals("MS:1000589", cvParam.getAccession());
        UserParam userParam = userParams.get(0);
        assertEquals("+44 (0)20 7486 1050", userParam.getValue());
    }

    @Test
    public void testProteinDetectionProtocol() throws JAXBException{
        ProteinDetectionProtocol proteinDetectionProtocol = unmarshaller.unmarshal(ProteinDetectionProtocol.class, "PDP_MascotParser_1");
        ThresholdCvParam thresholdCvParam = (ThresholdCvParam)proteinDetectionProtocol.getThreshold().getCvParam().get(0);
        assertEquals("MS:1001494", thresholdCvParam.getAccession());
        assertEquals("PSI-MS", thresholdCvParam.getCvRef());
        assertEquals("no threshold", thresholdCvParam.getName());

        List<CvParam> analysisParams = proteinDetectionProtocol.getAnalysisParams().getCvParam();
        assertEquals(10, analysisParams.size());
        AnalysisParamsCvParam analysisParamsCvParam = (AnalysisParamsCvParam)analysisParams.get(0);
        assertEquals("MS:1001316", analysisParamsCvParam.getAccession());
        assertEquals("mascot:SigThreshold", analysisParamsCvParam.getName());
        assertEquals("0.05", analysisParamsCvParam.getValue());

        analysisParamsCvParam = (AnalysisParamsCvParam)analysisParams.get(4);
        assertEquals("MS:1001320", analysisParamsCvParam.getAccession());
        assertEquals("mascot:ShowHomologousProteinsWithSamePeptides", analysisParamsCvParam.getName());
        assertEquals("1", analysisParamsCvParam.getValue());

        analysisParamsCvParam = (AnalysisParamsCvParam)analysisParams.get(9);
        assertEquals("MS:1001325", analysisParamsCvParam.getAccession());
        assertEquals("mascot:ShowDecoyMatches", analysisParamsCvParam.getName());
        assertEquals("0", analysisParamsCvParam.getValue());

    }
    
    @Test
    public void testProteinDetectionProtocol12() throws JAXBException{
        ProteinDetectionProtocol proteinDetectionProtocol = unmarshaller_1_2.unmarshal(ProteinDetectionProtocol.class, "PDP_MascotParser_1");
        ThresholdCvParam thresholdCvParam = (ThresholdCvParam)proteinDetectionProtocol.getThreshold().getCvParam().get(0);
        assertEquals("MS:1001494", thresholdCvParam.getAccession());
        assertEquals("PSI-MS", thresholdCvParam.getCvRef());
        assertEquals("no threshold", thresholdCvParam.getName());

        List<CvParam> analysisParams = proteinDetectionProtocol.getAnalysisParams().getCvParam();
        assertEquals(10, analysisParams.size());
        AnalysisParamsCvParam analysisParamsCvParam = (AnalysisParamsCvParam)analysisParams.get(0);
        assertEquals("MS:1001316", analysisParamsCvParam.getAccession());
        assertEquals("mascot:SigThreshold", analysisParamsCvParam.getName());
        assertEquals("0.05", analysisParamsCvParam.getValue());

        analysisParamsCvParam = (AnalysisParamsCvParam)analysisParams.get(4);
        assertEquals("MS:1001320", analysisParamsCvParam.getAccession());
        assertEquals("mascot:ShowHomologousProteinsWithSamePeptides", analysisParamsCvParam.getName());
        assertEquals("1", analysisParamsCvParam.getValue());

        analysisParamsCvParam = (AnalysisParamsCvParam)analysisParams.get(9);
        assertEquals("MS:1001325", analysisParamsCvParam.getAccession());
        assertEquals("mascot:ShowDecoyMatches", analysisParamsCvParam.getName());
        assertEquals("0", analysisParamsCvParam.getValue());

    }

    @Test
    public void testSpectrumIdentificationProtocol() throws JAXBException{
        SpectrumIdentificationProtocol spectrumIdentificationProtocol = unmarshaller.unmarshal(SpectrumIdentificationProtocol.class, "SIP");
        List<CvParam> additionalSearchCvParams = spectrumIdentificationProtocol.getAdditionalSearchParams().getCvParam();
        assertEquals(10, additionalSearchCvParams.size());
        AdditionalSearchParamsCvParam additionalSearchParamsCvParam = (AdditionalSearchParamsCvParam)additionalSearchCvParams.get(0);
        assertEquals("MS:1001211", additionalSearchParamsCvParam.getAccession());
        assertEquals("parent mass type mono", additionalSearchParamsCvParam.getName());

        List<UserParam> additionalSearchUserParams = spectrumIdentificationProtocol.getAdditionalSearchParams().getUserParam();
        assertEquals(2, additionalSearchUserParams.size());
        AdditionalSearchParamsUserParam additionalSearchParamsUserParam = (AdditionalSearchParamsUserParam) additionalSearchUserParams.get(0);
        assertEquals("Mascot User Comment", additionalSearchParamsUserParam.getName());
        assertEquals("Example Mascot MS-MS search for PSI mzIdentML", additionalSearchParamsUserParam.getValue());

        Param searchType = spectrumIdentificationProtocol.getSearchType();
        assertTrue(searchType.getCvParam() instanceof SearchTypeCvParam);

    }
    
    @Test
    public void testSpectrumIdentificationProtocol12() throws JAXBException{
        SpectrumIdentificationProtocol spectrumIdentificationProtocol = unmarshaller_1_2.unmarshal(SpectrumIdentificationProtocol.class, "SIP");
        List<CvParam> additionalSearchCvParams = spectrumIdentificationProtocol.getAdditionalSearchParams().getCvParam();
        assertEquals(10, additionalSearchCvParams.size());
        AdditionalSearchParamsCvParam additionalSearchParamsCvParam = (AdditionalSearchParamsCvParam)additionalSearchCvParams.get(0);
        assertEquals("MS:1001211", additionalSearchParamsCvParam.getAccession());
        assertEquals("parent mass type mono", additionalSearchParamsCvParam.getName());

        List<UserParam> additionalSearchUserParams = spectrumIdentificationProtocol.getAdditionalSearchParams().getUserParam();
        assertEquals(2, additionalSearchUserParams.size());
        AdditionalSearchParamsUserParam additionalSearchParamsUserParam = (AdditionalSearchParamsUserParam) additionalSearchUserParams.get(0);
        assertEquals("Mascot User Comment", additionalSearchParamsUserParam.getName());
        assertEquals("Example Mascot MS-MS search for PSI mzIdentML", additionalSearchParamsUserParam.getValue());

        Param searchType = spectrumIdentificationProtocol.getSearchType();
        assertTrue(searchType.getCvParam() instanceof SearchTypeCvParam);

    }

    @Test
    public void testAnalysisSearchDatabase() throws JAXBException{
        SearchDatabase searchDb = unmarshaller.unmarshal(SearchDatabase.class, "SDB_SwissProt");
        Param dbName = searchDb.getDatabaseName();
        assertTrue(dbName.getUserParam() instanceof DatabaseNameUserParam);
    }
    
    @Test
    public void testAnalysisSearchDatabase12() throws JAXBException{
        SearchDatabase searchDb = unmarshaller_1_2.unmarshal(SearchDatabase.class, "SDB_SwissProt");
        Param dbName = searchDb.getDatabaseName();
        assertTrue(dbName.getUserParam() instanceof DatabaseNameUserParam);
    }

    @Test
    public void testFilter() throws JAXBException{
        SpectrumIdentificationProtocol spectrumIdentificationProtocol = unmarshaller.unmarshal(SpectrumIdentificationProtocol.class, "SIP");
        DatabaseFilters dbFilters = spectrumIdentificationProtocol.getDatabaseFilters();
        List<Filter> filter = dbFilters.getFilter();
        assertEquals(1, filter.size());
        IncludeCvParam includeCvParam = (IncludeCvParam)filter.get(0).getInclude().getCvParam().get(0);
        assertEquals("MS:1001467", includeCvParam.getAccession());
        assertEquals("33208", includeCvParam.getValue());
        assertTrue(filter.get(0).getFilterType().getCvParam() instanceof FilterTypeCvParam);
        assertEquals("MS:1001020", filter.get(0).getFilterType().getCvParam().getAccession());
    }
    
    @Test
    public void testFilter12() throws JAXBException{
        SpectrumIdentificationProtocol spectrumIdentificationProtocol = unmarshaller_1_2.unmarshal(SpectrumIdentificationProtocol.class, "SIP");
        DatabaseFilters dbFilters = spectrumIdentificationProtocol.getDatabaseFilters();
        List<Filter> filter = dbFilters.getFilter();
        assertEquals(1, filter.size());
        IncludeCvParam includeCvParam = (IncludeCvParam)filter.get(0).getInclude().getCvParam().get(0);
        assertEquals("MS:1001467", includeCvParam.getAccession());
        assertEquals("33208", includeCvParam.getValue());
        assertTrue(filter.get(0).getFilterType().getCvParam() instanceof FilterTypeCvParam);
        assertEquals("MS:1001020", filter.get(0).getFilterType().getCvParam().getAccession());
    }

    @Test
    public void testAnalysisSoftware() throws JAXBException{
        AnalysisSoftware analysisSoftware = unmarshaller.unmarshal(AnalysisSoftware.class, "AS_mascot_parser");
        Param param = analysisSoftware.getSoftwareName();
        assertTrue(param.getCvParam() instanceof SoftwareNameCvParam);
    }
    
    @Test
    public void testAnalysisSoftware12() throws JAXBException{
        AnalysisSoftware analysisSoftware = unmarshaller_1_2.unmarshal(AnalysisSoftware.class, "mascot_parser");
        Param param = analysisSoftware.getSoftwareName();
        assertTrue(param.getCvParam() instanceof SoftwareNameCvParam);
    }

    @Test
    public void testIonType() throws JAXBException {
        SpectrumIdentificationList list =  unmarshaller.unmarshal(SpectrumIdentificationList.class, "SIL_1");
        List<IonType> ionTypes = list.getSpectrumIdentificationResult().get(0).getSpectrumIdentificationItem().get(0).getFragmentation().getIonType();
        assertTrue(ionTypes.size() > 0);
        IonType ionType = ionTypes.get(0);
        List<Integer> index = ionType.getIndex();
        assertEquals(5, index.size());
        assertEquals(1, (int) index.get(0));
        assertEquals(2, (int) index.get(1));
        assertEquals(8, (int) index.get(4));
        /**
         * Check if instances of Integer are returned. By default jaxb converts int in xml to BigInts in Java.
         */
        assertTrue(index.get(0) instanceof Integer);
    }
    
    @Test
    public void testIonType12() throws JAXBException {
        SpectrumIdentificationList list =  unmarshaller_1_2.unmarshal(SpectrumIdentificationList.class, "SIL_1");
        List<IonType> ionTypes = list.getSpectrumIdentificationResult().get(0).getSpectrumIdentificationItem().get(0).getFragmentation().getIonType();
        assertTrue(ionTypes.size() > 0);
        IonType ionType = ionTypes.get(0);
        List<Integer> index = ionType.getIndex();
        assertEquals(5, index.size());
        assertEquals(1, (int) index.get(0));
        assertEquals(2, (int) index.get(1));
        assertEquals(8, (int) index.get(4));
        /**
         * Check if instances of Integer are returned. By default jaxb converts int in xml to BigInts in Java.
         */
        assertTrue(index.get(0) instanceof Integer);
    }

    @Test
    public void testMassTable() throws JAXBException {
        SpectrumIdentificationProtocol spectrumIdentificationProtocol = unmarshaller.unmarshal(SpectrumIdentificationProtocol.class, "SIP");
        List<MassTable> massTables = spectrumIdentificationProtocol.getMassTable();
        assertTrue(massTables.size() > 0);
        MassTable massTable = massTables.get(0);
        assertEquals("MT", massTable.getId());
        List<Integer> msLevels = massTable.getMsLevel();
        assertEquals(2, msLevels.size());
        /**
         * Check if instances of Integer are returned. By default jaxb converts int in xml to BigInts in Java.
         */
        assertSame(msLevels.get(0).getClass(), Integer.class);
    }
    
    @Test
    public void testMassTable12() throws JAXBException {
        SpectrumIdentificationProtocol spectrumIdentificationProtocol = unmarshaller_1_2.unmarshal(SpectrumIdentificationProtocol.class, "SIP");
        List<MassTable> massTables = spectrumIdentificationProtocol.getMassTable();
        assertTrue(massTables.size() > 0);
        MassTable massTable = massTables.get(0);
        assertEquals("MT", massTable.getId());
        List<Integer> msLevels = massTable.getMsLevel();
        assertEquals(2, msLevels.size());
        /**
         * Check if instances of Integer are returned. By default jaxb converts int in xml to BigInts in Java.
         */
        assertSame(msLevels.get(0).getClass(), Integer.class);
    }

    @Test
    public void testPeptideEvidence(){
        MzIdentML mzIdentML = unmarshaller.unmarshal(MzIdentML.class);
        List<PeptideEvidence> peptideEvidences =
                mzIdentML.getSequenceCollection().getPeptideEvidence();
        assertEquals(56, peptideEvidences.size());
        PeptideEvidence pe = peptideEvidences.get(0);
        assertEquals("PE_1_1_HSP70_ECHGR_0", pe.getId());

        if (MzIdentMLElement.PeptideEvidence.isAutoRefResolving() && pe.getDBSequenceRef() != null) {
            DBSequence dbSeq = pe.getDBSequence();
            assertEquals("HSP70_ECHGR", dbSeq.getAccession());
        }else{
             System.out.println("PeptideEvidence is not auto-resolving.");
        }
    }
    
    @Test
    public void testPeptideEvidence12(){
        MzIdentML mzIdentML = unmarshaller_1_2.unmarshal(MzIdentML.class);
        List<PeptideEvidence> peptideEvidences =
                mzIdentML.getSequenceCollection().getPeptideEvidence();
        assertEquals(56, peptideEvidences.size());
        PeptideEvidence pe = peptideEvidences.get(0);
        assertEquals("PE_1_1_HSP70_ECHGR_0", pe.getId());

        if (MzIdentMLElement.PeptideEvidence.isAutoRefResolving() && pe.getDBSequenceRef() != null) {
            DBSequence dbSeq = pe.getDBSequence();
            assertEquals("HSP70_ECHGR", dbSeq.getAccession());
        }else{
             System.out.println("PeptideEvidence is not auto-resolving.");
        }
    }


    @Test
    public void testIDForElement() throws ConfigurationException {
        int numOfPE = unmarshaller.getIDsForElement(MzIdentMLElement.PeptideEvidence).size();
        assertEquals("number of PeptideEvidence not as expected.", 56, numOfPE);
    }
    
    @Test
    public void testIDForElement12() throws ConfigurationException {
        int numOfPE = unmarshaller_1_2.getIDsForElement(MzIdentMLElement.PeptideEvidence).size();
        assertEquals("number of PeptideEvidence not as expected.", 56, numOfPE);
    }
}
package uk.ac.ebi.jmzidml.xml.xxindex;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.ConfigurationException;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import psidev.psi.tools.xxindex.FastXmlElementExtractor;
import psidev.psi.tools.xxindex.StandardXpathAccess;
import psidev.psi.tools.xxindex.XpathAccess;
import psidev.psi.tools.xxindex.index.IndexElement;
import psidev.psi.tools.xxindex.index.XpathIndex;
import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.MzIdentMLObject;
import uk.ac.ebi.jmzidml.model.mzidml.Identifiable;
import uk.ac.ebi.jmzidml.xml.Constants;


public class MzIdentMLIndexerFactory {

    private static final Logger logger = LoggerFactory.getLogger(MzIdentMLIndexerFactory.class);

    private static final MzIdentMLIndexerFactory instance = new MzIdentMLIndexerFactory();
    private static final Pattern ID_PATTERN = Pattern.compile("\\sid\\s*=\\s*['\"]([^'\"]*)['\"]", Pattern.CASE_INSENSITIVE);

    private MzIdentMLIndexerFactory() {
    }

    public static MzIdentMLIndexerFactory getInstance() {
        return instance;
    }

    public MzIdentMLIndexer buildIndex(File xmlFile) {
        return buildIndex(xmlFile, Constants.XML_INDEXED_XPATHS, false);
    }

    public MzIdentMLIndexer buildIndex(File xmlFile, Set<String> xpaths) {
        return new MzIdentMLIndexerImpl(xmlFile, xpaths, false);
    }

    public MzIdentMLIndexer buildIndex(File xmlFile, boolean inMeomory) {
        return buildIndex(xmlFile, Constants.XML_INDEXED_XPATHS, inMeomory);
    }

    public MzIdentMLIndexer buildIndex(File xmlFile, Set<String> xpaths, boolean inMemory) {
        return new MzIdentMLIndexerImpl(xmlFile, xpaths, inMemory);
    }

    private class MzIdentMLIndexerImpl implements MzIdentMLIndexer {


        private File xmlFile = null;
        private boolean inMemory = false;
        private byte[] xmlFileBuffer;
        private XpathAccess xpathAccess = null;
        private FastXmlElementExtractor xmlExtractor = null;
        private MemoryMappedXmlElementExtractor memoryMappedXmlElementExtractor;
        private XpathIndex index = null;
        private String mzIdentMLAttributeXMLString = null;
        // a unified cache of all the id maps
        private Map<Class, Map<String, IndexElement>> idMapCache = new HashMap<>();

        ///// ///// ///// ///// ///// ///// ///// ///// ///// /////
        // Constructor

        private MzIdentMLIndexerImpl(File xmlFile, Set<String> xpaths, boolean inMemory) {

            if (xmlFile == null) {
                throw new IllegalStateException("XML File to index must not be null");
            }
            if (!xmlFile.exists()) {
                throw new IllegalStateException("XML File to index does not exist: " + xmlFile.getAbsolutePath());
            }

            //store file reference
            this.xmlFile = xmlFile;
            this.inMemory = inMemory;

            try {
                // create xml element extractor
                initXpathAccess(xmlFile, xpaths, inMemory);

                // create index
                index = xpathAccess.getIndex();

                // check if the xxindex contains this root
                if (!index.containsXpath(MzIdentMLElement.MzIdentML.getXpath())) {
                    logger.info("Index does not contain mzIdentML root! We are not dealing with an mzIndentML file!");
                    throw new IllegalStateException("Index does not contain mzIdentML root!");
                }

                // initialize the ID maps
                initIdMaps();

                // extract the MzIdentML attributes from the MzIdentML start tag
                mzIdentMLAttributeXMLString = extractMzIdentMLStartTag(xmlFile);

            } catch (IOException e) {
                logger.error("MzMLIndexerFactory$MzMlIndexerImpl.MzMlIndexerImpl", e);
                throw new IllegalStateException("Could not generate MzIdentML index for file: " + xmlFile);
            }

        }

        private void initXpathAccess(File xmlFile, Set<String> xpaths, boolean inMemory) throws IOException {
            if (inMemory) {
                // load file into memory
                loadFileIntoMemory(xmlFile);

                MemoryMappedStandardXpathAccess memoryMappedStandardXpathAccess = new MemoryMappedStandardXpathAccess(xmlFileBuffer, xpaths);
                memoryMappedXmlElementExtractor = memoryMappedStandardXpathAccess.getExtractor();
                xpathAccess = memoryMappedStandardXpathAccess;
            } else {
                xpathAccess = new StandardXpathAccess(xmlFile, xpaths);
                xmlExtractor = new FastXmlElementExtractor(xmlFile);
                xmlExtractor.setEncoding(xmlExtractor.detectFileEncoding(xmlFile.toURI().toURL()));
            }
        }

        private void loadFileIntoMemory(File xmlFile) throws IOException {
            FileInputStream fis = new FileInputStream(xmlFile);
            FileChannel fc = fis.getChannel();

            MappedByteBuffer mmb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

            xmlFileBuffer = new byte[(int)fc.size()];
            mmb.get(xmlFileBuffer);

            fis.close();
        }

        ///// ///// ///// ///// ///// ///// ///// ///// ///// /////
        // public methods

        public String getMzIdentMLAttributeXMLString() {
            return mzIdentMLAttributeXMLString;
        }

        public Iterator<String> getXmlStringIterator(String xpathExpression) {
            if ( index.containsXpath(xpathExpression) ) {
                return xpathAccess.getXmlSnippetIterator(xpathExpression);
            } else {
                return null;
            }
        }

        /**
         *
         * @param xpathExpression the xpath defining the XML element.
         * @return the number of XML elements matching the xpath or -1
         *         if no elements were found for the specified xpath.
         */
        public int getCount(String xpathExpression) {
            int retValue = -1;
            if (index.containsXpath(xpathExpression)) {
                List<IndexElement> tmpList = index.getElements(xpathExpression);
                if (tmpList != null) {
                    retValue = tmpList.size();
                }
            }
            return retValue;
        }

        public List<IndexElement> getIndexElements(String xpath) {
            return new ArrayList<>(index.getElements(xpath));
        }

        public Map<String, IndexElement> getIndexElements(Class clazz) {
            return new HashMap<>(idMapCache.get(clazz));
        }

        public Set<String> getXpath() {
            return new HashSet<>(index.getKeys());
        }

        // ToDo: maybe generify to <T extends IdentifiableMzIdentMLObject>  Class<T>  ??
        public String getXmlString(String ID, Class clazz) {
            logger.debug("Getting cached ID: " + ID + " from cache: " + clazz);

            Map<String, IndexElement> idMap = idMapCache.get(clazz);
            IndexElement element = idMap.get(ID);

            String xmlSnippet = null;
            if (element != null) {
                xmlSnippet = getXmlString(element);
                if (logger.isTraceEnabled()) {
                    logger.trace("Retrieved xml for class " + clazz + " with ID " + ID + ": " + xmlSnippet);
                }
            }
            return xmlSnippet;
        }

        public String getStartTag(String id, Class clazz) {
            logger.debug("Getting start tag of element with id: " + id + " for class: " + clazz);
            String tag = null;

            Map<String, IndexElement> idMap = idMapCache.get(clazz);
            if (idMap != null) {
                IndexElement element = idMap.get(id);
                if (element != null) {
                    try {
                        if (xpathAccess instanceof StandardXpathAccess) {
                            tag = ((StandardXpathAccess)xpathAccess).getStartTag(element);
                        } else if (xpathAccess instanceof MemoryMappedStandardXpathAccess) {
                            tag = ((MemoryMappedStandardXpathAccess)xpathAccess).getStartTag(element);
                        }
                    } catch (IOException e) {
                        // ToDo: proper handling
                        e.printStackTrace();
                    }
                } else {
                    // ToDo: what if the element exists, but its id was not cached?
                    // ToDo: throw an exception?
                }
            }
            return tag;
        }

        public boolean isIDmapped(String id, Class clazz) {
            if (clazz == null) {
                return false;
            }
            Map<String, IndexElement> idMap = idMapCache.get(clazz);
            return idMap != null && idMap.containsKey(id);
        }

        /**
         * Is dependent on the element being indexed and ID mapped.
         * See configuration of elements (MzIdentMLElement).
         *
         * @param element the element for which to get the IDs.
         * @return  a Set of all IDs of the specified element.
         * @throws ConfigurationException
         */
        public Set<String> getIDsForElement(MzIdentMLElement element) throws ConfigurationException {
            if ( element.isIdMapped() ) {
                return idMapCache.get(element.<MzIdentMLObject>getClazz()).keySet();
            } else {
                throw new ConfigurationException("API not configured to support ID mapping for element: " + element.getTagName());
            }
        }

        public <T extends MzIdentMLObject> Set<String> getElementIDs(Class<T> clazz) {
            if (idMapCache == null) { return null; }
            Map<String, IndexElement> classCache = idMapCache.get(clazz);
            if (classCache == null) { return null; }
            return classCache.keySet();
        }

        public String getXmlString(IndexElement byteRange) {
            return getXmlString(byteRange, 0);
        }

        ///// ///// ///// ///// ///// ///// ///// ///// ///// /////
        // private methods
        private String getXmlString(IndexElement byteRange, int maxChars) {
            try {
                if (byteRange != null) {
                    long stop; // where we will stop reading
                    long limitedStop = byteRange.getStart() + maxChars; // the potential end-point of reading
                    // if a limit was specified and the XML element length is longer
                    // than the limit, we only read up to the provided limit
                    if (maxChars > 0 && byteRange.getStop() > limitedStop) {
                        stop = limitedStop;
                    } else { // otherwise we will read up to the end of the XML element
                        stop = byteRange.getStop();
                    }
                    return inMemory ? memoryMappedXmlElementExtractor.readString(byteRange.getStart(), stop, new ByteArrayInputStream(xmlFileBuffer)) : xmlExtractor.readString(byteRange.getStart(), stop, xmlFile);
                } else {
                    throw new IllegalStateException("Attempting to read NULL ByteRange");
                }
            } catch (IOException e) {
                logger.error("MzMLIndexerFactory$MzMlIndexerImpl.readXML", e);
                throw new IllegalStateException("Could not extract XML from file: " + xmlFile);
            }
        }

        private String extractMzIdentMLStartTag(File xmlFile) throws IOException {
            // get start position of the mzIdentML element
            List<IndexElement> ie = index.getElements(MzIdentMLElement.MzIdentML.getXpath());
            // there is only one root
            long startPos = ie.get(0).getStart();

            // get end position of the mzIdentML start tag
            // this is the start position of the next tag (cvList)
            ie = index.getElements(MzIdentMLElement.CvList.getXpath());
            // there will always be one and only one cvList
            long stopPos = ie.get(0).getStart() - 1;

            // get mzML start tag content
            String startTag = inMemory ? memoryMappedXmlElementExtractor.readString(startPos, stopPos, new ByteArrayInputStream(xmlFileBuffer)) : xmlExtractor.readString(startPos, stopPos, xmlFile);
            if (startTag != null) {
                //strip newlines that might interfere with later on regex matching
                startTag = startTag.replace("\n", "");
            }
            return startTag;
        }

        /**
         * Method to generate and populate ID maps for the XML elements that should be
         * mapped to a unique ID. This will require that these elements are indexes and
         * that they extend the Identifiable class to make sure they have a unique ID.
         *
         * @see uk.ac.ebi.jmzidml.MzIdentMLElement
         * @throws IOException in case of a read error from the underlying XML file.
         */
        private void initIdMaps() throws IOException {
            for (MzIdentMLElement element : MzIdentMLElement.values()) {
                // only for elements were a ID map is needed and a xpath is given
                if (element.isIdMapped() && element.isIndexed()) {
                    if (element.getClass().isAssignableFrom(Identifiable.class))  {
                        logger.warn("Element for class " + element.getClass() + " may not contain an 'id' attribute, but was selected for id mapping!" );
                    }
                    logger.debug("Initialising ID map for " + element.getClazz().getName());
                    // check if the according class is a sub-class of Identifiable
//                    if (!IdentifiableMzIdentMLObject.class.isAssignableFrom(element.getClazz())) {
//                        throw new IllegalStateException("Attempt to create ID map for not Identifiable element: " + element.getClazz());
//                    }
                    // so far so good, now generate the ID map (if not already present) and populate it
                    Map<String, IndexElement> map = idMapCache.get(element.getClazz());
                    if (map == null) {
                        map = new HashMap<>(index.getElements(element.getXpath()).size());
                        idMapCache.put(element.getClazz(), map);
                    }else{
                        logger.debug("This IdElement Map already exists");
                    }
                    initIdMapCache(map, element.getXpath());
                }
            }
        }

        private void initIdMapCache(Map<String, IndexElement> idMap, String xpath) throws IOException {
            List<IndexElement> ranges = index.getElements(xpath);
            for (IndexElement byteRange : ranges) {
                String xml = null;
                if (xpathAccess instanceof StandardXpathAccess) {
                    xml = ((StandardXpathAccess)xpathAccess).getStartTag(byteRange);
                } else if (xpathAccess instanceof MemoryMappedStandardXpathAccess) {
                    xml = ((MemoryMappedStandardXpathAccess)xpathAccess).getStartTag(byteRange);
                }
                String id = getIdFromRawXML(xml);
                if (id != null) {
                    idMap.put(id, byteRange);
                } else {
                    throw new IllegalStateException("Error initializing ID cache: No id attribute found for element " + xml);
                }
            }
        }

        /**
         * Validate and Extract Id from the XML elements
         * @param xml MzIdentML type XML section
         * @return String value of the ID attribute
         */
        private String getIdFromRawXML(String xml) {
            String id = null;

            if(xml != null && xml != ""){
                Matcher match = ID_PATTERN.matcher(xml);

                // ToDo: more checks: if no id found or more than one match, ...
                if (match.find()) {
                    id = match.group(1).intern();
                } else {
                    throw new IllegalStateException("Invalid ID in xml: " + xml);
                }
            }
            return id;
        }
    }
}

/*
 * Date: 22/7/2008
 * Author: rcote
 * File: uk.ac.ebi.jmzml.xml.jaxb.unmarshaller.UnmarshallerFactory
 *
 * jmzml is Copyright 2008 The European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 *
 */

/**
 * User: rcote
 * Date: 13-Jun-2008
 * Time: 12:08:25
 * $Id: $
 */
package uk.ac.ebi.jmzidml.xml.jaxb.unmarshaller;

import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import uk.ac.ebi.jmzidml.model.utils.ModelConstants;
import uk.ac.ebi.jmzidml.xml.io.MzIdentMLObjectCache;
import uk.ac.ebi.jmzidml.xml.jaxb.unmarshaller.filters.MzIdentMLNamespaceFilter;
import uk.ac.ebi.jmzidml.xml.jaxb.unmarshaller.listeners.RawXMLListener;
import uk.ac.ebi.jmzidml.xml.xxindex.MzIdentMLIndexer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class UnmarshallerFactory {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UnmarshallerFactory.class);

    private static UnmarshallerFactory instance = new UnmarshallerFactory();
    private static JAXBContext jc = null;

    public static UnmarshallerFactory getInstance() {
        return instance;
    }

    private UnmarshallerFactory() {
    }

    public Unmarshaller initializeUnmarshaller(MzIdentMLIndexer index, MzIdentMLObjectCache cache, MzIdentMLNamespaceFilter xmlFilter) {

        try {
            // Lazy caching of the JAXB Context.
            if (jc == null) {
                jc = JAXBContext.newInstance(ModelConstants.PACKAGE);
            }

            //create unmarshaller
            Unmarshaller unmarshaller = jc.createUnmarshaller();

            /*
               Sometimes it's convenient to maintain application states inside adapters; for example, if you have
               an adapter that converts string on XML into a java.lang.Class object, you might want to have a
                ClassLoader in an adapter.

               In JAXB, this is done by allowing applications to set configured instances of XmlAdapters
               to the unmarshaller/marshaller. This is also an opportunity to pass in a sub-class of the
               declared adapter, if you so wish.

               If the application doesn't provide a configured instance, JAXB will create
               one by calling the default constructor.
                */

            //it is not possible to concurrently reuse a common unmarshaller
            //across all the Adapters because of internal state conflicts
            //when trying to unmarshall a referenced object from within a
            //parent object.

            //we don't have a validation handler yet
//            unmarshaller.setEventHandler(new DefaultValidationEventHandler());

            // This is used to post-process unmarshalled Java objects and convert
            // ParamAlternative classes to appropriate sub-classes (CvParam or UserParam).
            unmarshaller.setListener(new RawXMLListener(index, cache, xmlFilter.getMzIdentMLVersion()));

            UnmarshallerHandler uh = unmarshaller.getUnmarshallerHandler();

            //Create a new XML parser SAX Woodstox
//            WstxInputFactory inputFactory = new WstxInputFactory();
//            inputFactory.configureForSpeed();
//            SAXParserFactory factory = new WstxSAXParserFactory(inputFactory);

           // SAXParserFactory factory = new WstxSAXParserFactory();

            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlReader = parser.getXMLReader();

            // Create a filter to intercept events -- and patch the missing namespace
            xmlFilter.setParent(xmlReader);
            xmlFilter.setContentHandler(uh);

            logger.debug("Unmarshaller Initialized");

            return unmarshaller;

        } catch (JAXBException | ParserConfigurationException | SAXException e) {
            logger.error("UnmarshallerFactory.initializeUnmarshaller", e);
            throw new IllegalStateException("Could not initialize unmarshaller");
        }

    }

}

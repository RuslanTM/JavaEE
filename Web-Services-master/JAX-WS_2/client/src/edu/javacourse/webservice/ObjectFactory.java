
package edu.javacourse.webservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the edu.javacourse.webservice package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetRegionByName_QNAME = new QName("http://webservice.javacourse.edu/", "getRegionByName");
    private final static QName _Region_QNAME = new QName("http://webservice.javacourse.edu/", "region");
    private final static QName _GetAllRegions_QNAME = new QName("http://webservice.javacourse.edu/", "getAllRegions");
    private final static QName _GetAllRegionsResponse_QNAME = new QName("http://webservice.javacourse.edu/", "getAllRegionsResponse");
    private final static QName _GetRegionByNameResponse_QNAME = new QName("http://webservice.javacourse.edu/", "getRegionByNameResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: edu.javacourse.webservice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Region }
     * 
     */
    public Region createRegion() {
        return new Region();
    }

    /**
     * Create an instance of {@link GetRegionByName }
     * 
     */
    public GetRegionByName createGetRegionByName() {
        return new GetRegionByName();
    }

    /**
     * Create an instance of {@link GetAllRegions }
     * 
     */
    public GetAllRegions createGetAllRegions() {
        return new GetAllRegions();
    }

    /**
     * Create an instance of {@link GetAllRegionsResponse }
     * 
     */
    public GetAllRegionsResponse createGetAllRegionsResponse() {
        return new GetAllRegionsResponse();
    }

    /**
     * Create an instance of {@link GetRegionByNameResponse }
     * 
     */
    public GetRegionByNameResponse createGetRegionByNameResponse() {
        return new GetRegionByNameResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRegionByName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.javacourse.edu/", name = "getRegionByName")
    public JAXBElement<GetRegionByName> createGetRegionByName(GetRegionByName value) {
        return new JAXBElement<GetRegionByName>(_GetRegionByName_QNAME, GetRegionByName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Region }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.javacourse.edu/", name = "region")
    public JAXBElement<Region> createRegion(Region value) {
        return new JAXBElement<Region>(_Region_QNAME, Region.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllRegions }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.javacourse.edu/", name = "getAllRegions")
    public JAXBElement<GetAllRegions> createGetAllRegions(GetAllRegions value) {
        return new JAXBElement<GetAllRegions>(_GetAllRegions_QNAME, GetAllRegions.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllRegionsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.javacourse.edu/", name = "getAllRegionsResponse")
    public JAXBElement<GetAllRegionsResponse> createGetAllRegionsResponse(GetAllRegionsResponse value) {
        return new JAXBElement<GetAllRegionsResponse>(_GetAllRegionsResponse_QNAME, GetAllRegionsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRegionByNameResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.javacourse.edu/", name = "getRegionByNameResponse")
    public JAXBElement<GetRegionByNameResponse> createGetRegionByNameResponse(GetRegionByNameResponse value) {
        return new JAXBElement<GetRegionByNameResponse>(_GetRegionByNameResponse_QNAME, GetRegionByNameResponse.class, null, value);
    }

}

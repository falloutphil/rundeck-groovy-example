import com.dtolabs.rundeck.plugins.logs.ContentConverterPlugin
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import javax.xml.transform.dom.DOMSource
import org.xml.sax.InputSource

rundeckPlugin(ContentConverterPlugin) {
        title = "XML Visualization"
        description = "Convert XML to HTML Output"
        version = "1.1"
        url = "http://rundeck.com"
        author = "Phil Beadling, © 2018"


    convert('application/xml'){

        def xsltReader = new StringReader("""<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output omit-xml-declaration="yes" indent="yes"/>
  <xsl:strip-space elements="*"/>

  <xsl:template match="/">
    <table>
      <xsl:apply-templates/>
    </table>
  </xsl:template>

  <xsl:template match="*">
    <tr>
      <td style="background-color: #aaa; white-space: normal; border-collapse: separate; border-spacing: 10px; padding: 4px;">
        <xsl:value-of select="name()"/>
      </td>
      <td style="background-color: #ccc; white-space: normal; border-collapse: separate; border-spacing: 10px; padding: 4px;">
        <xsl:value-of select="."/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="*[*]">
    <tr>
      <td style="border:2px solid #c55; font-size:120%; white-space: normal; border-collapse: separate; border-spacing: 10px; padding: 4px;">
        <xsl:value-of select="name()"/>
      </td>
      <td style="white-space: normal; border-collapse: separate; border-spacing: 10px; padding: 4px;">
        <table>
          <xsl:apply-templates/>
        </table>
      </td>
    </tr>
  </xsl:template>

</xsl:stylesheet>""")

        // Stop the loading of DTD files.
        // Almost all the code below is to disable DTD validation
        // and then strip our the DTD before running the transform.
        // It complicates what was a trivial peice of code as we must
        // create a a sepcially configurated factory to create an
        // instance of the XML whilst ignoring validation.
        def factory = DocumentBuilderFactory.newInstance()
        factory.setValidating(false)
        factory.setNamespaceAware(true)
        factory.setFeature("http://xml.org/sax/features/namespaces", false)
        factory.setFeature("http://xml.org/sax/features/validation", false)
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false)
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
        // Open up the xml document
        def docbuilder = factory.newDocumentBuilder()
        def inputSource = new InputSource(new StringReader(data))
        def doc = docbuilder.parse(inputSource)

        def tFactory = TransformerFactory.newInstance()
        def transformer = tFactory.newTransformer(new StreamSource(xsltReader))
        def writer = new StringWriter()

        // The getDocumentElement will return XML without DTD
        // This will prevent failure in the event the DTD is not retrievable
        // For display purposes we don't overly care about the DTD
        transformer.transform(new DOMSource(doc.getDocumentElement()), new StreamResult(writer))

        writer.toString()
    }
}

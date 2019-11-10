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
        version = "1.2"
        url = "http://rundeck.com"
        author = "Phil Beadling, © 2019"


    convert('application/xml'){

        def xsltReader = new StringReader("""<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output omit-xml-declaration="yes" indent="yes"/>
  <xsl:strip-space elements="*"/>

  <xsl:template match="/">
        <style>
            .datagrid table { border-collapse: collapse; text-align: left; width: 100%; } .datagrid {font: normal 12px/150% Arial, Helvetica, sans-serif; background: #fff; overflow: hidden; border: 1px solid #006699; -webkit-border-radius: 3px; -moz-border-radius: 3px; border-radius: 3px; }.datagrid table td, .datagrid table th { padding: 3px 10px; }.datagrid table thead th {background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #006699), color-stop(1, #00557F) );background:-moz-linear-gradient( center top, #006699 5%, #00557F 100% );filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#006699', endColorstr='#00557F');background-color:#006699; color:#FFFFFF; font-size: 15px; font-weight: bold; border-left: 1px solid #0070A8; } .datagrid table thead th:first-child { border: none; }.datagrid table tbody td { color: #00496B; border-left: 1px solid #E1EEF4;font-size: 12px;font-weight: normal; }.datagrid table tbody .alt td { background: #E1EEF4; color: #00496B; }.datagrid table tbody td:first-child { border-left: none; }.datagrid table tbody tr:last-child td { border-bottom: none; }.datagrid table tfoot td div { border-top: 1px solid #006699;background: #E1EEF4;} .datagrid table tfoot td { padding: 0; font-size: 12px } .datagrid table tfoot td div{ padding: 2px; }.datagrid table tfoot td ul { margin: 0; padding:0; list-style: none; text-align: right; }.datagrid table tfoot  li { display: inline; }.datagrid table tfoot li a { text-decoration: none; display: inline-block;  padding: 2px 8px; margin: 1px;color: #FFFFFF;border: 1px solid #006699;-webkit-border-radius: 3px; -moz-border-radius: 3px; border-radius: 3px; background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #006699), color-stop(1, #00557F) );background:-moz-linear-gradient( center top, #006699 5%, #00557F 100% );filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#006699', endColorstr='#00557F');background-color:#006699; }.datagrid table tfoot ul.active, .datagrid table tfoot ul a:hover { text-decoration: none;border-color: #006699; color: #FFFFFF; background: none; background-color:#00557F;}div.dhtmlx_window_active, div.dhx_modal_cover_dv { position: fixed !important; }
        </style>
        <div class="datagrid"><table>
            <xsl:apply-templates/>
        </table></div>
  </xsl:template>

  <xsl:template match="*">
    <tr>
      <td style="background-color: #aaa;">
        <p><xsl:value-of select="name()"/></p>
      </td>
      <td style="background-color: #ccc;">
        <p><xsl:value-of select="."/></p>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="*[*]">
    <tr>
      <td style="border:2px solid #c55; font-size:120%;">
        <p><xsl:value-of select="name()"/></p>
      </td>
      <td style="">
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

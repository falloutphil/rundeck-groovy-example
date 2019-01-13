import com.dtolabs.rundeck.plugins.logs.ContentConverterPlugin
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

rundeckPlugin(ContentConverterPlugin) {
        title = "XML Visualization"
        description = "Convert XML to HTML Output"
        version = "1.0"
        url = "https://github.com/falloutphil/rundeck-groovy-example"
        author = "Phil Beadling, © 2018"


    convert('application/xml'){

        def xsltReader = new StringReader("""<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output omit-xml-declaration="yes" indent="yes"/>
  <xsl:strip-space elements="*"/>

  <xsl:template match="/">
    <style>
      body {font-family: sans-serif;}
      td {padding: 4px;}
    </style>
    <table>
      <xsl:apply-templates/>
    </table>
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

        def tFactory = TransformerFactory.newInstance()
        def transformer = tFactory.newTransformer(new StreamSource(xsltReader))
        def writer = new StringWriter()

        transformer.transform(new StreamSource(new StringReader(data)), new StreamResult(writer))

        writer.toString()
    }
}

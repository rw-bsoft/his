<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:variable name="nodeSet" select="DataSet/Data/node()[@Name='Male' or @Name='Female']"/>

  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml" >
      <head>
        <title>people</title>
      </head>
      <body style="text-align:center;">
        <div style="text-align:center;margin:auto;padding:auto;">
          <div style="padding:10px 10px 10px 10px;">
            <!--div style="font-weight:bold;height:40px;">
              <xsl:value-of select="DataSet/Other/Caption"/>
            </div-->
            <table border="0" cellpadding="0" cellspacing="0" >

              <tr>
                <td style="text-align:right;height:30px;padding-right:30px;width:48%;" valign="top">

                  <span style="width:8px;height:2px;overflow:hidden; background-color: #00FF00;margin-bottom:6px;">&#160;</span><b>男</b>
                </td>
                <td valign="top" style="text-align:center;width:4%;white-space:nowrap;">
                  	<b>年龄段</b>
                </td>
                <td style="text-align:left;padding-left:30px;width:48%;" valign="top">

                  <span style="width:8px;height:2px;overflow:hidden; background-color: #FF0000;margin-bottom:6px;">&#160;</span><b>女</b>
                </td>
              </tr>
              <xsl:variable name="maxVal">
                <xsl:call-template name="maxFunc">

                  <xsl:with-param name="posVal">1</xsl:with-param>

                  <xsl:with-param name="endPosVal" select="count($nodeSet)"/>

                  <xsl:with-param name="result">0</xsl:with-param>
                </xsl:call-template>
              </xsl:variable>

              <xsl:for-each select="//DataSet/Data[count(node())>0]">
                <xsl:sort select="Item[@Name='ID']" order="descending"/>
                <xsl:variable name="male_divWidthVal" select="round((Item[@Name='Male'] div $maxVal)*70)"/>
                <xsl:variable name="female_divWidthVal" select="round((Item[@Name='Female'] div $maxVal)*70)"/>
                <tr>
                  
                  <td style="text-align:right;white-space:nowrap;">

                    <xsl:choose>
                      <xsl:when test="$male_divWidthVal>=1">
                        <div style="width:{$male_divWidthVal}%; background-color: #00FF00;float:right;filter:progid:DXImageTransform.Microsoft.Shadow(color=#909090,direction=240,strength=4);-moz-box-shadow: 2px 2px 10px #909090;-webkit-box-shadow: 2px 2px 10px #909090;box-shadow:2px 2px 10px #909090;">&#160;</div>
                      </xsl:when>
                      <xsl:when test="$male_divWidthVal>0 and $male_divWidthVal&lt;1">
                        <div style="width:1px; background-color: #00FF00;float:right;filter:progid:DXImageTransform.Microsoft.Shadow(color=#909090,direction=120,strength=4);-moz-box-shadow: 2px 2px 10px #909090;-webkit-box-shadow: 2px 2px 10px #909090;box-shadow:2px 2px 10px #909090;">&#160;</div>
                      </xsl:when>
                      <xsl:otherwise>
                        <div style="width:1px;float:right;">&#160;</div>
                      </xsl:otherwise>
                    </xsl:choose>
                      <xsl:value-of select="Item[@Name='Male']"/>&#160;
                  </td>
                  <td style="padding-right:2px;padding-left:2px;text-align:center;white-space:nowrap;">
                    <xsl:value-of select="Item[@Name='RowName']"/>
                  </td>
                  <!-- td style="width:7px;border-top:1px black solid;border-left:1px black solid;border-bottom:1px black solid;">

                    <xsl:if test="position()>1">
                      <xsl:attribute name="style">width: 7px; border-left: 1px black solid; border-bottom: 1px black solid;</xsl:attribute>
                    </xsl:if>
                    &#160;
                  </td -->
                  <td  style="text-align:left;white-space:nowrap;">

                    <xsl:choose>
                      <xsl:when test="$female_divWidthVal>=1">
                        <div style="width:{$female_divWidthVal}%; background-color: #FF0000;float:left;filter:progid:DXImageTransform.Microsoft.Shadow(color=#909090,direction=120,strength=4);-moz-box-shadow: 2px 2px 10px #909090;-webkit-box-shadow: 2px 2px 10px #909090;box-shadow:2px 2px 10px #909090;">&#160;</div>
                      </xsl:when>
                      <xsl:when test="$female_divWidthVal>0 and $female_divWidthVal&lt;1">
                        <div style="width:1px; background-color: #FF0000;float:left;filter:progid:DXImageTransform.Microsoft.Shadow(color=#909090,direction=120,strength=4);-moz-box-shadow: 2px 2px 10px #909090;-webkit-box-shadow: 2px 2px 10px #909090;box-shadow:2px 2px 10px #909090;">&#160;</div>
                      </xsl:when>
                      <xsl:otherwise>
                        <div style="width:1px;float:left;">&#160;</div>
                      </xsl:otherwise>
                    </xsl:choose>
                     &#160;<xsl:value-of select="Item[@Name='Female']"/>
                  </td>
                </tr>
                <tr>
                	<td style="height:1px;"></td>
                	<td></td>
                	<td></td>
                </tr>
              </xsl:for-each>
            </table>
          </div>
        </div>
      </body>
    </html>

  </xsl:template>


  <xsl:template name="maxFunc">
    <xsl:param name="posVal"/>
    <xsl:param name="endPosVal"/>
    <xsl:param name="result"/>
    <xsl:choose>
      <xsl:when test="$endPosVal>$posVal">

        <xsl:call-template name="maxFunc">
          <xsl:with-param name="posVal" select="$posVal+1"/>
          <xsl:with-param name="endPosVal" select="$endPosVal"/>
          <xsl:with-param name="result">
            <xsl:variable name="nodeVal" select="$nodeSet[$posVal]"/>
            <xsl:choose>
              <xsl:when test="$nodeVal>$result">
                <xsl:value-of select="$nodeSet[$posVal]"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="$result"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
        </xsl:call-template>

      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="nodeVal" select="$nodeSet[$posVal]"/>
        <xsl:choose>
          <xsl:when test="$nodeVal>$result">
            <xsl:value-of select="$nodeSet[$posVal]"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$result"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
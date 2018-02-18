<?xml version="1.0" encoding="UTF-8"?>
<!--
The MIT License (MIT)

Copyright (c) 2017-2018 Yegor Bugayenko

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:template match="skeleton">
    <metric>
      <xsl:apply-templates select="@*"/>
      <title>LCOM5</title>
      <description>
        <xsl:text>'LCOM5' is a 1996 revision by B. Henderson-Sellers,
          L. L. Constantine, and I. M. Graham, of the initial LCOM metric
          proposed by MIT researchers.
          The values for LCOM5 are defined in the real interval [0, 1] where
          '0' describes "perfect cohesion" and '1' describes "no cohesion".
          Two problems with the original definition are addressed:
            a) LCOM5 has the ability to give values across the full range and
               no specific value has a higher probability of attainment than
               any other (the original LCOM has a preference towards the
               value "0")
            b) Following on from the previous point, the values can be uniquely
               interpreted in terms of cohesion, suggesting that they be treated
               as percentages of the "no cohesion" score '1'</xsl:text>
      </description>
      <xsl:apply-templates select="node()"/>
    </metric>
  </xsl:template>
  <xsl:template match="class">
    <xsl:variable name="methods" select="methods/method"/>
    <xsl:variable name="m" select="count($methods)"/>
    <xsl:variable name="attributes" select="attributes/attribute/text()"/>
    <xsl:variable name="a" select="count($attributes)"/>
    <xsl:variable name="attributes_use">
      <xsl:for-each select="$attributes">
        <xsl:variable name="attr" select="."/>
        <count>
          <xsl:value-of select="count($methods[ops/op = $attr])"/>
        </count>
      </xsl:for-each>
    </xsl:variable>
    <xsl:copy>
      <xsl:attribute name="value">
        <xsl:choose>
          <xsl:when test="$a = 0">
            <xsl:text>NaN</xsl:text>
          </xsl:when>
          <xsl:when test="$m = 1">
            <xsl:text>NaN</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:variable name="lcom" select="(((1 div $a) * sum($attributes_use/count)) - $m) div (1 - $m)"/>
            <xsl:value-of select="format-number($lcom, '0.####')"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="@*"/>
      <vars>
        <var id="m">
          <xsl:value-of select="$m"/>
        </var>
        <var id="a">
          <xsl:value-of select="$a"/>
        </var>
      </vars>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>

<?xml version="1.0"?>
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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="2.0">
  <xsl:template match="class">
    <xsl:copy>
      <xsl:attribute name="short_id">
        <xsl:value-of select="replace(replace(@id, '([a-z])[a-z0-9\$]+\.', '$1.'), '([A-Z])[A-Za-z0-9]+\$', '$1..\$')"/>
      </xsl:attribute>
      <xsl:attribute name="rank">
        <xsl:choose>
          <xsl:when test="metric">
            <xsl:value-of select="format-number(sum(metric/@rank) div (count(metric) * 5),'0.0000')"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>0</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="trust">
        <xsl:variable name="counts" as="node()*">
          <xsl:if test="metric[@color='red']">
            <c>
              <xsl:value-of select="count(metric[@color='red'])"/>
            </c>
          </xsl:if>
          <xsl:if test="metric[@color='yellow']">
            <c>
              <xsl:value-of select="count(metric[@color='yellow'])"/>
            </c>
          </xsl:if>
          <xsl:if test="metric[@color='green']">
            <c>
              <xsl:value-of select="count(metric[@color='green'])"/>
            </c>
          </xsl:if>
        </xsl:variable>
        <xsl:choose>
          <xsl:when test="count($counts)">
            <xsl:value-of select="sum($counts) div count($counts) div count(metric)"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>0</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>

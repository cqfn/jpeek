<?xml version="1.0"?>
<!--
The MIT License (MIT)

Copyright (c) 2017-2024 Yegor Bugayenko

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
  <xsl:param name="low"/>
  <xsl:param name="high"/>
  <xsl:template match="metric">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
      <colors low="{$low}" high="{$high}">
        <xsl:text>[</xsl:text>
        <xsl:value-of select="format-number($low, '0.0000')"/>
        <xsl:text> .. </xsl:text>
        <xsl:value-of select="format-number($high, '0.0000')"/>
        <xsl:text>]</xsl:text>
      </colors>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="class">
    <xsl:copy>
      <xsl:attribute name="color">
        <xsl:choose>
          <xsl:when test="$low &lt; $high">
            <xsl:choose>
              <xsl:when test="@value &lt; $low">
                <xsl:text>red</xsl:text>
              </xsl:when>
              <xsl:when test="@value &gt; $high">
                <xsl:text>green</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>yellow</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <xsl:otherwise>
            <xsl:choose>
              <xsl:when test="@value &lt; $high">
                <xsl:text>green</xsl:text>
              </xsl:when>
              <xsl:when test="@value &gt; $low">
                <xsl:text>red</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>yellow</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
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

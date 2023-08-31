<?xml version="1.0"?>
<!--
The MIT License (MIT)

Copyright (c) 2017-2023 Yegor Bugayenko

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
  <xsl:variable name="clean" select="//class[@value!='NaN']"/>
  <xsl:variable name="min" select="number(min($clean/@value))"/>
  <xsl:variable name="max" select="number(max($clean/@value))"/>
  <xsl:variable name="all" select="$clean[@value!=$min and @value!=$max]"/>
  <xsl:template match="metric">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
      <xsl:apply-templates select="." mode="range"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="metric" mode="range">
    <min>
      <xsl:choose>
        <xsl:when test="$all">
          <xsl:value-of select="min($all/@value)"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>0</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </min>
    <max>
      <xsl:choose>
        <xsl:when test="$all">
          <xsl:value-of select="max($all/@value)"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>0</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </max>
  </xsl:template>
  <xsl:template match="class|package|app">
    <xsl:copy>
      <xsl:attribute name="element">
        <xsl:value-of select="number(@value) &gt; $min and number(@value) &lt; $max"/>
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

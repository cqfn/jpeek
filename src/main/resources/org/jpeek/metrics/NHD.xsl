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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:template match="skeleton">
    <metric>
      <xsl:apply-templates select="@*"/>
      <title>NHD</title>
      <description>
        <xsl:text>The NHD (Normalized Hamming Distance) class cohesion metric
          is based on the "Hamming distance" from information theory. Here,
          we measure the similarity in all methods of a class in terms of
          the types of their arguments. A class in which all methods accept
          the same set of types of parameters is said to be in "perfect
          parameter agreement" (NHD score "1"), whereas a class in which all
          methods accept unique parameter types not shared by others has
          no parameter agreement (NHD score "0").</xsl:text>
      </description>
      <xsl:apply-templates select="node()"/>
    </metric>
  </xsl:template>
  <xsl:template match="class">
    <xsl:variable name="class" select="."/>
    <xsl:variable name="types" select="distinct-values($class/methods/method/args/arg[@type!='V']/@type)"/>
    <xsl:variable name="l" select="count($types)"/>
    <xsl:variable name="k" select="count($class/methods/method)"/>
    <xsl:variable name="c">
      <xsl:for-each select="$types">
        <xsl:variable name="type" select="."/>
        <xsl:variable name="cj" select="count($class/methods/method[args/arg/@type=$type])"/>
        <value>
          <xsl:value-of select="$cj * ($k - $cj)"/>
        </value>
      </xsl:for-each>
    </xsl:variable>
    <xsl:copy>
      <xsl:attribute name="value">
        <xsl:choose>
          <xsl:when test="$k = 0 or $k = 1">
            <xsl:text>NaN</xsl:text>
          </xsl:when>
          <xsl:when test="$l = 0">
            <xsl:text>NaN</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:variable name="nhd" select="1 - (2 div ($l * $k * ($k - 1)) * sum($c/value))"/>
            <xsl:value-of select="format-number($nhd, '0.####')"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="@*"/>
      <vars>
        <var id="k">
          <xsl:value-of select="$k"/>
        </var>
        <var id="l">
          <xsl:value-of select="$l"/>
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

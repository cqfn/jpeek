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
      <title>MMAC</title>
      <description>
        Method-Method through Attributes Cohesion (MMAC).
        The MMAC is the average cohesion of all pairs of methods.
        In simple words this metric shows how many methods have the
        same parameters or return types. When class has some number
        of methods and most of them operate the same parameters it
        assumes better. It looks like class contains overloaded
        methods. Preferably when class has only one method with
        parameters and/or return type and it assumes that class
        do only one thing. Value of MMAC metric is better for these
        one classes.
        Metric value is in interval [0, 1]. Value closer to 1 is better.
      </description>
      <xsl:apply-templates select="node()"/>
    </metric>
  </xsl:template>
  <xsl:template match="class">
    <xsl:variable name="class" select="."/>
    <xsl:variable name="k" select="count($class/methods/method)"/>
    <xsl:variable name="types" select="distinct-values($class/methods/method/args/arg[@type!='V']/@type)"/>
    <xsl:variable name="l" select="count($types)"/>
    <xsl:variable name="type_methods">
      <xsl:for-each select="$types">
        <xsl:variable name="type" select="."/>
        <xsl:variable name="count" select="count($class/methods/method[args/arg/@type=$type])"/>
        <count>
          <xsl:value-of select="$count * ($count - 1)"/>
        </count>
      </xsl:for-each>
    </xsl:variable>
    <xsl:copy>
      <xsl:attribute name="value">
        <xsl:choose>
          <xsl:when test="$k = 0">
            <xsl:text>NaN</xsl:text>
          </xsl:when>
          <xsl:when test="$l = 0">
            <xsl:text>NaN</xsl:text>
          </xsl:when>
          <xsl:when test="$k = 1">
            <xsl:text>NaN</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:variable name="mmac" select="sum($type_methods/count) div ($k * $l * ($k - 1))"/>
            <xsl:value-of select="format-number($mmac, '0.####')"/>
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

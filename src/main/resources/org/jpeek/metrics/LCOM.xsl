<?xml version="1.0"?>
<!--
The MIT License (MIT)

Copyright (c) 2017 Yegor Bugayenko

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
      <title>LCOM</title>
      <description>
        <xsl:text>LCOM is calculated as the number of pairs of methods
          operating on disjoint sets of instance variables,
          reduced by the number of method pairs acting on at
          least one shared instance variable.
          Say, there are 5 methods in a class. This means that there are 10
          pairs of methods (`5 * 4 / 2`). Now, we need to see how many of these
          pairs are using at least one and the same attribute (Nonempty) and how many
          of them are not using any similar attributes (Empty). Then, we
          just do `LCOM = Empty - Nonempty`. The metric can be really big,
          starting from zero and up to any possible number. The bigger the
          value the least cohesive is the class. A perfect design would have
          `LCOM=0`.</xsl:text>
      </description>
      <xsl:apply-templates select="node()"/>
    </metric>
  </xsl:template>
  <xsl:template match="class">
    <xsl:variable name="class" select="."/>
    <xsl:variable name="pairs">
      <xsl:for-each select="$class/methods/method">
        <xsl:variable name="i" select="position()"/>
        <xsl:variable name="left" select="."/>
        <xsl:variable name="left_ops" select="$left/ops/op[@code='get' or @code='put']"/>
        <xsl:for-each select="$class/methods/method">
          <xsl:if test="position() &gt; $i">
            <xsl:variable name="right" select="."/>
            <xsl:variable name="right_ops" select="$right/ops/op[@code='get' or @code='put']"/>
            <pair>
              <xsl:value-of select="count($left_ops[.=$right_ops])"/>
            </pair>
          </xsl:if>
        </xsl:for-each>
      </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="empty" select="count($pairs/pair[.=0])"/>
    <xsl:variable name="nonempty" select="count($pairs/pair[.!=0])"/>
    <xsl:copy>
      <xsl:attribute name="value">
        <xsl:choose>
          <xsl:when test="$nonempty &gt; $empty">
            <xsl:text>0</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$empty - $nonempty"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="@*"/>
      <vars>
        <var id="methods">
          <xsl:value-of select="count($class/methods/method)"/>
        </var>
        <var id="pairs">
          <xsl:value-of select="count($pairs/pair)"/>
        </var>
        <var id="empty">
          <xsl:value-of select="$empty"/>
        </var>
        <var id="nonempty">
          <xsl:value-of select="$nonempty"/>
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

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
  <xsl:param name="ctors" select="0"/>
  <xsl:template match="skeleton">
    <metric>
      <xsl:apply-templates select="@*"/>
      <title>LCOM</title>
      <description>
        <xsl:text><![CDATA[
          LORM = (R / RN) where,
          R = number of pairs of methods in the class for which
          one method contains conceptual relations forming
          external links out of the set of concepts that
          belong to the method to or from the set of concepts
          belonging to another method in the class.
          N = total number of member functions (methods)
          RN - Total number of possible relations. RN = N * (N - 1) / 2
        ]]></xsl:text>
      </description>
      <xsl:apply-templates select="node()"/>
    </metric>
  </xsl:template>
  <xsl:template match="class">
    <xsl:variable name="methods" select="methods/method[($ctors=0 and @ctor='false') or $ctors=1]"/>
    <xsl:variable name="possible_relations">
      <xsl:for-each select="$methods">
        <xsl:variable name="i" select="position()"/>
        <xsl:variable name="left" select="."/>
        <xsl:variable name="left_ops" select="$left/ops/op[@code='get' or @code='put']"/>
        <xsl:for-each select="$methods">
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
    <!--
    @todo #15:30min `conceptual-relations` are pairs of methods in the class
     for which one method contains conceptual relations forming external
     links out of the set of concepts that belong to the method to or from
     the set of concepts belonging to another method in the class
     (quoted from here: https://github.com/yegor256/jpeek/blob/master/papers/izadkhah17.pdf).
     Right now it is stubbed because information about the method's conceptual
     relations are not available anywhere in the project. Collect
     more information on what these conceptual relations are in LORM, ensure that
     JPeek core collects information on such relations and unstub the lines commented
     below.
    -->
    <!--
     <xsl:variable name="possible_relations"/>
     <xsl:variable name="R" select="count($possible_relations)"/>
    -->
    <xsl:variable name="R">
      <xsl:value-of select="0"/>
    </xsl:variable>
    <xsl:variable name="RN" select="count($possible_relations)"/>
    <xsl:variable name="LORM">
      <xsl:choose>
        <xsl:when test="$RN lt 1">
          <xsl:value-of select="0"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$R div $RN"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:copy>
      <xsl:attribute name="value" select="$LORM"/>
      <xsl:apply-templates select="@*"/>
      <vars>
        <var id="R">
          <xsl:value-of select="$R"/>
        </var>
        <var id="RN">
          <xsl:value-of select="$RN"/>
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

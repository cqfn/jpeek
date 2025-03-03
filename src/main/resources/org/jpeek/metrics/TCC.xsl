<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:template match="skeleton">
    <metric>
      <xsl:apply-templates select="@*"/>
      <title>TCC</title>
      <description>
        <xsl:text>TCC(C) = NDC(C) / NP(C), where C is the class, NP(C) is a
          maximal possible number of direct or indirect connections - N * (N - 1) / 2,
          NDC(C) is a number of direct connections.
          Value of the metric is in range [0, 1], greater is better.
        </xsl:text>
      </description>
      <xsl:apply-templates select="node()"/>
    </metric>
  </xsl:template>
  <xsl:template match="class">
    <!--
    @todo #120:30min TCC: inclusion of inherited attributes and methods in the analysis
     should be configurable. Come back here after #187 is fixed and adjust the xpath
     for `attrs` and `methods` accordingly.
    -->
    <xsl:variable name="attrs" select="attributes/attribute[@static='false']/text()"/>
    <!--
    @todo #120:30min TCC: this metric needs to exclude private methods from the analysis.
     Adjust the xpath for `methods` accordingly after #188 is fixed.
    -->
    <xsl:variable name="methods" select="methods/method[@abstract='false' and @ctor='false']"/>
    <xsl:variable name="methods_count" select="count($methods)"/>
    <xsl:variable name="NC" select="$methods_count * ($methods_count - 1) div 2"/>
    <xsl:variable name="directly-related-pairs">
      <xsl:for-each select="$methods">
        <xsl:variable name="i" select="position()"/>
        <xsl:variable name="left" select="."/>
        <xsl:variable name="left_attrs" select="$attrs[. = $left/ops/op/text()]"/>
        <xsl:for-each select="$methods">
          <xsl:if test="position() &gt; $i">
            <xsl:variable name="right" select="."/>
            <xsl:variable name="right_attrs" select="$attrs[. = $right/ops/op/text()]"/>
            <xsl:if test="exists($left_attrs[. = $right_attrs])">
              <pair/>
            </xsl:if>
          </xsl:if>
        </xsl:for-each>
      </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="NDC" select="count($directly-related-pairs/pair)"/>
    <xsl:copy>
      <xsl:attribute name="value">
        <xsl:choose>
          <xsl:when test="$methods_count le 1">
            <xsl:text>1</xsl:text>
          </xsl:when>
          <xsl:when test="count($attrs) lt 1">
            <xsl:text>1</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:variable name="tcc" select="$NDC div $NC"/>
            <xsl:value-of select="format-number($tcc, '0.####')"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="@*"/>
      <vars>
        <var id="attributes">
          <xsl:value-of select="count($attrs)"/>
        </var>
        <var id="methods">
          <xsl:value-of select="count($methods)"/>
        </var>
        <var id="NC">
          <xsl:value-of select="$NC"/>
        </var>
        <var id="NDC">
          <xsl:value-of select="$NDC"/>
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

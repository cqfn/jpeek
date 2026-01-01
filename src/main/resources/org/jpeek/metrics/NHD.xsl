<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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

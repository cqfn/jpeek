<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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

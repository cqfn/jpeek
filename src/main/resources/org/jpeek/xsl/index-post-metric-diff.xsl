<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="2.0">
  <xsl:variable name="score">
    <xsl:choose>
      <xsl:when test="/index/metric">
        <xsl:value-of select="sum(/index/metric/score) div count(/index/metric)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="0"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:template match="/index">
    <xsl:copy>
      <xsl:attribute name="score">
        <xsl:value-of select="format-number($score,'0.0000')"/>
      </xsl:attribute>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="metric">
    <xsl:copy>
      <xsl:attribute name="diff">
        <xsl:choose>
          <xsl:when test="$score &gt; 0">
            <xsl:value-of select="((score - $score) div $score)"/>
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

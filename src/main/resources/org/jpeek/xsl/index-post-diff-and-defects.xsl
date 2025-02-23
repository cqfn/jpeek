<?xml version="1.0"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="2.0">
  <xsl:template match="/index">
    <xsl:copy>
      <xsl:attribute name="diff">
        <xsl:choose>
          <xsl:when test="metric">
            <xsl:value-of select="(sum(metric[@diff &gt; 0]/@diff) - sum(metric[@diff &lt; 0]/@diff)) div count(metric)"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>0</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="defects">
        <xsl:choose>
          <xsl:when test="metric">
            <xsl:value-of select="sum(metric/defects) div count(metric)"/>
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

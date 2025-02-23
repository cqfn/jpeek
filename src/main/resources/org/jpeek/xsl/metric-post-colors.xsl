<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:param name="low"/>
  <xsl:param name="high"/>
  <xsl:template match="metric">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
      <colors low="{$low}" high="{$high}">
        <xsl:text>[</xsl:text>
        <xsl:value-of select="format-number($low, '0.0000')"/>
        <xsl:text> .. </xsl:text>
        <xsl:value-of select="format-number($high, '0.0000')"/>
        <xsl:text>]</xsl:text>
      </colors>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="class">
    <xsl:copy>
      <xsl:attribute name="color">
        <xsl:choose>
          <xsl:when test="$low &lt; $high">
            <xsl:choose>
              <xsl:when test="@value &lt; $low">
                <xsl:text>red</xsl:text>
              </xsl:when>
              <xsl:when test="@value &gt; $high">
                <xsl:text>green</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>yellow</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <xsl:otherwise>
            <xsl:choose>
              <xsl:when test="@value &lt; $high">
                <xsl:text>green</xsl:text>
              </xsl:when>
              <xsl:when test="@value &gt; $low">
                <xsl:text>red</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>yellow</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
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

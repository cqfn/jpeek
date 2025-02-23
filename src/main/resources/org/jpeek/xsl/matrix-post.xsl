<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="2.0">
  <xsl:template match="class">
    <xsl:copy>
      <xsl:attribute name="short_id">
        <xsl:value-of select="replace(replace(@id, '([a-z])[a-z0-9\$]+\.', '$1.'), '([A-Z])[A-Za-z0-9]+\$', '$1..\$')"/>
      </xsl:attribute>
      <xsl:attribute name="rank">
        <xsl:choose>
          <xsl:when test="metric">
            <xsl:value-of select="format-number(sum(metric/@rank) div (count(metric) * 5),'0.0000')"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>0</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="trust">
        <xsl:variable name="counts" as="node()*">
          <xsl:if test="metric[@color='red']">
            <c>
              <xsl:value-of select="count(metric[@color='red'])"/>
            </c>
          </xsl:if>
          <xsl:if test="metric[@color='yellow']">
            <c>
              <xsl:value-of select="count(metric[@color='yellow'])"/>
            </c>
          </xsl:if>
          <xsl:if test="metric[@color='green']">
            <c>
              <xsl:value-of select="count(metric[@color='green'])"/>
            </c>
          </xsl:if>
        </xsl:variable>
        <xsl:choose>
          <xsl:when test="count($counts)">
            <xsl:value-of select="sum($counts) div count($counts) div count(metric)"/>
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

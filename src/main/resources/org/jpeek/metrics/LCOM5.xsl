<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:template match="skeleton">
    <metric>
      <xsl:apply-templates select="@*"/>
      <title>LCOM5</title>
      <description>
        <xsl:text>'LCOM5' is a 1996 revision by B. Henderson-Sellers,
          L. L. Constantine, and I. M. Graham, of the initial LCOM metric
          proposed by MIT researchers.
          The values for LCOM5 are defined in the real interval [0, 1] where
          '0' describes "perfect cohesion" and '1' describes "no cohesion".
          Two problems with the original definition are addressed:
            a) LCOM5 has the ability to give values across the full range and
               no specific value has a higher probability of attainment than
               any other (the original LCOM has a preference towards the
               value "0")
            b) Following on from the previous point, the values can be uniquely
               interpreted in terms of cohesion, suggesting that they be treated
               as percentages of the "no cohesion" score '1'</xsl:text>
      </description>
      <xsl:apply-templates select="node()"/>
    </metric>
  </xsl:template>
  <xsl:template match="class">
    <xsl:variable name="class_fqn" select="replace(string-join(../@id | @id, '.'), '^\.', '')"/>
    <xsl:variable name="attrs_fqn">
      <xsl:for-each select="attributes/attribute">
        <xsl:copy>
          <xsl:copy-of select="@*"/>
          <xsl:if test="@static='true' and $class_fqn != ''">
            <xsl:value-of select="concat($class_fqn, '.')"/>
          </xsl:if>
          <xsl:value-of select="text()"/>
        </xsl:copy>
      </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="methods" select="methods/method"/>
    <xsl:variable name="m" select="count($methods)"/>
    <xsl:variable name="attributes" select="$attrs_fqn/*"/>
    <xsl:variable name="a" select="count($attributes/text())"/>
    <xsl:variable name="attributes_use">
      <xsl:for-each select="$attributes/text()">
        <xsl:variable name="attr" select="."/>
        <count>
          <xsl:value-of select="count($methods[ops/op = $attr])"/>
        </count>
      </xsl:for-each>
    </xsl:variable>
    <xsl:copy>
      <xsl:attribute name="value">
        <xsl:choose>
          <xsl:when test="$a = 0">
            <xsl:text>NaN</xsl:text>
          </xsl:when>
          <xsl:when test="$m = 1">
            <xsl:text>NaN</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:variable name="lcom" select="(((1 div $a) * sum($attributes_use/count)) - $m) div (1 - $m)"/>
            <xsl:value-of select="format-number($lcom, '0.####')"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="@*"/>
      <vars>
        <var id="m">
          <xsl:value-of select="$m"/>
        </var>
        <var id="a">
          <xsl:value-of select="$a"/>
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

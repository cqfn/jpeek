<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:template match="skeleton">
    <metric>
      <xsl:apply-templates select="@*"/>
      <title>DOC</title>
      <description>
        <xsl:text>
          DOC (Distance of Coupling) estimates how many data
          transformation steps happen inside methods. It is calculated as
          the average number of local-variable stores minus one. For each
          method, we count how many times a local variable is assigned
          (locals/var[@code='store']). The first assignment in a chain doesn't
          increase coupling, so we subtract one. The class value is the
          arithmetic average across its methods.
        </xsl:text>
      </description>
      <xsl:apply-templates select="node()"/>
    </metric>
  </xsl:template>
  <xsl:template match="class">
    <xsl:variable name="methods" select="methods/method"/>
    <xsl:variable name="docs">
      <xsl:for-each select="$methods">
        <xsl:variable name="stores" select="count(locals/var[@code='store'])"/>
        <doc>
          <xsl:choose>
            <xsl:when test="$stores &gt; 0">
              <xsl:value-of select="$stores - 1"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>0</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </doc>
      </xsl:for-each>
    </xsl:variable>
    <xsl:copy>
      <xsl:attribute name="value">
        <xsl:choose>
          <xsl:when test="count($methods) &gt; 0">
            <xsl:value-of select="sum($docs/doc) div count($methods)"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>0</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="@*"/>
      <vars>
        <var id="methods">
          <xsl:value-of select="count($methods)"/>
        </var>
        <var id="stores">
          <xsl:value-of select="count($methods/locals/var[@code='store'])"/>
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

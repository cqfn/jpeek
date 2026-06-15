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
          the average number of local-variable stores that depend on
          previous local-variable values. For each method, we count how many
          times a local variable is assigned after a load from any previously stored local.
          The class value is the arithmetic average across its methods.
        </xsl:text>
      </description>
      <xsl:apply-templates select="node()"/>
    </metric>
  </xsl:template>
  <xsl:template match="class">
    <xsl:variable name="methods" select="methods/method[@ctor = 'false' or count(ops/op[@code != 'call' or (@code = 'call' and not(matches(name, '\.&lt;(cl)?init&gt;$')))]) &gt; 0]"/>
    <xsl:variable name="docs">
      <xsl:for-each select="$methods">
        <xsl:variable name="stores" select="count(locals/var[@code='store'][preceding-sibling::var[@code='load' and @var = preceding-sibling::var[@code='store']/@var]])"/>
        <doc>
          <xsl:value-of select="$stores"/>
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
          <xsl:value-of select="count($methods/locals/var[@code='store'][preceding-sibling::var[@code='load' and @var = preceding-sibling::var[@code='store']/@var]])"/>
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

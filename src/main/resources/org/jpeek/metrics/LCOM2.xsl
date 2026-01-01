<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:template match="skeleton">
    <metric>
      <xsl:apply-templates select="@*"/>
      <title>LCOM2</title>
      <description>
        <xsl:text>LCOM2 is an attempt to address some shortcomings of the
          original LCOM:
          - LCOM gives a value of zero for very different classes
          - Its definition is based on method-data interaction, which may not
            be a correct way to define cohesiveness in the object-oriented world
          - Very different classes may have an equal value
          - As LCOM is defined on variable access, it's not well suited for
            classes that internally access their data via properties
          LCOM2 equals the percentage of methods that do not access a specific
          attribute averaged over all attributes in the class. If the number
          of methods or attributes is zero, LCOM2 is undefined and displayed
          as zero. Values for LCOM2 are in the range [0, 1]. A low value indicates
          high cohesion and a well-designed class. It is likely that the system has
          good class subdivision implying simplicity and high reusability. A
          cohesive class will tend to provide a high degree of encapsulation.
          A higher value of LCOM2 indicates decreased encapsulation and
          increased complexity, thereby increasing the likelihood of errors.</xsl:text>
      </description>
      <xsl:apply-templates select="node()"/>
    </metric>
  </xsl:template>
  <xsl:template match="class">
    <xsl:variable name="attrs" select="attributes/attribute/text()"/>
    <xsl:variable name="attrs_count" select="count($attrs)"/>
    <xsl:variable name="methods" select="methods/method[@bridge=false()]"/>
    <xsl:variable name="methods_count" select="count($methods)"/>
    <xsl:variable name="attr_use">
      <xsl:for-each select="$attrs">
        <xsl:variable name="attr" select="."/>
        <count>
          <xsl:value-of select="count($methods/ops[op = $attr])"/>
        </count>
      </xsl:for-each>
    </xsl:variable>
    <xsl:copy>
      <xsl:attribute name="value">
        <xsl:choose>
          <xsl:when test="$methods_count = 0 or $attrs_count = 0">
            <xsl:text>0</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="format-number(1 - (sum($attr_use/count) div ($methods_count * $attrs_count)), '0.####')"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="@*"/>
      <vars>
        <var id="methods">
          <xsl:value-of select="$methods_count"/>
        </var>
        <var id="attributes">
          <xsl:value-of select="$attrs_count"/>
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

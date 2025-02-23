<?xml version="1.0"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:template match="skeleton">
    <metric>
      <xsl:apply-templates select="@*"/>
      <title>OCC</title>
      <description>
        <xsl:text>Optimistic Class Cohesion (OCC) quantifies the maximum extent
          of shared attributes between any two methods among all methods of
          a class. That is, for all pairs of methods of a class, it calculates
          a score based on the number of attributes of the class they both use
          in common, and then the maximum score is taken as the value for OCC.
          OCC is given a value of 0 for the exceptional case when the class has
          only one method.</xsl:text>
      </description>
      <xsl:apply-templates select="node()"/>
    </metric>
  </xsl:template>
  <xsl:template match="class">
    <xsl:variable name="class_fqn" select="replace(string-join(../@id | @id, '.'), '^\.', '')"/>
    <xsl:variable name="attrs">
      <xsl:for-each select="attributes/attribute">
        <xsl:copy>
          <xsl:if test="@static='true' and $class_fqn != ''">
            <xsl:value-of select="concat($class_fqn, '.')"/>
          </xsl:if>
          <xsl:value-of select="text()"/>
        </xsl:copy>
      </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="A" select="$attrs/*/text()"/>
    <xsl:variable name="M" select="methods/method"/>
    <xsl:variable name="n" select="count($M)"/>
    <xsl:variable name="connections">
      <xsl:for-each select="$M">
        <xsl:variable name="this" select="."/>
        <xsl:variable name="attrs_used_by_this" select="$A[. = $this/ops/op/text()]"/>
        <Rw from="$this/@name">
          <xsl:for-each select="$this/following-sibling::method">
            <xsl:variable name="other" select="."/>
            <xsl:variable name="attrs_used_by_other" select="$A[. = $other/ops/op/text()]"/>
            <xsl:if test="$attrs_used_by_this[. = $attrs_used_by_other]">
              <reachable>
                <xsl:value-of select="$other/@name"/>
              </reachable>
            </xsl:if>
          </xsl:for-each>
        </Rw>
      </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="Rw_scalar">
      <xsl:for-each select="$connections/Rw">
        <Rw>
          <xsl:choose>
            <xsl:when test="$n != 1">
              <xsl:value-of select="count(./reachable) div ($n - 1)"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>0</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </Rw>
      </xsl:for-each>
    </xsl:variable>
    <xsl:copy>
      <xsl:variable name="Rw_max" select="max($Rw_scalar/Rw)"/>
      <xsl:attribute name="value">
        <xsl:value-of select="format-number($Rw_max, '0.####')"/>
      </xsl:attribute>
      <xsl:apply-templates select="@*"/>
      <vars>
        <var id="n">
          <xsl:value-of select="$n"/>
        </var>
        <var id="A">
          <xsl:value-of select="count($A)"/>
        </var>
        <var id="Rw_total">
          <xsl:value-of select="count($connections/Rw/reachable)"/>
        </var>
        <xsl:if test="$Rw_max">
          <var id="Rw_max">
            <xsl:value-of select="$Rw_max"/>
          </var>
        </xsl:if>
      </vars>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>

<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:template match="skeleton">
    <metric>
      <xsl:apply-templates select="@*"/>
      <title>PCC</title>
      <description>
        <xsl:text>The Pessimistic Class Cohesion (PCC) metric quantifies the
          maximum extent to which any two methods of a class have a strong
          dependency where one writes to an attribute and the other reads from
          the same attribute. The maximum number of methods that can be reached
          from any given method according to this definition in proportion to
          the total amount of methods is taken as the value for PCC.
          PCC is given a value of 0 for the exceptional case when the class has
          only one method.</xsl:text>
      </description>
      <xsl:apply-templates select="node()"/>
    </metric>
  </xsl:template>
  <xsl:template match="class">
    <xsl:variable name="M" select="methods/method"/>
    <xsl:variable name="n" select="count($M)"/>
    <xsl:variable name="A" select="attributes/attribute/text()"/>
    <xsl:variable name="connections">
      <xsl:for-each select="$M">
        <xsl:variable name="me" select="."/>
        <xsl:variable name="attrs_written_by_me" select="$A[. = $me/ops/op[starts-with(@code, 'put')]/text()]"/>
        <xsl:variable name="attrs_read_by_me" select="$A[. = $me/ops/op[starts-with(@code, 'get')]/text()]"/>
        <xsl:for-each select="$me/following-sibling::method">
          <xsl:variable name="other" select="."/>
          <xsl:variable name="attrs_written_by_other" select="$A[. = $other/ops/op[starts-with(@code, 'put')]/text()]"/>
          <xsl:variable name="attrs_read_by_other" select="$A[. = $other/ops/op[starts-with(@code, 'get')]/text()]"/>
          <xsl:if test="$attrs_written_by_me[. = $attrs_read_by_other]">
            <connection>
              <from>
                <xsl:value-of select="$me/@name"/>
              </from>
              <to>
                <xsl:value-of select="$other/@name"/>
              </to>
            </connection>
          </xsl:if>
          <xsl:if test="$attrs_written_by_other[. = $attrs_read_by_me]">
            <connection>
              <from>
                <xsl:value-of select="$other/@name"/>
              </from>
              <to>
                <xsl:value-of select="$me/@name"/>
              </to>
            </connection>
          </xsl:if>
        </xsl:for-each>
      </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="Rw_scalar">
      <xsl:for-each select="distinct-values($connections/connection/from)">
        <xsl:variable name="from" select="."/>
        <Rw>
          <xsl:choose>
            <xsl:when test="$n != 1">
              <xsl:value-of select="count($connections/connection[from = $from]) div ($n - 1)"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>0</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </Rw>
      </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="Rw_max" select="max($Rw_scalar/Rw)"/>
    <xsl:copy>
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
          <xsl:value-of select="count($connections/connection)"/>
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

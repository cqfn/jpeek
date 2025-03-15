<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:template match="skeleton/meta"/>
  <xsl:template match="skeleton">
    <metric>
      <xsl:apply-templates select="@*"/>
      <title>CCM</title>
      <description>
        <xsl:text>Class Connection Metric (CCM) is based on the connection
          graph of a class where each node is a method and there exists an
          "edge" or connection between nodes if either both nodes access the
          same attribute of a class, or if both call the same method of the
          class. The formula is defined as CCM = NC/(NMP x NCC), where
          NC = number of connections according to the previous criteria,
          NMP = maximum number of possible connections (NMP = (N x (N - 1))/2),
          and NCC = number of connected components.</xsl:text>
      </description>
      <xsl:apply-templates select="node()"/>
    </metric>
  </xsl:template>
  <xsl:variable name="met" select="/skeleton/meta"/>
  <xsl:template match="class">
    <xsl:variable name="currentClassId" select="@id"/>
    <xsl:variable name="currentPackageId" select="../@id"/>
    <xsl:variable name="currentClassNcc" select="$met/package[@id = $currentPackageId]/class[@id = $currentClassId]/ncc"/>
    <xsl:variable name="methods" select="methods/method[@ctor='false']"/>
    <xsl:variable name="edges">
      <xsl:for-each select="$methods">
        <xsl:variable name="method" select="."/>
        <xsl:for-each select="$method/following-sibling::method">
          <xsl:variable name="other" select="."/>
          <xsl:if test="$method/ops/op/text()[. = $other/ops/op/text()]">
            <edge>
              <method>
                <xsl:value-of select="$method/@name"/>
              </method>
              <method>
                <xsl:value-of select="$other/@name"/>
              </method>
            </edge>
          </xsl:if>
        </xsl:for-each>
      </xsl:for-each>
    </xsl:variable>
    <xsl:copy>
      <xsl:variable name="nc" select="count($edges/edge)"/>
      <xsl:variable name="ncc" select="$currentClassNcc"/>
      <xsl:variable name="nmp" select="(count($methods) * (count($methods) - 1)) div 2"/>
      <xsl:attribute name="value">
        <xsl:choose>
          <xsl:when test="$nmp = 0 or $ncc = 0">
            <xsl:text>NaN</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="format-number($nc div ($nmp * $ncc), '0.####')"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="@*"/>
      <vars>
        <var id="methods">
          <xsl:value-of select="count($methods)"/>
        </var>
        <var id="nc">
          <xsl:value-of select="$nc"/>
        </var>
        <var id="ncc">
          <xsl:value-of select="$ncc"/>
        </var>
        <var id="nmp">
          <xsl:value-of select="$nmp"/>
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

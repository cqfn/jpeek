<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:template match="skeleton">
    <metric>
      <xsl:apply-templates select="@*"/>
      <title>LCOM4</title>
      <description>
        <xsl:text>'LCOM4' is a 1995 revision by Martin Hitz and Behzad Montazeri,
          that further improves upon the revised versions LCOM3 proposed by
          W. Li and S. Henry in "Maintenance Metrics for the Object Oriented Paradigm".
          LCOM4 establishes connections between methods not just on shared attributes
          of the class, but also on whether one method calls the other. That is,
          methods A and B are said to be related if either both use at least one
          instance variable in common, OR A calls B, OR B calls A.
          LCOM4 tweaks the formula for method connections in order to eliminate
          the distorting effects that 'getters' may have on earlier LCOM variants,
          and also methods that purely rely on the reuse of other methods without
          accessing attributes directly (think method overloads where the ones
          with the least args reuse the ones with the most args).
        </xsl:text>
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
    <xsl:variable name="A" select="$attrs_fqn/*/text()"/>
    <xsl:variable name="a" select="count($A)"/>
    <!-- Ctors are not methods -->
    <xsl:variable name="M" select="methods/method[@ctor='false']"/>
    <xsl:variable name="m" select="count($M)"/>
    <!--
    @todo #216:30min LCOM4: "NotCommonAttributesWithAllArgsConstructor" test now fails.
     Constructor with all attributes parameters should not affect LCOM4 metric.
     So LCOM4.xsl needs to be fixed in order to avoid constructors affection to the metric values.
     "NotCommonAttributesWithAllArgsConstructor" test case must be added into collection
     returning by the targets() in the MetricsTest.java when this puzzle done.
    -->
    <xsl:variable name="E">
      <xsl:for-each select="$M">
        <xsl:variable name="this" select="."/>
        <xsl:variable name="this_fullname" select="concat($class_fqn, '.', $this/@name)"/>
        <xsl:variable name="this_attrs" select="$A[. = $this/ops/op[starts-with(@code, 'put') or starts-with(@code, 'get')]/text()]"/>
        <xsl:variable name="this_methods" select="$this/ops/op[@code = 'call' and matches(replace(., $class_fqn, ''), '^\.[^.]+$')]"/>
        <xsl:for-each select="$this/following-sibling::method">
          <xsl:variable name="other" select="."/>
          <xsl:variable name="other_fullname" select="concat($class_fqn, '.', $other/@name)"/>
          <xsl:variable name="other_attrs" select="$A[. = $other/ops/op[starts-with(@code, 'put') or starts-with(@code, 'get')]/text()]"/>
          <xsl:variable name="other_methods" select="$other/ops/op[@code = 'call' and matches(replace(., $class_fqn, ''), '^\.[^.]+$')]"/>
          <xsl:if test="exists($this_attrs[.= $other_attrs]) or exists($this_methods[.= $other_fullname]) or exists($other_methods[.= $this_fullname])">
            <pair/>
          </xsl:if>
        </xsl:for-each>
      </xsl:for-each>
    </xsl:variable>
    <xsl:copy>
      <!-- LCOM4 is the number of connected components of G.-->
      <xsl:attribute name="value">
        <xsl:choose>
          <xsl:when test="$m &lt; 2 or $a = 0">
            <xsl:text>0</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="format-number((((count($E/pair) div $a) - $m) div (1 - $m)), '0.####')"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="@*"/>
      <vars>
        <var id="methods">
          <xsl:value-of select="$m"/>
        </var>
        <var id="attributes">
          <xsl:value-of select="$a"/>
        </var>
        <var id="pairs">
          <xsl:value-of select="count($E/pair)"/>
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

<?xml version="1.0"?>
<!--
The MIT License (MIT)

Copyright (c) 2017-2018 Yegor Bugayenko

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:param name="ctors" select="0"/>
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
    <xsl:variable name="class_fqn" select="concat(parent::@id, '.', @id)"/>
    <xsl:variable name="attrs" select="attributes/attribute/text()"/>
    <xsl:variable name="methods" select="methods/method"/>
    <xsl:variable name="n" select="count($methods)"/>
    <xsl:variable name="Rw">
      <xsl:for-each select="$methods">
        <xsl:variable name="this" select="."/>
        <xsl:variable name="attrs_used_by_this" select="$attrs[concat($class_fqn, '.', .) = $this/ops/op/text()]"/>
          <pair anchor="$this/@name">
            <xsl:for-each select="$this/following-sibling::method">
              <xsl:variable name="other" select="."/>
              <xsl:variable name="attrs_used_by_other" select="$attrs[concat($class_fqn, '.', .) = $other/ops/op/text()]"/>
              <xsl:if test="count($attrs_used_by_this[. = $attrs_used_by_other]) > 0">
                <with_method>
                  <xsl:value-of select="$other/@name"/>
                </with_method>
              </xsl:if>
            </xsl:for-each>
          </pair> 
      </xsl:for-each>
    </xsl:variable>
    <xsl:copy>
      <xsl:variable name="max_Rw" select="max($Rw/pair)"/>
      <xsl:attribute name="value">
        <xsl:choose>
          <xsl:when test="$n = 1">
            <xsl:text>0</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="format-number($max_Rw div (n - 1), '0.####')"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="@*"/>
      <vars>
        <var id="methods">
          <xsl:value-of select="$n"/>
        </var>
        <var id="pairs">
          <xsl:value-of select="count($Rw/pair)"/>
        </var>
        <var id="attributes">
          <xsl:value-of select="count($attrs)"/>
        </var>
        <var id="max_Rw">
          <xsl:value-of select="$max_Rw"/>
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

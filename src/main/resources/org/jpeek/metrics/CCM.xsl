<?xml version="1.0"?>
<!--
The MIT License (MIT)

Copyright (c) 2017-2024 Yegor Bugayenko

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
  <xsl:template match="class">
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
            <edge>
              <method>
                <xsl:value-of select="$other/@name"/>
              </method>
              <method>
                <xsl:value-of select="$method/@name"/>
              </method>
            </edge>
          </xsl:if>
        </xsl:for-each>
      </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="graph">
      <xsl:for-each select="$methods">
        <xsl:variable name="method" select="."/>
        <node id="{$method/@name}">
          <xsl:for-each select="$edges/edge[method[1]/text() = $method/@name]">
            <edge>
              <xsl:value-of select="method[2]/text()"/>
            </edge>
          </xsl:for-each>
        </node>
      </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="result">
      <xsl:for-each select="$graph/node">
        <xsl:variable name="root" select="."/>
        <xsl:variable name="id" select="$root/@id"/>
        <xsl:element name="group">
          <xsl:attribute name="id">
            <xsl:value-of select="$id"/>
          </xsl:attribute>
          <xsl:call-template name="group">
            <xsl:with-param name="x" select="$root"/>
            <xsl:with-param name="seen" select="$id"/>
            <xsl:with-param name="graph" select="$graph"/>
          </xsl:call-template>
          <xsl:element name="edge">
            <xsl:value-of select="$id"/>
          </xsl:element>
        </xsl:element>
      </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="counts">
      <xsl:for-each-group select="$result/group" group-by="@id">
        <xsl:variable name="minValue">
          <xsl:perform-sort select="current-group()/edge">
            <xsl:sort select="./text()"/>
          </xsl:perform-sort>
        </xsl:variable>
        <minValue>
          <xsl:value-of select="$minValue/edge[1]/text()"/>
        </minValue>
      </xsl:for-each-group>
    </xsl:variable>
    <xsl:copy>
      <xsl:variable name="nc" select="count($edges/edge) div 2"/>
      <xsl:variable name="ncc" select="count(distinct-values($counts/minValue/text()))"/>
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
  <xsl:template name="group">
    <xsl:param name="x" as="element()"/>
    <xsl:param name="seen"/>
    <xsl:param name="graph"/>
    <xsl:for-each select="$x/edge">
      <xsl:variable name="r" select="."/>
      <xsl:if test="not(contains($seen, $r/text()))">
        <xsl:element name="edge">
          <xsl:value-of select="$r"/>
        </xsl:element>
        <xsl:call-template name="group">
          <xsl:with-param name="seen" select="concat($seen, $r/text())"/>
          <xsl:with-param name="x" select="$graph/node[@id=$r][1]"/>
          <xsl:with-param name="graph" select="$graph"/>
        </xsl:call-template>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>

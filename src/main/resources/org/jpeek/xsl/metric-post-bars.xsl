<?xml version="1.0"?>
<!--
The MIT License (MIT)

Copyright (c) 2017-2025 Yegor Bugayenko

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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0">
  <xsl:template match="metric">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
      <xsl:apply-templates select="." mode="bars"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="metric" mode="bars">
    <xsl:variable name="min" select="xs:float(min)"/>
    <xsl:variable name="max" select="xs:float(max)"/>
    <xsl:variable name="all" select="//class[xs:float(@value)&lt;=$max and xs:float(@value)&gt;=$min]"/>
    <xsl:variable name="steps" select="xs:integer(max((32, count($all) div 20)))"/>
    <bars>
      <xsl:variable name="delta" select="($max - $min) div $steps"/>
      <xsl:comment>
        <xsl:text>count: </xsl:text>
        <xsl:value-of select="count(//class)"/>
        <xsl:text>, steps: </xsl:text>
        <xsl:value-of select="$steps"/>
        <xsl:text>; all: </xsl:text>
        <xsl:value-of select="count($all)"/>
        <xsl:text>; min: </xsl:text>
        <xsl:value-of select="$min"/>
        <xsl:text>; max: </xsl:text>
        <xsl:value-of select="$max"/>
        <xsl:text>; delta: </xsl:text>
        <xsl:value-of select="$delta"/>
      </xsl:comment>
      <xsl:for-each select="0 to ($steps - 1)">
        <xsl:variable name="step" select="."/>
        <xsl:variable name="left" select="$min + $step * $delta"/>
        <xsl:variable name="right" select="$min + ($step + 1) * $delta"/>
        <xsl:variable name="classes" select="$all[(xs:float(@value) &gt;= $left) and (xs:float(@value) &lt; $right) or ($step = $steps -1 and xs:float(@value) = $min + $steps * $delta)]"/>
        <bar x="{$step div $steps}">
          <xsl:attribute name="color">
            <xsl:choose>
              <xsl:when test="$classes">
                <xsl:value-of select="$classes[1]/@color"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>yellow</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
          <xsl:value-of select="count($classes)"/>
        </bar>
        <xsl:comment>
          <xsl:text>step: </xsl:text>
          <xsl:value-of select="$step"/>
          <xsl:text>, left: </xsl:text>
          <xsl:value-of select="$left"/>
          <xsl:text>, right: </xsl:text>
          <xsl:value-of select="$right"/>
        </xsl:comment>
      </xsl:for-each>
    </bars>
  </xsl:template>
  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>

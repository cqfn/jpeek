<?xml version="1.0"?>
<!--
The MIT License (MIT)

Copyright (c) 2017 Yegor Bugayenko

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
  <xsl:template match="metric">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
      <xsl:apply-templates select="." mode="bars"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="metric" mode="bars">
    <xsl:variable name="all" select="//class[@value != 'NaN']"/>
    <xsl:variable name="steps" select="max((32, count($all) div 20))"/>
    <bars>
      <xsl:variable name="min" select="min($all/@value)"/>
      <xsl:variable name="max" select="max($all/@value)"/>
      <xsl:variable name="delta" select="($max - $min) div $steps"/>
      <xsl:comment>
        <xsl:text>steps: </xsl:text>
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
        <xsl:variable name="classes" select="$all[(@value &gt;= $step * $delta) and (@value &lt; ($step + 1) * $delta) or ($step = $steps -1 and @value = $steps * $delta)]"/>
        <bar x="{$step div $steps}" color="{$classes[1]/@color}">
          <xsl:value-of select="count($classes)"/>
        </bar>
      </xsl:for-each>
    </bars>
  </xsl:template>
  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>

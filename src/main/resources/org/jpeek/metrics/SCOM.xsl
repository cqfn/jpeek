<?xml version="1.0" encoding="UTF-8"?>
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
  <xsl:template match="skeleton">
    <metric>
      <xsl:apply-templates select="@*"/>
      <title>SCOM</title>
      <description>
        <xsl:text>"Sensitive Class Cohesion Metric" (SCOM) notes some deficits
          in the LCOM5 metric, particularly how it fails in some cases to
          discriminate (ie. it assigns the same score) between classes where
          one is clearly more cohesive than the other. In these cases, SCOM
          is more "sensitive" than LCOM5 because it evaluates to different
          values.
          Like LCOM5, SCOM values lie in the range [0..1], but their meanings
          are inverted: "0" indicates no cohesion at all (i.e. every method
          deals with an independent set of attributes), whereas "1" indicates
          full cohesion (ie. every method uses all the attributes of the class).
          This inversion stems from SCOM measuring how much "agreement" there
          is among the methods, unlike LCOM which measures how much
          "disagreement" there is.
          Another important distinction is that SCOM assigns "weights" to each
          pair of methods computed equal to the proportion of total attributes
          being used between the two. This contributes to the metric's
          "sensitivity".
          Finally, the authors provide a formula for the minimum value beyond
          which "we can claim that [the class] has at least two clusters and
          it must be subdivided into smaller, more cohesive classes".</xsl:text>
      </description>
      <xsl:apply-templates select="node()"/>
    </metric>
  </xsl:template>
  <xsl:template match="class">
    <xsl:variable name="methods" select="methods/method"/>
    <xsl:variable name="m" select="count($methods)"/>
    <xsl:variable name="attributes" select="attributes/attribute/text()"/>
    <xsl:variable name="a" select="count($attributes)"/>
    <xsl:variable name="SCOM_minK">
      <xsl:choose>
        <xsl:when test="$m = 0 or $m = 1 or $a = 0">
          <xsl:text>NaN</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:variable name="x" select="($m - 1) div $a"/>
          <xsl:variable name="S" select="0.5 * (1 + floor($x)) * (($x - floor($x)) + $m - 1)"/>
          <xsl:value-of select="$S * (2 div ($m * ($m - 1) * $a))"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="pairs">
      <xsl:for-each select="$methods">
        <xsl:variable name="method" select="."/>
        <xsl:for-each select="$method/following-sibling::method">
          <pair>
            <xsl:variable name="other" select="."/>
            <xsl:variable name="intersections">
              <xsl:for-each select="$attributes">
                <xsl:variable name="attr" select="."/>
                <match>
                  <xsl:value-of select="count($method/ops[op = $attr]) &gt; 0 and count($other/ops[op = $attr]) &gt; 0"/>
                </match>
              </xsl:for-each>
            </xsl:variable>
            <c>
              <xsl:choose>
                <xsl:when test="count($intersections/match[text() = 'true']) = 0">
                  <xsl:text>0</xsl:text>
                </xsl:when>
                <xsl:when test="min((count($method/ops/op), count($other/ops/op))) &gt; 0">
                  <xsl:value-of select="count($intersections/match[text() = 'true']) div min((count(distinct-values($method/ops/op)), count(distinct-values($other/ops/op))))"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>NaN</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </c>
            <alpha>
              <xsl:choose>
                <xsl:when test="$a &gt; 0">
                  <xsl:value-of select="count(distinct-values($method/ops/op/text() | $other/ops/op/text())) div $a"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>NaN</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </alpha>
          </pair>
        </xsl:for-each>
      </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="scores">
      <xsl:for-each select="$pairs/pair">
        <score>
          <xsl:choose>
            <xsl:when test="c != 'NaN' and alpha != 'NaN'">
              <xsl:value-of select="c * alpha"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>NaN</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </score>
      </xsl:for-each>
    </xsl:variable>
    <xsl:copy>
      <xsl:attribute name="value">
        <xsl:choose>
          <xsl:when test="$m = 0 or $m = 1 or $a = 0">
            <xsl:text>NaN</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:variable name="scom" select="(2 div ($m * ($m - 1))) * sum($scores/score)"/>
            <xsl:value-of select="format-number($scom, '0.####')"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="@*"/>
      <vars>
        <var id="m">
          <xsl:value-of select="$m"/>
        </var>
        <var id="pairs">
          <xsl:value-of select="count($pairs/pair)"/>
        </var>
        <var id="a">
          <xsl:value-of select="$a"/>
        </var>
        <var id="SCOM_minK">
          <xsl:value-of select="$SCOM_minK"/>
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

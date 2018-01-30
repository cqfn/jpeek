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
  <xsl:param name="ctors" select="0"/>
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
    <xsl:variable name="methods"
                  select="methods/method[($ctors=1 and @ctors='true') or @ctor='false']"/>
    <xsl:variable name="methods_count" select="count($methods)"/>
    <xsl:variable name="attributes" select="attributes/attribute/text()"/>
    <xsl:variable name="attributes_count" select="count($attributes)"/>
    <xsl:variable name="method_pairs">
      <xsl:for-each select="$methods">
        <xsl:variable name="method" select="."/>
        <xsl:for-each select="$method/following-sibling::method">
          <pair>
            <xsl:variable name="other" select="."/>
            <xsl:variable name="intersections">
              <xsl:for-each select="$attributes">
                <xsl:variable name="attr" select="."/>
                <match>
                  <xsl:value-of select="count($method/ops[op = $attr]) > 0 and count($other/ops[op = $attr]) > 0"/>
                </match>
              </xsl:for-each>
            </xsl:variable>
            <intensity>
              <xsl:choose>
                <xsl:when test="count($intersections/match[text() = 'true']) = 0">
                  <xsl:text>0</xsl:text>
                </xsl:when>
                <xsl:when test="min((count($method/ops/op), count($other/ops/op))) > 0">
                  <xsl:value-of select="count($intersections/match[text() = 'true']) div min((count(distinct-values($method/ops/op)), count(distinct-values($other/ops/op))))"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>NaN</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </intensity>
            <weight>
              <xsl:choose>
                <xsl:when test="$attributes_count > 0">
                  <xsl:value-of select="count(distinct-values($method/ops/op/text() | $other/ops/op/text())) div $attributes_count"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>NaN</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </weight>
          </pair>
        </xsl:for-each>
      </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="method_pair_scores">
      <xsl:for-each select="$method_pairs/pair">
        <score>
          <xsl:choose>
            <xsl:when test="intensity != 'NaN' and weight != 'NaN'">
              <xsl:value-of select="intensity * weight"/>
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
          <xsl:when test="$methods_count = 0 or $methods_count = 1 or $attributes_count = 0">
            <xsl:text>NaN</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="(2 div ($methods_count * ($methods_count - 1))) * sum($method_pair_scores/score)"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="@*"/>
      <!--
        @todo #68:30min Calculate SCOM_min as per page 86 of the paper (I don't
         know what the "int" function means). This value can be used as a guide
         to determine whether a class needs to be split into several classes.
      -->
      <vars>
        <var id="methods">
          <xsl:value-of select="$methods_count"/>
        </var>
        <var id="method_pairs">
          <xsl:value-of select="count($method_pairs/pair)"/>
        </var>
        <var id="attributes">
          <xsl:value-of select="$attributes_count"/>
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

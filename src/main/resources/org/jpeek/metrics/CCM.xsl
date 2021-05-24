<?xml version="1.0"?>
<!--
The MIT License (MIT)

Copyright (c) 2017-2019 Yegor Bugayenko

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
          </xsl:if>
        </xsl:for-each>
        <xsl:for-each select="$method/preceding-sibling::method">
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

    <xsl:variable name="graph">
      <xsl:for-each-group select="$edges/edge" group-by="method">
        <v name="{current-grouping-key()}">
          <xsl:value-of select="current-group()"/>
        </v>
      </xsl:for-each-group>
    </xsl:variable>

    <xsl:copy>
      <xsl:variable name="groups">
        <xsl:call-template name="groups">
          <xsl:with-param name="root" select="$graph"/>
        </xsl:call-template>
      </xsl:variable>

      <xsl:variable name="nc" select="count($edges/edge)"/>
      <xsl:variable name="ncc" select="count(distinct-values($edges/edge/method/text()))"/>
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

        <var id="graph">
          <xsl:copy-of select="$graph"/>
        </var>

        <var id="groups">
          <xsl:copy-of select="$groups"/>
<!--          <groups>-->
<!--            <xsl:for-each select="$groups/groups/group">-->
<!--              <xsl:variable name="group" select="."/>-->
<!--              <group>-->
<!--                <xsl:for-each select="$group/e">-->
<!--                  <e>-->
<!--                    <xsl:value-of select="."/>-->
<!--                  </e>-->
<!--                </xsl:for-each>-->
<!--              </group>-->
<!--            </xsl:for-each>-->
<!--          </groups>-->
        </var>
      </vars>
    </xsl:copy>
  </xsl:template>

  <xsl:template name="group">
    <xsl:param name="root"/>
    <xsl:param name="x" as="element()"/>
    <xsl:param name="seen"/>

    <xsl:element name="group">
      <xsl:attribute name="name">
        <xsl:value-of select="$x/@name"/>
      </xsl:attribute>

      <xsl:for-each select="$x/edge">
        <xsl:for-each select="./method">
          <xsl:variable name="r" select="."/>
          <xsl:if test="not($seen[@name=$r])">
            <xsl:element name="e">
              <xsl:value-of select="$r"/>
            </xsl:element>

            <xsl:call-template name="group">
              <xsl:with-param name="root" select="$root"/>
              <xsl:with-param name="x" select="$root/v[@name=$r]"/>
              <xsl:with-param name="seen">
                <xsl:copy-of select="$seen"/>
                <xsl:copy-of select="$r"/>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:if>
        </xsl:for-each>
      </xsl:for-each>
    </xsl:element>

  </xsl:template>

  <xsl:template name="groups">
    <xsl:param name="root"/>

    <xsl:element name="groups">
      <xsl:for-each select="$root/v">
        <xsl:variable name="g">
          <xsl:call-template name="group">
            <xsl:with-param name="root" select="$root"/>
            <xsl:with-param name="x" select="."/>
            <xsl:with-param name="seen"/>
          </xsl:call-template>
        </xsl:variable>

        <xsl:element name="group">
          <xsl:for-each select="$g/e">
            <xsl:sort select="."/>
            <xsl:copy-of select="."/>
          </xsl:for-each>
        </xsl:element>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>

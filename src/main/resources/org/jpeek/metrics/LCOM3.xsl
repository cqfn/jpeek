<?xml version="1.0"?>
<!--
 * Copyright (c) 2017-2025 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:template match="skeleton">
    <metric>
      <xsl:apply-templates select="@*"/>
      <title>LCOM3</title>
      <description>
        <xsl:text>LCOM3, like LCOM2, is an attempt to address some shortcomings
          of the original LCOM:
          - LCOM gives a value of zero for very different classes
          - Its definition is based on method-data interaction, which may not
            be a correct way to define cohesiveness in the object-oriented world
          - Very different classes may have an equal value
          - As LCOM is defined on variable access, it's not well suited for
            classes that internally access their data via properties
          LCOM3 values are in the range [0, 2], where 0 = "high cohesion",
          1 = "no cohesion" (class should be split), and values &gt;= 1 suggest
          serious design flaws in the class, such as unused ("dead") attributes
          or perhaps the attributes are accessed only from outside the class.
          If there are no more than one method in a class, LCOM3 is undefined.
          If there are no variables in a class, LCOM3 is undefined. An
          undefined LCOM3 is displayed as zero.</xsl:text>
      </description>
      <xsl:apply-templates select="node()"/>
    </metric>
  </xsl:template>
  <xsl:template match="class">
    <xsl:variable name="attrs" select="attributes/attribute/text()"/>
    <xsl:variable name="attrs_count" select="count($attrs)"/>
    <xsl:variable name="methods" select="methods/method"/>
    <xsl:variable name="methods_count" select="count($methods)"/>
    <xsl:variable name="attr_use">
      <xsl:for-each select="$attrs">
        <xsl:variable name="attr" select="."/>
        <count>
          <xsl:value-of select="count($methods/ops[op = $attr])"/>
        </count>
      </xsl:for-each>
    </xsl:variable>
    <xsl:copy>
      <xsl:attribute name="value">
        <xsl:choose>
          <xsl:when test="$methods_count &lt; 2 or $attrs_count = 0">
            <xsl:text>0</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="format-number((($methods_count - (sum($attr_use/count) div $attrs_count)) div ($methods_count - 1)), '0.####')"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="@*"/>
      <vars>
        <var id="methods">
          <xsl:value-of select="$methods_count"/>
        </var>
        <var id="attributes">
          <xsl:value-of select="$attrs_count"/>
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

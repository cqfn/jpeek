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
  <xsl:template match="skeleton">
    <metric>
      <xsl:apply-templates select="@*"/>
      <title>TCC</title>
      <description>
        <xsl:text>LCC(C) = (NDC(C) + NIC(C)) / NP(C), where C is the class, NP(C) is a
          maximal possible number of direct or indirect connections - N * (N - 1) / 2,
          NDC(C) is a number of direct connections, NID(C) is a number of indirect
          connections. Value of the metric is in range [0, 1], greater is better.
        </xsl:text>
      </description>
      <xsl:apply-templates select="node()"/>
    </metric>
  </xsl:template>
  <xsl:template match="class">
    <xsl:variable name="methods" select="methods/method"/>
    <xsl:variable name="methods_count" select="count($methods)"/>
    <xsl:variable name="NC" select="$methods_count * ($methods_count - 1) div 2"/>
    <xsl:variable name="directly-related-pairs">
      <xsl:for-each select="$methods">
        <xsl:variable name="i" select="position()"/>
        <xsl:variable name="left" select="."/>
        <xsl:variable name="left_ops" select="$left/ops/op[@code='get' or @code='put']"/>
        <xsl:for-each select="$methods">
          <xsl:if test="position() &gt; $i">
            <xsl:variable name="right" select="."/>
            <xsl:variable name="right_ops" select="$right/ops/op[@code='get' or @code='put']"/>
            <pair>
              <xsl:value-of select="count($left_ops[.=$right_ops])"/>
            </pair>
          </xsl:if>
        </xsl:for-each>
      </xsl:for-each>
    </xsl:variable>
    <!--
    @todo #119:30min `indirectly-related-pairs` are pairs of methods, which are connected through `directly-related-pairs`.
     See section 3.1 of https://pdfs.semanticscholar.org/672e/de6e3e600eafd84036a0b983b88e481ac626.pdf
     The key sentence is "The indirect connection relation is the **transitive closure** of direct connection relation".
     See https://en.wikipedia.org/wiki/Transitive_closure for more info on what is a Transitive Closure.
     We need to apply a Transitive Closure Algorithm on a graph of `directly-related-pairs`.
     A simpliest Transitive Closure algorithms has O(V³) time complexity and involves a mutable V×V table (V is amount of vertices).
     See https://www.geeksforgeeks.org/transitive-closure-of-a-graph/ for an example.
    -->
    <!--<xsl:variable name="indirectly-related-pairs">
    </xsl:variable>
    <xsl:variable name="NIC" select="count($indirectly-related-pairs)"/>
    -->
    <xsl:variable name="NDC" select="count($directly-related-pairs)"/>
    <xsl:variable name="NIC" select="0"/>
    <xsl:copy>
      <xsl:attribute name="value">
        <xsl:choose>
          <xsl:when test="$methods_count le 1">
            <xsl:text>0</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:variable name="lcc" select="($NDC + $NIC) div $NC"/>
            <xsl:value-of select="format-number($lcc, '0.####')"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="@*"/>
      <vars>
        <var id="methods">
          <xsl:value-of select="count($methods)"/>
        </var>
        <var id="NC">
          <xsl:value-of select="$NC"/>
        </var>
        <var id="NDC">
          <xsl:value-of select="$NDC"/>
        </var>
        <var id="NIC">
          <xsl:value-of select="$NIC"/>
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

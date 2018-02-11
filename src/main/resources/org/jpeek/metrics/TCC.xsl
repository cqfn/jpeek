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
        <xsl:text>TCC(C) = NDC(C) / NP(C), where C is the class, NP(C) is a
          maximal possible number of direct or indirect connections - N * (N - 1) / 2,
          NDC(C) is a number of direct connections. Value of the metric is in range [0, 1],
          greater is better.
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
        <!--
        @todo #9:30min `directly-related-pairs` currently don't take into account cases when two methods are
         directly related through calling the third one. This could be possible to take into account only when
         skeleton will be able to provide method-method relation information (issue #106)
        -->
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
    <xsl:variable name="NDC" select="count($directly-related-pairs)"/>
    <xsl:copy>
      <xsl:attribute name="value">
        <xsl:choose>
          <xsl:when test="$methods_count le 1">0</xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$NDC div $NC"/>
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
      </vars>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>

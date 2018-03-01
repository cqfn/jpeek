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
      <title>TLCOM</title>
      <description>
        <xsl:text>
          "Transitive Lack of Cohesion in Methods" (TLCOM) expands upon
          the original definition for LCOM by extending the criteria for
          estabilishing relations between method pairs by also including cases
          when a m1 calls m2 and m2 happens to use attribute a2 of the class. This
          then means that m1 transitively uses a2 via m2.
          The chain of calls can have more than one hop: if m1 calls m2 which calls
          m3 which calls m4, and m4 uses attribute a4, then m1 is said to transitively
          use a4 as well.
          The final formula is the same as the original LCOM:
          TLCOM = |P| - |Q|, if |P| &gt; |Q|, otherwise it's 0 (zero). 'P' is
          the set of method-pairs that are not connected according to the
          definition above, and 'Q' is the set of method-pairs that are connected.
        </xsl:text>
      </description>
      <xsl:apply-templates select="node()"/>
    </metric>
  </xsl:template>
  <xsl:template match="class">
    <!--
    @todo #65:30min TLCOM: we are including access to ALL fields here. After #156 is fixed,
     come back and refactor xpath for $Ms_that_use_attrs, $left_attrs, and $right_attrs
     in order to avoid including access to fields belonging to other classes.
    -->
    <xsl:variable name="class_fqn" select="replace(string-join(../@id | @id, '.'), '^\.', '')"/>
    <xsl:variable name="A" select="attributes/attribute"/>
    <xsl:variable name="M" select="methods/method"/>
    <xsl:variable name="Ms_that_use_attrs">
      <xsl:for-each select="$M">
        <xsl:if test="./ops/op[@code != 'call'][. = $A]">
          <method>
            <xsl:value-of select="concat($class_fqn, '.', @name)"/>
          </method>
        </xsl:if>
      </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="pairs">
      <xsl:for-each select="$M">
        <xsl:variable name="left" select="."/>
        <xsl:variable name="left_name" select="concat($class_fqn, '.', $left/@name)"/>
        <xsl:variable name="left_attrs" select="$left/ops/op[@code != 'call' and exists(. = $A)]"/>
        <xsl:variable name="left_calls" select="$left/ops/op[@code = 'call' and matches(., concat('^', $class_fqn, '\.[^\.]+$'))]"/>
        <xsl:for-each select="$left/following-sibling::method">
          <xsl:variable name="right" select="."/>
          <xsl:variable name="right_name" select="concat($class_fqn, '.', $right/@name)"/>
          <xsl:variable name="right_attrs" select="$right/ops/op[@code != 'call' and exists(. = $A)]"/>
          <xsl:variable name="right_calls" select="$right/ops/op[@code = 'call' and matches(., concat('^', $class_fqn, '\.[^\.]+$'))]"/>
          <!--
          @todo #65:30min TLCOM: need to establish transitive connections between methods with arbitrarily long
           chains of method calls between them. Right now, this code can establish transitive connections to a
           max depth of 3 levels, ie up to something like this: m1 calls m2 calls m3 which uses a3 therefore m1 is
           connected to m3. It will miss connections in longer chains. After fixing this, maybe we can add it to
           the standard metrics in App.
          -->
          <!--
          @todo #65:15min TLCOM: add test for OneMethodCreatesLambda after #171 is fixed. The extra synthetic
           method introduced by the compiler distorts this metric's result.
          -->
          <xsl:choose>
            <xsl:when test="$left_attrs[. = $right_attrs]">
              <Q/>
            </xsl:when>
            <xsl:when test="exists($left_calls[matches(., $right_name)]) and exists($Ms_that_use_attrs/method[. = $right_name])">
              <Q/>
            </xsl:when>
            <xsl:when test="exists($right_calls[matches(., $left_name)]) and exists($Ms_that_use_attrs/method[. = $left_name])">
              <Q/>
            </xsl:when>
            <xsl:when test="exists($left_calls[. = $right_calls]) and exists($right_calls[. = $Ms_that_use_attrs/method])">
              <Q/>
            </xsl:when>
            <xsl:when test="exists($right_calls[. = $left_calls]) and exists($left_calls[. = $Ms_that_use_attrs/method])">
              <Q/>
            </xsl:when>
            <xsl:otherwise>
              <P/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:for-each>
      </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="P" select="count($pairs/P)"/>
    <xsl:variable name="Q" select="count($pairs/Q)"/>
    <xsl:copy>
      <xsl:attribute name="value">
        <xsl:choose>
          <xsl:when test="$P &gt; $Q">
            <xsl:value-of select="$P - $Q"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>0</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="@*"/>
      <vars>
        <var id="methods">
          <xsl:value-of select="count($M)"/>
        </var>
        <var id="attributes">
          <xsl:value-of select="count($A)"/>
        </var>
        <var id="|P|">
          <xsl:value-of select="$P"/>
        </var>
        <var id="|Q|">
          <xsl:value-of select="$Q"/>
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

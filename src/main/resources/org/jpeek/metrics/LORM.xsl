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
  <!--
  @todo #111:30min LORM: figure out how to extract the concepts from each
   method of a class and then measure their "dispersal": how far apart
   they are semantically speaking. This would be the same as asking
   "to what degree are these methods talking about the same things?".
   Based on a couple of supporting papers that I added to this ticket
   (here: https://github.com/yegor256/jpeek/issues/111#issuecomment-365651455),
   I believe the way forward is to use Latent Semantic Indexing of the methods
   of the class and see how close or synonymous they are with the concept
   expressed in the class name itself. There seem to be a few java libraries
   available for latent semantic indexing that we can use.
  -->
  <xsl:template match="skeleton">
    <metric>
      <xsl:apply-templates select="@*"/>
      <title>LORM</title>
      <description>
        <xsl:text><![CDATA[
          LORM = (R / RN) where,
          R = number of pairs of methods in the class for which
          one method contains conceptual relations forming
          external links out of the set of concepts that
          belong to the method to or from the set of concepts
          belonging to another method in the class.
          Concepts/conceptual relations are identified by
          semantic processing techniques.
          N = total number of member functions (methods)
          RN - Total number of possible relations. RN = N * (N - 1) / 2
        ]]></xsl:text>
      </description>
      <xsl:apply-templates select="node()"/>
    </metric>
  </xsl:template>
  <xsl:template match="class">
    <xsl:variable name="methods" select="methods/method"/>
    <xsl:variable name="possible_relations">
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
    @todo #112:30min Implement natural language processing techniques to analyze
     conceptual similarity of methods. Two methods have a `conceptual-relation`
     if they contain at least one same `concept`. `concepts` are identified by
     semantic processing of the class/method, as being associated in the
     knowledge-base with that class/method (original paper in
     /papers/etzkorn00.pdf). The original paper does not explicitly define the
     implementation of semantic processing, or which NLP methods are being used,
     but it is a knowledge-based system, which means that additional input must
     inserted into the LORM method - that is, a set of possible concepts -
     which are then recognized in a particular method/piece of code. This set of
     possible concepts depends on the `domain` that the class is in.
     One way of interpreting what the `domain` is (this is not defined in the
     original paper), is the business domain the class is used for. Another
     definition of `domain` also comes to mind - the programming language being
     used. Concepts could be defined as all reserved Java keywords and "semantic
     processing" recognizes which reserved keywords are used in a particular
     method. Hence, if two methods use same reserved keywords, they have a
     `conceptual-relation`. Using this definition would require no additional
     input by the user of the LORM method. Right now it is stubbed because
     NLP/semantic processing is not implemented yet.k
     Ensure that JPeek core implements these techniques, collects information on
     such relations and unstub the lines commented below.
    -->
    <!--
     <xsl:variable name="possible_relations"/>
     <xsl:variable name="R" select="count($possible_relations)"/>
    -->
    <xsl:variable name="R">
      <xsl:value-of select="0"/>
    </xsl:variable>
    <xsl:variable name="RN" select="count($possible_relations)"/>
    <xsl:variable name="LORM">
      <xsl:choose>
        <xsl:when test="$RN lt 1">
          <xsl:value-of select="0"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$R div $RN"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:copy>
      <xsl:attribute name="value" select="$LORM"/>
      <xsl:apply-templates select="@*"/>
      <vars>
        <var id="R">
          <xsl:value-of select="$R"/>
        </var>
        <var id="RN">
          <xsl:value-of select="$RN"/>
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

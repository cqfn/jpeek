<?xml version="1.0"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
    <xsl:variable name="prefix" select="concat(@id, '.')"/>
    <!-- constructors are not methods -->
    <xsl:variable name="methods" select="methods/method[@ctor='false']"/>
    <!-- links between class methods -->
    <xsl:variable name="relations">
      <xsl:for-each select="$methods">
        <xsl:variable name="from" select="@name"/>
        <xsl:for-each select="ops/op[@code='call'][starts-with(name, $prefix)]">
          <xsl:variable name="to" select="substring-after(name, $prefix)"/>
          <link from="{$from}" to="{$to}"/>
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
    <!--  number of unique pairs of methods in the class -->
    <xsl:variable name="R">
      <xsl:variable name="unique_relations" select="$relations/link[not(following::link/@to=@to and following::link/@from=@from)]"/>
      <xsl:value-of select="count($unique_relations)"/>
    </xsl:variable>
    <!-- N = "total number of method functions in the class" -->
    <xsl:variable name="N" select="count(./methods/method[@ctor='false'])"/>
    <!-- R = "Total number of possible relations." -->
    <xsl:variable name="RN" select="$N * ($N - 1) div 2"/>
    <!-- LORM = the main metric -->
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
      <!-- rounded to 5 meaningful digits -->
      <xsl:attribute name="value" select="round($LORM*10000) div 10000"/>
      <xsl:apply-templates select="@*"/>
      <vars>
        <var id="R">
          <xsl:value-of select="$R"/>
        </var>
        <var id="N">
          <xsl:value-of select="$N"/>
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

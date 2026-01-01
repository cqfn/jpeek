<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:param name="ctors" select="0"/>
  <xsl:template match="skeleton">
    <metric>
      <xsl:apply-templates select="@*"/>
      <title>C3</title>
      <description>
        <xsl:text><![CDATA[
          C3(c) = ACMS(c) if ACMS(c) > 0 else 0; where c is a class
          ACSM(c) = average CSM(c) for all method pairs in c (skip identity
                    pairs, i.e. a method pairing with itself)
          CSM(m1, m2) = cosine between LSI(m1) and LSI(m2)

          LSI is a vector, defined as:
          1) For each function, collect all unique words from variable
          names and comments (split variable names such as `myDatabaseBook` into
          words `my`, `database`, and `book.
          2) Extract frequency information and build co-occurrence matrix.
          3) Reduce matrix with SVD.
          More info on LSI: https://en.wikipedia.org/wiki/Latent_semantic_analysis#Latent_semantic_indexing

          The original paper is at /papers/marcus05.pdf.
        ]]></xsl:text>
      </description>
      <xsl:apply-templates select="node()"/>
    </metric>
  </xsl:template>
  <xsl:template match="class">
    <xsl:variable name="methods" select="methods/method[($ctors=0 and @ctor='false') or $ctors=1]"/>
    <xsl:variable name="possible_relations">
      <xsl:for-each select="$methods">
        <xsl:variable name="i" select="position()"/>
        <xsl:variable name="left" select="."/>
        <xsl:for-each select="$methods">
          <xsl:if test="position() &gt; $i">
            <xsl:variable name="right" select="."/>
          </xsl:if>
        </xsl:for-each>
      </xsl:for-each>
    </xsl:variable>
    <!--
    @todo #64:30min Implement the C3 method according to the description above.
     The variable names and comments have to be extracted into a set of words.
     JPeek core will have to provide all comments for each method so words
     can be extracted and LSI calculated for each method.
    -->
  </xsl:template>
  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>

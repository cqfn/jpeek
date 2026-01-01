<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="2.0">
  <xsl:output method="html" doctype-system="about:legacy-compat" encoding="UTF-8" indent="yes"/>
  <xsl:strip-space elements="*"/>
  <xsl:import href="/org/jpeek/web/layout.xsl"/>
  <xsl:template match="/page" mode="head">
    <title>
      <xsl:text>Upload</xsl:text>
    </title>
  </xsl:template>
  <xsl:template match="/page" mode="body">
    <form action="/do-upload" method="post" enctype="multipart/form-data">
      <fieldset>
        <label>
          <xsl:text>List of artifact coordinates (one per line):</xsl:text>
        </label>
        <textarea name="coordinates" style="width:100%;height:8em;">
          <xsl:text>org.jpeek:jpeek</xsl:text>
        </textarea>
        <input type="submit" value="Upload"/>
      </fieldset>
    </form>
  </xsl:template>
</xsl:stylesheet>

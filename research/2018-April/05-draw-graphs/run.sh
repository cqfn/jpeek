#!/bin/bash
#
# SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
# SPDX-License-Identifier: MIT

home=$(pwd)

rm -f "${home}/graph-*.tex"

ruby "${home}/draw.rb" ../04-collect-metrics/metrics.txt .

ruby "${home}/draw-distribution.rb" ../04-collect-metrics/metrics.txt .

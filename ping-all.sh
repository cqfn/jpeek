#!/usr/bin/env bash
# SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
# SPDX-License-Identifier: MIT

set -e -o pipefail

while IFS= read -r a; do
    curl -s "https://i.jpeek.org/${a}/index.html" > /dev/null
    echo "${a} pinged"
done < artifacts.csv

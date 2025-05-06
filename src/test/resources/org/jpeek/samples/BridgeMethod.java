// SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
// SPDX-License-Identifier: MIT

class BridgeMethod extends Node {
    public void setData(Integer data) {
        super.setData(data);
    }
}

class Node {
    private Object data;

    public void setData(Object data) {
        this.data = data;
    }
}

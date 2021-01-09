package com.zhao.common.utils.wechat.menu;

import java.util.List;

public class ComplexButton extends Button {
    private List<CommonButton> sub_button;

    public List<CommonButton> getSub_button() {
        return sub_button;
    }

    public void setSub_button(List<CommonButton> sub_button) {
        this.sub_button = sub_button;
    }
}

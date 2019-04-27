package com.xiaolong.miaosha.exception;

import com.xiaolong.miaosha.result.CodeMsg;

public class GlobalException extends RuntimeException {

    private static final long serialVersionUID = -4950024878901741680L;

    private CodeMsg cm;

    public GlobalException(CodeMsg cm) {
        super(cm.toString());
        this.cm = cm;
    }

    public CodeMsg getCm() {
        return cm;
    }
}

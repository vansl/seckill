package com.vansl.seckill.VO;

import lombok.Data;
import java.io.Serializable;

@Data
public class ResultVO<T> implements Serializable {

    private static final long serialVersionUID = 3068837394742385883L;

    private Integer code = 200;
    private String msg = "ok";
    private T data;

}

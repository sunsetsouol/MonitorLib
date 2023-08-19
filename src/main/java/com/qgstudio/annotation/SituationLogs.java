package com.qgstudio.annotation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SituationLogs {

    private String method;

    private String args;

    private String actualArgs;

    private String  result;

    private String actualResult;

    private Integer status;
}

package com.qgstudio.MyLogger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Log {

    private String moduleName;

    private String time;

    private String thread;

    private String level;


    private String message;

}

package org.example;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InputSpec {
    public String controllerName;
    public String requestMapping;
    public String helperClass;

    public APISpec[] apis;

}

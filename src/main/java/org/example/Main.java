package org.example;

import com.example.controller.ResponseData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.javapoet.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        ObjectMapper objectMapper = new ObjectMapper();
        InputStream is = Main.class.getResourceAsStream("/inputSpec.json");
        InputSpec user = objectMapper.readValue(is, InputSpec.class);

        List<MethodSpec> methodSpecs = new ArrayList<>();

        generateHelperClass(user.getHelperClass());

        for(APISpec api : user.apis) {
            AnnotationSpec requestMapping = null;

            ParameterSpec nameParameter = null;
            AnnotationSpec requestParam = null;
            for (ParamInput param : api.params) {
                switch (api.apiType) {
                    case "GET" -> {
                        requestMapping = AnnotationSpec.builder(GetMapping.class)
                                .addMember("value", "$S", api.apiName)
                                .build();

                        requestParam = AnnotationSpec.builder(RequestParam.class)
                                .addMember("value", "$S", param.paramName)
                                .build();
                    }

                    case "POST" -> {
                        requestMapping = AnnotationSpec.builder(PostMapping.class)
                                .addMember("value", "$S", api.apiName)
                                .build();

                        requestParam = AnnotationSpec.builder(RequestBody.class)
                                .build();
                    }
                }
                nameParameter = ParameterSpec.builder(String.class, param.paramName)
                        .addAnnotation(requestParam)
                        .build();
                MethodSpec methodSpec = MethodSpec.methodBuilder(api.methodName)
                        .addAnnotation(requestMapping)
                        .addStatement(user.helperClass + " helper = "+user.helperClass+".getInstance()")
                        .addStatement("$T resData = helper." + api.methodName + "()", ResponseData.class)
                        .addParameter(nameParameter)
                        .build();

                ClassName personClass = ClassName.get("com.example.helper", user.helperClass);

                MethodSpec methodSpec2 = MethodSpec.methodBuilder("getHelper")
                        .addStatement(user.helperClass + " helper = "+user.helperClass+".getInstance()")
                        .addStatement("return helper")
                        .returns(personClass)
                        .build();

                methodSpecs.add(methodSpec);
                methodSpecs.add(methodSpec2);

            }
        }

        AnnotationSpec requestMapping = AnnotationSpec.builder(RequestMapping.class)
                .addMember("value", "$S", user.requestMapping)
                .build();

        TypeSpec typeSpec = TypeSpec
                .classBuilder(user.controllerName)
                .addAnnotation(requestMapping)
                .addMethods(methodSpecs)
                .build();


        JavaFile javaFile = JavaFile.builder("com.example.controller", typeSpec)

                .build();

        // Write the generated file to the specified path
        javaFile.writeTo(Paths.get("./src/main/java"));
    }

    private static void generateHelperClass(String helperClass) throws IOException {

        ClassName personClass = ClassName.get("com.example.helper", helperClass);

        MethodSpec getInstance = MethodSpec.methodBuilder("getInstance")
                .returns(personClass)
                .addModifiers(Modifier.STATIC)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return new " + helperClass + "()")
                .build();

        TypeSpec typeSpec = TypeSpec
                .classBuilder(helperClass)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(getInstance)
                .build();

        JavaFile javaFile = JavaFile.builder("com.example.helper", typeSpec)
                .build();

        javaFile.writeTo(Paths.get("./src/main/java"));
    }
}
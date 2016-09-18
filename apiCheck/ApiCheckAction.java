package com.jd.market.interact.web.action.checkNewModel.api;

import com.jd.common.struts.action.BaseAction;
import com.jd.common.web.result.Result;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by maoyi on 2016/8/31.
 */
public class ApiCheckAction extends BaseAction{

    private String classComleteName;
    private String methodName;

    public String init(){
        String packageName = "com.jd.market.interact.api.service";

        Set<String> className = getClassName(packageName, true);
        Result result = new Result();
        result.addDefaultModel("class",className);
        toVm(result);
        return "success";
    }

    public String method(){
        try {
            Method[] methods = Class.forName(classComleteName).getMethods();
            Set<String> methodSet = new HashSet<>();
            for (Method method : methods) {
                methodSet.add(method.getName());
            }
            Result result = new Result();
            result.addDefaultModel("method",methodSet);
            toVm(result);
            return "success";
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return SUCCESS;
    }

    public String args(){
        try {
            Class<?> aClass = Class.forName(classComleteName);
            Method[] methods = aClass.getMethods();
            Map<String,Method> methodMap = new HashMap<>();
            for (Method method : methods) {
                methodMap.put(method.getName(),method);
            }
            Method method = methodMap.get(methodName);
            Class<?>[] parameterTypes = method.getParameterTypes();
            Map<String,List<String>> args = new HashMap<>();
            for (Class<?> type : parameterTypes) {
                List<String> fields = new ArrayList<>();
                if(type.getSimpleName().equals("Long")){
                    fields.add("value");
                }else {
                    for (Field field : type.getDeclaredFields()) {
                        fields.add(field.getName());
                    }
                }
                args.put(type.getSimpleName(),fields);
            }
            Result result = new Result();
            result.addDefaultModel("args",args);
            toVm(result);
            return "success";
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return SUCCESS;
    }

    public String doCheck(){
        return SUCCESS;
    }


    public static Set<String> getClassName(String packageName, boolean isRecursion) {
        Set<String> classNames = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packagePath = packageName.replace(".", "/");

        URL url = loader.getResource(packagePath);
        if (url != null) {
            String protocol = url.getProtocol();
            if (protocol.equals("file")) {
                classNames = getClassNameFromDir(url.getPath(), packageName, isRecursion);
            } else if (protocol.equals("jar")) {
                JarFile jarFile = null;
                try{
                    jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
                } catch(Exception e){
                    e.printStackTrace();
                }

                if(jarFile != null){
                    classNames =getClassNameFromJar(jarFile.entries(), packageName, isRecursion);
                }
            }
        } else {
            classNames = getClassNameFromJars(((URLClassLoader)loader).getURLs(), packageName, isRecursion);
        }

        return classNames;
    }

    private static Set<String> getClassNameFromDir(String filePath, String packageName, boolean isRecursion) {
        Set<String> className = new HashSet<String>();
        File file = new File(filePath);
        File[] files = file.listFiles();
        for (File childFile : files) {
            if (childFile.isDirectory()) {
                if (isRecursion) {
                    className.addAll(getClassNameFromDir(childFile.getPath(), packageName+"."+childFile.getName(), isRecursion));
                }
            } else {
                String fileName = childFile.getName();
                if (fileName.endsWith(".class") && !fileName.contains("$")) {
                    className.add(packageName+ "." + fileName.replace(".class", ""));
                }
            }
        }

        return className;
    }

    private static Set<String> getClassNameFromJar(Enumeration<JarEntry> jarEntries, String packageName, boolean isRecursion){
        Set<String> classNames = new HashSet<String>();

        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            if(!jarEntry.isDirectory()){
                String entryName = jarEntry.getName().replace("/", ".");
                if (entryName.endsWith(".class") && !entryName.contains("$") && entryName.startsWith(packageName)) {
                    entryName = entryName.replace(".class", "");
                    if(isRecursion){
                        classNames.add(entryName);
                    } else if(!entryName.replace(packageName+".", "").contains(".")){
                        classNames.add(entryName);
                    }
                }
            }
        }

        return classNames;
    }

    private static Set<String> getClassNameFromJars(URL[] urls, String packageName, boolean isRecursion) {
        Set<String> classNames = new HashSet<String>();

        for (int i = 0; i < urls.length; i++) {
            String classPath = urls[i].getPath();

            if (classPath.endsWith("classes/")) {continue;}

            JarFile jarFile = null;
            try {
                jarFile = new JarFile(classPath.substring(classPath.indexOf("/")));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jarFile != null) {
                classNames.addAll(getClassNameFromJar(jarFile.entries(), packageName, isRecursion));
            }
        }
        return classNames;
    }


    public String getClassComleteName() {
        return classComleteName;
    }

    public void setClassComleteName(String classComleteName) {
        this.classComleteName = classComleteName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}

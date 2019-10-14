package com.raidandfade.JsonDoclet;

import com.sun.javadoc.*;
import com.google.gson.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String... args){}

    public static boolean start(RootDoc root) throws Exception {
        ClassDoc[] classes = root.classes();
        File bp = new File("."+File.separator+"docs");
        if(!bp.exists()&&!bp.mkdirs()) throw new IOException("Couldn't create base folder paths");
        float count = 0;
        int lastpct = 0;
        String lastClass = "";
        for (ClassDoc c : classes) {
            parseClass(c);
            count++;
            int curpct = Math.round((count/classes.length)*100f);
            String curClass = c.qualifiedName();
            if(lastpct!=curpct||count==classes.length){
                System.out.println("["+(int)count+"/"+classes.length+"] "+(curpct)+"% ("+lastClass+" - "+curClass+")");
                lastpct = curpct;
                lastClass = curClass;
            }
        }
        return true;
    }

    private static void parseClass(ClassDoc c) throws Exception{
        HashMap<String,Object> props = new HashMap<>();
        props.put("typeName",c.typeName());
        props.put("name",c.name());
        props.put("modifiers",c.modifiers());
        props.put("qualifiedName",c.qualifiedName());
        props.put("docString", c.commentText());
        Object superclass = null;
        if(c.superclass()!=null){
            superclass = new HashMap<String,Object>();
            ((HashMap<String,Object>) superclass).put("name",c.superclass().name());
            ((HashMap<String,Object>) superclass).put("qualifiedName",c.superclass().qualifiedTypeName());
        }
        props.put("superclass",superclass);
        ArrayList<HashMap<String, Object>> parents = null;
        if (c.superclass() != null) {
            parents = new ArrayList<>();
            for (ClassDoc sc = c.superclass(); sc != null; sc = sc.superclass()) {
                HashMap<String, Object> parent = new HashMap<>();
                parent.put("name",sc.name());
                parent.put("qualifiedName",sc.qualifiedTypeName());
                parents.add(parent);
            }
        }
        props.put("parents", parents);
        ArrayList<Object> ans = new ArrayList<>();
        for(AnnotationDesc at : c.annotations()){
            ans.add(parseAnnotation(at));
        }
        props.put("annotations",ans);
        ArrayList<Object> cns = new ArrayList<>();
        for(ConstructorDoc cn : c.constructors()){
            cns.add(parseConstructor(cn));
        }
        props.put("constructors",cns);
        ArrayList<Object> mthds = new ArrayList<>();
        for(MethodDoc mtd : c.methods()){
            mthds.add(parseMethod(mtd));
        }
        props.put("methods",mthds);
        ArrayList<Object> flds = new ArrayList<>();
        for(FieldDoc fld : c.fields()){
            flds.add(parseField(fld));
        }
        props.put("fields",flds);
        ArrayList<Object> ifaces = new ArrayList<>();
        for(Type ifct : c.interfaceTypes()){
            ifaces.add(parseType(ifct));
        }
        props.put("interfaces",ifaces);
        writeJson(new File("."+File.separator+"docs"+File.separator+c.qualifiedName()+".json"),props);
        for (ClassDoc ic:c.innerClasses()) {
            parseClass(ic);
        }
    }

    private static Object parseField(FieldDoc fld) {
        HashMap<String,Object> props = new HashMap<>();
        props.put("name",fld.name());
        props.put("modifiers",fld.modifiers());
        props.put("type",parseType(fld.type()));
        props.put("qualifiedName",fld.qualifiedName());
        props.put("docString",fld.commentText());
        ArrayList<Object> ans = new ArrayList<>();
        for(AnnotationDesc at : fld.annotations()){
            ans.add(parseAnnotation(at));
        }
        props.put("annotations",ans);
        return props;
    }

    private static Object parseMethod(MethodDoc mt) {
        HashMap<String,Object> props = new HashMap<>();
        props.put("name",mt.name());
        props.put("modifiers",mt.modifiers());
        props.put("docString",mt.commentText());
        props.put("qualifiedName",mt.qualifiedName());
        ArrayList<Object> xcpts = new ArrayList<>();
        for(Type xcpt : mt.thrownExceptionTypes()){
            xcpts.add(parseType(xcpt));
        }
        props.put("exceptions",xcpts);
        ArrayList<Object> ans = new ArrayList<>();
        for(AnnotationDesc at : mt.annotations()){
            ans.add(parseAnnotation(at));
        }
        props.put("annotations",ans);
        ArrayList<Object> prms = new ArrayList<>();
        for(Parameter pm : mt.parameters()){
            prms.add(parseParameter(pm));
        }
        props.put("parameters",prms);
        props.put("returnType",parseType(mt.returnType()));
        return props;
    }

    private static Object parseType(Type t){
        HashMap<String,Object> props = new HashMap<>();
        props.put("name",t.typeName());
        props.put("qualifiedName",t.qualifiedTypeName());
        props.put("type",t.toString());
        return props;
    }

    private static Object parseConstructor(ConstructorDoc cn) {
        HashMap<String,Object> props = new HashMap<>();
        props.put("name",cn.name());
        props.put("modifiers",cn.modifiers());
        props.put("docString",cn.commentText());
        props.put("qualifiedName",cn.qualifiedName());
        ArrayList<Object> xcpts = new ArrayList<>();
        for(Type xcpt : cn.thrownExceptionTypes()){
            xcpts.add(parseType(xcpt));
        }
        props.put("exceptions",xcpts);
        ArrayList<Object> ans = new ArrayList<>();
        for(AnnotationDesc at : cn.annotations()){
            ans.add(parseAnnotation(at));
        }
        props.put("annotations",ans);
        ArrayList<Object> prms = new ArrayList<>();
        for(Parameter pm : cn.parameters()){
            prms.add(parseParameter(pm));
        }
        props.put("parameters",prms);
        return props;
    }

    private static Object parseParameter(Parameter pm) {
        HashMap<String,Object> props = new HashMap<>();
        props.put("name",pm.name());
        props.put("type",parseType(pm.type()));
        ArrayList<Object> ans = new ArrayList<>();
        for(AnnotationDesc at : pm.annotations()){
            ans.add(parseAnnotation(at));
        }
        props.put("annotations",ans);
        return props;
    }

    private static Object parseAnnotation(AnnotationDesc at) {
        HashMap<String,Object> props = new HashMap<>();
        props.put("typeName",at.annotationType().name());
        props.put("qualifiedTypeName",at.annotationType().qualifiedTypeName());
        ArrayList<Object> els = new ArrayList<>();
        for (AnnotationDesc.ElementValuePair evp:at.elementValues()) {
            HashMap<String,Object> elprops = new HashMap<>();
            elprops.put("name",evp.element().name());
            elprops.put("qualifiedName",evp.element().qualifiedName());
            elprops.put("value",evp.value().toString());
            els.add(elprops);
        }
        props.put("elements",els);
        return props;
    }

    private static void writeJson(File f, Object o) throws IOException {
        if(f.exists())f.delete();
        if(!f.createNewFile()) throw new IOException("Cant create file "+f.getName());
        if(!f.canWrite()) throw new IOException("Hey bud let me write to "+f.getName());

        FileWriter fw = new FileWriter(f);
        String json = new Gson().toJson(o);
        fw.write(json);
        fw.flush();
        fw.close();
    }

}

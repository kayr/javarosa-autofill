package com.github.kayr.javarosa.autofill.api.functions;

import com.github.kayr.javarosa.autofill.api.IOUtils;
import org.javarosa.core.model.FormDef;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static com.github.kayr.javarosa.autofill.api.functions.FunctionUtils.*;
import static com.github.kayr.javarosa.autofill.api.functions.ISimpleFunctionHandler.createFn;

public class ListFunctions {

    public static void registerAll(FormDef formDef) {

        Arrays.asList(


                createFn("list")
                        .setHandler((objects, context) -> Arrays.stream(objects).collect(ArrayList::new, ArrayList::add, ArrayList::addAll)),


                createFn("list-remove-at")
                        .setHandler((objects, context) -> {
                            List   list = _list(objects, 0);
                            int    i    = _int(objects, 1);
                            Object o    = list.get(i);
                            list.remove(i);
                            return o;
                        }),


                createFn("list-remove")
                        .setHandler((objects, context) -> {
                            List list = _list(objects, 0);
                            list.remove(objects[1]);
                            return list;
                        }),


                createFn("list-add")
                        .setHandler((objects, context) -> {
                            List<Object> list = _list(objects, 0);
                            IntStream.range(1, objects.length)
                                     .forEach((i) -> list.add(objects[i]));
                            return list;
                        }),


                createFn("list-get-at")
                        .setHandler((objects, context) -> {
                            List<Object> list = _list(objects, 0);
                            return list.get(_int(objects, 1));
                        }),


                createFn("list-size")
                        .setHandler((objects, context) -> _list(objects, 0).size()),


                createFn("list-random-get")
                        .setHandler((objects, context) -> Fakers.getRandom(_list(objects, 0))),


                createFn("list-random-remove")
                        .setHandler((objects, context) -> {
                            List<Object> list  = _list(objects, 0);
                            int          i     = Fakers.faker.number().numberBetween(0, list.size());
                            Object       value = list.get(i);

                            list.remove(i);
                            return value;
                        }),


                createFn("list-from-file")
                        .setHandler((objects, context) -> {
                            String path = _string(objects, 0);

                            try {
                                return IOUtils.loadFile(path);
                            } catch (Exception x) {
                                return IOUtils.sneakyThrow(x);
                            }
                        }))
              .forEach(h -> formDef.getEvaluationContext().addFunctionHandler(h));


    }
}

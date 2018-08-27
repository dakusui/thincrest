package com.github.dakusui.crest.utils.printable;

import com.github.dakusui.crest.core.InternalUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.dakusui.crest.core.InternalUtils.summarize;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

public enum Functions {
  ;

  public static VarArgs varargs(Object... args) {
    return varargsOf(Object.class, args);
  }

  @SafeVarargs
  public static <T> VarArgs<T> varargsOf(Class<T> type, T... args) {
    return VarArgs.of(type, args);
  }

  public interface VarArgs<T> {
    T[] values();

    @SafeVarargs
    static <T> VarArgs<T> of(Class<T> type, T... args) {
      requireNonNull(type);
      return new VarArgs<T>() {
        @Override
        public T[] values() {
          return args;
        }

        @Override
        public String toString() {
          return String.format("%s:varargs%s", summarize(type.getSimpleName()), summarize(args));
        }
      };
    }
  }

  public static final Object THIS = new Object() {
    public String toString() {
      return "(THIS)";
    }
  };

  public static <E> Function<E, E> identity() {
    return Printable.function(
        "identity",
        Function.identity()
    );
  }

  public static <E> Function<? super E, String> stringify() {
    return Printable.function(
        "toString",
        Object::toString
    );
  }

  public static Function<? super String, Integer> length() {
    return Printable.function(
        "length",
        String::length
    );
  }

  public static <E> Function<List<? extends E>, ? extends E> elementAt(int i) {
    return Printable.function(
        () -> String.format("elementAt[%s]", i),
        es -> (E) es.get(i)
    );
  }

  public static Function<? super Collection, Integer> size() {
    return Printable.function(
        "size",
        Collection::size
    );
  }

  public static <E> Function<Collection<? extends E>, Stream<? extends E>> stream() {
    return Printable.function(
        "stream",
        Collection::stream
    );
  }

  public static <E> Function<? super Object, ? extends E> cast(Class<E> type) {
    return Printable.function(
        () -> String.format("castTo[%s]", requireNonNull(type).getSimpleName()),
        type::cast
    );
  }

  public static <I extends Collection<? extends E>, E> Function<I, List<E>> collectionToList() {
    return Printable.function("collectionToList", (I c) -> new LinkedList<E>() {
      {
        addAll(c);
      }
    });
  }

  public static <E> Function<E[], List<E>> arrayToList() {
    return Printable.function("arrayToList", Arrays::asList);
  }

  public static Function<String, Integer> countLines() {
    return Printable.function("countLines", (String s) -> s.split("\n").length);
  }

  @SuppressWarnings("unchecked")
  public static <I, E> Function<? super I, ? extends E> invoke(String methodName, Object... args) {
    return invokeOn(THIS, methodName, args);
  }

  public static <I, E> Function<? super I, ? extends E> invokeOn(Object on, String methodName, Object... args) {
    return Printable.function(
        on == THIS
            ? () -> String.format("@%s%s", methodName, summarize(args))
            : () -> String.format("%s@%s%s", on, methodName, summarize(args)),
        (I target) -> InternalUtils.invokeMethod(replaceTarget(on, target), methodName, replaceTargetInArray(target, expandVarArgsInArray(args)))
    );
  }

  @SuppressWarnings("unchecked")
  public static <I, E> Function<? super I, ? extends E> invokeStatic(Class klass, String methodName, Object... args) {
    return Printable.function(
        () -> String.format("@%s.%s%s", klass.getSimpleName(), methodName, summarize(args)),
        (I target) -> InternalUtils.invokeStaticMethod(klass, methodName, replaceTargetInArray(target, expandVarArgsInArray(args)))
    );
  }

  private static Object[] expandVarArgsInArray(Object[] args) {
    if (args.length > 0) {
      if (args[args.length - 1] instanceof VarArgs) {
        if (IntStream.range(0, args.length - 1).anyMatch(i -> args[i] instanceof VarArgs))
          throw new RuntimeException("VarArgs can only come at the last of values");
        return new ArrayList<Object>() {{
          this.addAll(asList(Arrays.copyOf(args, args.length - 1)));
          this.add(((VarArgs) args[args.length - 1]).values());
        }}.toArray();
      } else {
        return args;
      }
    }
    return args;
  }

  private static Object[] replaceTargetInArray(Object target, Object[] args) {
    return Arrays.stream(args)
        .map(e -> replaceTarget(e, target)).toArray();
  }

  private static <I> Object replaceTarget(Object on, I target) {
    return on == THIS ?
        target :
        on instanceof Object[] ?
            replaceTargetInArray(target, (Object[]) on) :
            on;
  }
}

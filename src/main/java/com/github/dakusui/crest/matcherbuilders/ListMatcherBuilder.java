package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.core.Printable;
import com.github.dakusui.crest.functions.CrestPredicates;
import com.github.dakusui.crest.functions.TransformingPredicate;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ListMatcherBuilder<IN, ENTRY, SELF extends ListMatcherBuilder<IN, ENTRY, SELF>> extends ObjectMatcherBuilder<IN, List<ENTRY>, SELF> {
  protected ListMatcherBuilder(Function<? super IN, ? extends List<ENTRY>> function) {
    super(function);
  }

  public SELF containsAll(Collection<?> collection) {
    return this.check(
        new TransformingPredicate<Collection<?>, Collection<?>>(
            String.format("containsAll%s", collection),
            CrestPredicates.isEmpty(),
            Printable.function(
                String.format("missing%s", collection),
                objects -> collection.stream(
                ).filter(
                    each -> !objects.contains(each)
                ).collect(
                    toList()
                ))
        )
    );
  }

  public SELF containsOnly(Collection<?> collection) {
    return this.check(
        new TransformingPredicate<Collection<?>, Collection<?>>(
            String.format("containsOnly%s", collection),
            CrestPredicates.isEmpty(),
            Printable.function(
                String.format("extra%s", collection),
                objects -> objects.stream(
                ).filter(
                    each -> !collection.contains(each)
                ).collect(
                    toList()
                ))
        )
    );
  }

  public SELF containsExactly(Collection<?> collection) {
    return this.check(
        new TransformingPredicate<Collection<?>, Collection<?>>(
            String.format("containsExactly%s", collection),
            CrestPredicates.isEmpty(),
            Printable.function(
                String.format("difference%s", collection),
                objects -> Stream.concat(
                    objects.stream(), collection.stream()
                ).filter(
                    each -> !(collection.contains(each) && objects.contains(each))
                ).collect(
                    toList()
                ))
        )
    );
  }

  public SELF containsNone(Collection<?> collection) {
    return this.check(
        new TransformingPredicate<Collection<?>, Collection<?>>(
            String.format("containsNone%s", collection),
            CrestPredicates.isEmpty(),
            Printable.function(
                String.format("contained%s", collection),
                objects -> objects.stream(
                ).filter(
                    collection::contains
                ).collect(
                    toList()
                ))
        )
    );
  }

  public SELF contains(Object entry) {
    return this.check(CrestPredicates.contains(entry));
  }

  public SELF isEmpty() {
    return this.check(CrestPredicates.isEmpty());
  }

  public SELF allMatch(Predicate<? super ENTRY> predicate) {
    return this.check(
        Printable.predicate(
            String.format("allMatch[%s]", predicate),
            entries -> entries.stream().allMatch(predicate)
        ));
  }

  public SELF anyMatch(Predicate<? super ENTRY> predicate) {
    return this.check(
        Printable.predicate(
            String.format("anyMatch[%s]", predicate),
            entries -> entries.stream().anyMatch(predicate)
        ));
  }

  public SELF noneMatch(Predicate<? super ENTRY> predicate) {
    return this.check(
        Printable.predicate(
            String.format("noneMatch[%s]", predicate),
            entries -> entries.stream().noneMatch(predicate)
        ));
  }

}

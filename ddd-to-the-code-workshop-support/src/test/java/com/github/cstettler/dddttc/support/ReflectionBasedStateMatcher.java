package com.github.cstettler.dddttc.support;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.cstettler.dddttc.support.ReflectionUtils.propertyValue;
import static java.util.stream.Collectors.joining;

public abstract class ReflectionBasedStateMatcher<T> extends TypeSafeDiagnosingMatcher<T> {

    private static final String INDENTATION = "        ";

    private final String objectName;
    private final Map<String, Matcher<?>> matchersByPropertyName;

    protected ReflectionBasedStateMatcher(String objectName, Map<String, Matcher<?>> matchersByPropertyName) {
        this.objectName = objectName;
        this.matchersByPropertyName = matchersByPropertyName;
    }

    @Override
    protected boolean matchesSafely(T object, Description mismatchDescription) {
        mismatchDescription.appendText("properties of " + this.objectName + " did not match\n");

        return this.matchersByPropertyName.entrySet().stream()
                .reduce(true, (state, entry) -> {
                    String propertyName = entry.getKey();
                    Object propertyValue = propertyValue(object, propertyName);
                    Matcher<?> matcher = entry.getValue();

                    if (!(matcher.matches(propertyValue))) {
                        mismatchDescription.appendText(INDENTATION + "'" + propertyName + "' : ");
                        matcher.describeMismatch(propertyValue, mismatchDescription);
                        mismatchDescription.appendText("\n");

                        return false;
                    }

                    return state;
                }, (a, b) -> a && b);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(this.objectName + " with state\n" + describe(this.matchersByPropertyName));
    }

    public static <M extends ReflectionBasedStateMatcher<?>, B extends MatcherBuilder<M>> M hasState(B matcherBuilder) {
        return matcherBuilder.build();
    }

    private static String describe(Map<String, Matcher<?>> matchersByPropertyName) {
        return matchersByPropertyName.entrySet().stream()
                .map((entry) -> "'" + entry.getKey() + "' : " + entry.getValue())
                .collect(joining("\n" + INDENTATION, INDENTATION, ""));
    }


    protected static abstract class MatcherBuilder<M extends ReflectionBasedStateMatcher<?>> {

        private Map<String, Matcher<?>> matchersByPropertyName;

        protected MatcherBuilder() {
            this.matchersByPropertyName = new LinkedHashMap<>();
        }

        M build() {
            return build(this.matchersByPropertyName);
        }

        protected abstract M build(Map<String, Matcher<?>> matchersByPropertyName);

        protected <B extends MatcherBuilder<M>> B recordPropertyMatcher(B matcherBuilder, String propertyName, Matcher<?> propertyMatcher) {
            this.matchersByPropertyName.put(propertyName, propertyMatcher);

            return matcherBuilder;
        }

    }

}

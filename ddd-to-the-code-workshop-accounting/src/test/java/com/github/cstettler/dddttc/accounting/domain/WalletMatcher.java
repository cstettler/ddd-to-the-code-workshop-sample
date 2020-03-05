package com.github.cstettler.dddttc.accounting.domain;

import com.github.cstettler.dddttc.support.ReflectionBasedStateMatcher;
import org.hamcrest.Matcher;

import java.util.Map;

public class WalletMatcher extends ReflectionBasedStateMatcher<Wallet> {

    private WalletMatcher(Map<String, Matcher<?>> matchersByPropertyName) {
        super("wallet", matchersByPropertyName);
    }

    public static Builder walletWith() {
        return new Builder();
    }


    public static class Builder extends MatcherBuilder<WalletMatcher> {

        public Builder id(Matcher<?> idMatcher) {
            return recordPropertyMatcher(this, "id", idMatcher);
        }

        @Override
        protected WalletMatcher build(Map<String, Matcher<?>> matchersByPropertyName) {
            return new WalletMatcher(matchersByPropertyName);
        }

    }

}

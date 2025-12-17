package com.github.cstettler.dddttc.support;

import com.github.cstettler.dddttc.stereotype.Repository;
import org.springframework.core.type.filter.AnnotationTypeFilter;

public class RepositoryImplementationFilter extends AnnotationTypeFilter {

    public RepositoryImplementationFilter() {
        super(Repository.class, false, true);
    }

}

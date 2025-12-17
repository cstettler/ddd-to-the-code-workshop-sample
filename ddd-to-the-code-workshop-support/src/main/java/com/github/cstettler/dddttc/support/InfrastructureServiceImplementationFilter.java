package com.github.cstettler.dddttc.support;

import com.github.cstettler.dddttc.stereotype.InfrastructureService;
import org.springframework.core.type.filter.AnnotationTypeFilter;

public class InfrastructureServiceImplementationFilter extends AnnotationTypeFilter {

    public InfrastructureServiceImplementationFilter() {
        super(InfrastructureService.class, false, true);
    }

}

package com.github.cstettler.dddttc.support;


import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.boot.test.autoconfigure.filter.TypeExcludeFilters;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
@TypeExcludeFilters(EnableComponentScanExclusions.ComponentScanExcludeFilter.class)
public @interface EnableComponentScanExclusions {

    @Target(TYPE)
    @Retention(RUNTIME)
    @interface ExcludeFromComponentScan {

    }


    class ComponentScanExcludeFilter extends TypeExcludeFilter {

        @Override
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
            return metadataReader.getAnnotationMetadata().hasAnnotation(ExcludeFromComponentScan.class.getName());
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof ComponentScanExcludeFilter;
        }

        @Override
        public int hashCode() {
            return 37;
        }

    }

}

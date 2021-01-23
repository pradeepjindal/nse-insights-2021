package org.pra.nse.config;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;
import java.util.List;

public class YamlPropertyLoaderFactory extends DefaultPropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        if(resource == null) {
            return super.createPropertySource(name, resource);
        }
        List<PropertySource<?>> list = new YamlPropertySourceLoader().load(resource.getResource().getFilename(), resource.getResource());
        return list.get(0);
    }

}

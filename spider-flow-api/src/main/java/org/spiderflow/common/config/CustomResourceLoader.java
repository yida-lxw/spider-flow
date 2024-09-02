package org.spiderflow.common.config;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.net.URL;

/**
 * @author yida
 * @date 2023-05-26 10:45
 * @description 自定义ResourceLoader
 */
public class CustomResourceLoader extends DefaultResourceLoader {
    public CustomResourceLoader() {
        super();
    }

    /**
     * Create a new DefaultResourceLoader.
     *
     * @param classLoader the ClassLoader to load class path resources with, or {@code null}
     *                    for using the thread context class loader at the time of actual resource access
     */
    public CustomResourceLoader(@Nullable ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    public Resource getResource(String location) {
        Assert.notNull(location, "Location must not be null");
        for (ProtocolResolver protocolResolver : getProtocolResolvers()) {
            Resource resource = protocolResolver.resolve(location, this);
            if (resource != null) {
                return resource;
            }
        }

        if (location.startsWith("/")) {
            return getResourceByPath(location);
        } else if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            return new CustomClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()), this.getClass());
        } else {
            try {
                // Try to parse the location as a URL...
                URL url = ResourceUtils.getURL(location);
                return (ResourceUtils.isFileURL(url) ? new FileUrlResource(url) : new UrlResource(url));
            } catch (FileNotFoundException ex) {
                // No URL -> resolve as resource path.
                return getResourceByPath(location);
            }
        }
    }
}

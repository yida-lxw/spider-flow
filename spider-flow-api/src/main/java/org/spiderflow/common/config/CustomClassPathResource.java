package org.spiderflow.common.config;

import org.spiderflow.core.utils.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author yida
 * @date 2023-05-25 17:38
 * @description 自定义ClassPathResource
 */
public class CustomClassPathResource extends ClassPathResource {
    protected String path;

    protected String absolutePath;

    @Nullable
    protected ClassLoader classLoader;

    @Nullable
    protected Class<?> clazz;


    public CustomClassPathResource(String path) {
        super(path);
    }

    public CustomClassPathResource(String path, ClassLoader classLoader) {
        super(path, classLoader);
    }

    public CustomClassPathResource(String path, Class<?> clazz) {
        super(path, clazz);
        Assert.notNull(path, "Path must not be null");
        this.path = StringUtils.cleanPath(path, false);

        String absolutePath = this.path;
        if (clazz != null && !absolutePath.startsWith("/")) {
            if (!path.endsWith(".class") && !path.endsWith(".yml") && !path.endsWith(".xml") && !path.endsWith(".properties")) {
                absolutePath = ClassUtils.classPackageAsResourcePath(clazz) + "/" + absolutePath;
            }
        } else if (absolutePath.startsWith("/")) {
            absolutePath = absolutePath.substring(1);
        }
        this.absolutePath = absolutePath;

        this.classLoader = null;
        this.clazz = clazz;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream is;
        if (this.clazz != null) {
            if (!this.path.endsWith(".class")) {
                if (this.path.endsWith(".yml") || this.path.endsWith(".xml") || this.path.endsWith(".properties")) {
                    if (!this.path.startsWith("/")) {
                        this.path = "/" + this.path;
                        this.absolutePath = this.path;
                    }
                }
                is = this.clazz.getResourceAsStream(this.path);
            } else {
                is = ClassLoader.getSystemResourceAsStream(this.absolutePath);
            }
        } else if (this.classLoader != null) {
            is = this.classLoader.getResourceAsStream(this.absolutePath);
        } else {
            is = ClassLoader.getSystemResourceAsStream(this.absolutePath);
        }
        if (is == null) {
            throw new FileNotFoundException(getDescription() + " cannot be opened because it does not exist");
        }
        return is;
    }

    @Override
    protected URL resolveURL() {
        try {
            if (this.clazz != null) {
                return this.clazz.getResource(this.path);
            } else if (this.classLoader != null) {
                return this.classLoader.getResource(this.absolutePath);
            } else {
                return ClassLoader.getSystemResource(this.absolutePath);
            }
        } catch (IllegalArgumentException ex) {
            // Should not happen according to the JDK's contract:
            // see https://github.com/openjdk/jdk/pull/2662
            return null;
        }
    }
}

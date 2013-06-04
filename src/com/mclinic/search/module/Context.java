/**
 * Copyright 2012 Muzima Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mclinic.search.module;

import android.content.res.Resources;
import com.google.inject.Injector;
import com.mclinic.search.api.context.ServiceContext;
import com.mclinic.search.api.model.object.Searchable;
import com.mclinic.search.api.model.resolver.Resolver;
import com.mclinic.search.api.model.serialization.Algorithm;
import com.mclinic.search.api.resource.ResourceConstants;
import com.mclinic.search.sample.Configuration;
import com.nribeka.search.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * TODO: Write brief description about the class here.
 */
public class Context {

    private Injector injector;

    private static final ThreadLocal<Configuration> configHolder = new ThreadLocal<Configuration>();

    public Context(final Injector injector, final Resources resources) throws IOException {
        setInjector(injector);
        initConfiguration();
        initService(resources);
    }

    private void setInjector(final Injector injector) {
        System.out.println("Thread: " + Thread.currentThread().hashCode());
        this.injector = injector;
    }

    private void initConfiguration() throws IOException {
        Configuration savedConfig = configHolder.get();
        if (savedConfig != null) {
            Configuration config = injector.getInstance(Configuration.class);
            config.configure(savedConfig.getUsername(), savedConfig.getPassword(), savedConfig.getServer());
        }
    }

    public void initService(final Resources resources) throws IOException {
        // TODO: this should be replaced with a classpath scanner.
        // Approach:
        // * Create marker (annotation) for Object, Algorithm and Resolver.
        // * Register it to the ServiceContext of the search api.
        // Once we have classpath scanner we can probably kick off the scanner here.
        // TODO: a better approach will be reading the config and the loading the class
        // Approach:
        // * Get the configuration file.
        // * Read the object, algorithm, and resolver line to get the class name.
        // * Do: Class c = Class.forName() using the class name from above.
        // * Do: Object o = inject.getInstance(c).
        // * Register object according to their type registerObject, registerAlgorithm, or registerResolver.
        // TODO: Create a list of all available configuration file to load j2l automatically.
        // Approach:
        // * Create a list of all configuration file (list of j2l file names, just like they list hbm files).
        // * TODO: need to add a new method in the search api to register resource using InputStream --done
        // * Read each line and use Class.getResourceAsStream(path to j2l) and then register each of them.
        try {
            ServiceContext serviceContext = injector.getInstance(ServiceContext.class);
            initServiceContext(serviceContext, resources.openRawResource(R.raw.cohortmember));
            initServiceContext(serviceContext, resources.openRawResource(R.raw.searchcohort));
            initServiceContext(serviceContext, resources.openRawResource(R.raw.searchobservation));
            initServiceContext(serviceContext, resources.openRawResource(R.raw.uuidpatient));

            serviceContext.registerResource(resources.openRawResource(R.raw.cohortmember));
            serviceContext.registerResource(resources.openRawResource(R.raw.searchcohort));
            serviceContext.registerResource(resources.openRawResource(R.raw.searchobservation));
            serviceContext.registerResource(resources.openRawResource(R.raw.uuidpatient));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initServiceContext(final ServiceContext serviceContext, final InputStream inputStream)
            throws IOException, ClassNotFoundException {
        Properties properties = new Properties();
        properties.load(inputStream);

        String searchableName = properties.getProperty(ResourceConstants.RESOURCE_SEARCHABLE);
        Class searchableClass = Class.forName(searchableName);
        Searchable searchable = (Searchable) injector.getInstance(searchableClass);
        serviceContext.registerSearchable(searchable);

        String algorithmName = properties.getProperty(ResourceConstants.RESOURCE_ALGORITHM_CLASS);
        Class algorithmClass = Class.forName(algorithmName);
        Algorithm algorithm = (Algorithm) injector.getInstance(algorithmClass);
        serviceContext.registerAlgorithm(algorithm);

        String resolverName = properties.getProperty(ResourceConstants.RESOURCE_URI_RESOLVER_CLASS);
        Class resolverClass = Class.forName(resolverName);
        Resolver resolver = (Resolver) injector.getInstance(resolverClass);
        serviceContext.registerResolver(resolver);
        inputStream.close();
    }

    private Injector getInjector() throws IOException {
        if (injector == null)
            throw new IOException("Injector that will wired up the API is not ready.");
        return injector;
    }

    public void configure(final String username, final String password, final String server) {
        Configuration config = injector.getInstance(Configuration.class);
        config.configure(username, password, server);

        Configuration configuration = new Configuration();
        configuration.configure(username, password, server);
        configHolder.set(configuration);
    }

    // TODO: need to bound the service class to prevent user from accessing the internal guice structure
    // Only open user to service layer. To do this:
    // * Make all *Service interface to extends Service
    // * Change this method signature to <T extends Service>
    public <T> T getInstance(final Class<T> serviceClass) throws IOException {
        return getInjector().getInstance(serviceClass);
    }
}

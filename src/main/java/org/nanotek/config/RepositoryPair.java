package org.nanotek.config;

import net.bytebuddy.dynamic.DynamicType;

public record RepositoryPair(String repositoryName , DynamicType.Unloaded<?>unloaded) {}

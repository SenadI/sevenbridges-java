/*
 * Copyright 2017 Seven Bridges Genomics, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sevenbridges.apiclient.impl.io;

import com.sevenbridges.apiclient.lang.Assert;

public class DefaultResourceFactory implements ResourceFactory {

  @Override
  public Resource createResource(String location) {
    Assert.hasText(location, "location argument cannot be null or empty.");

    if (location.startsWith(ClasspathResource.SCHEME_PREFIX)) {
      return new ClasspathResource(location);
    }

    //otherwise we assume a file:
    return new FileResource(location);
  }
}

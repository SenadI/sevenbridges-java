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
package com.sevenbridges.apiclient.impl.query;

import com.sevenbridges.apiclient.lang.Assert;
import com.sevenbridges.apiclient.lang.Strings;
import com.sevenbridges.apiclient.query.Criteria;
import com.sevenbridges.apiclient.query.Criterion;
import com.sevenbridges.apiclient.query.Options;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DefaultCriteria<T extends Criteria<T>, O extends Options> implements Criteria<T>, Expandable {

  protected final List<Criterion> criterionEntries;
  protected final O options;
  protected Integer limit;
  protected Integer offset;

  protected int currentOrderIndex = -1; //used for order clause building

  protected DefaultCriteria(O options) {
    Assert.notNull(options, "options argument cannot be null.");
    Assert.isInstanceOf(Expandable.class, options, "options argument is expected to implement the " + Expandable.class.getName() + " interface.");
    this.options = options;
    this.criterionEntries = new ArrayList<>();
  }

  @SuppressWarnings("unchecked")
  public T add(Criterion criterion) {
    Assert.notNull(criterion, "criterion cannot be null.");
    this.criterionEntries.add(criterion);
    return (T) this;
  }

  public T and(Criterion criterion) {
    return add(criterion);
  }

  public void remove(String propertyName) {
    Assert.hasText(propertyName, "property name cannot be null or empty");
    Iterator<Criterion> iterator = criterionEntries.iterator();
    while (iterator.hasNext()) {
      Criterion criterion = iterator.next();
      if (SimpleExpression.class.isAssignableFrom(criterion.getClass())) {
        if (propertyName.equals(((SimpleExpression) criterion).getPropertyName())) {
          iterator.remove();
        }
      }
    }
  }

  private int ensureOrderIndex() {
    int i = this.currentOrderIndex;
    Assert.state(i >= 0, "There is no current orderBy clause to declare as ascending or descending!");
    return i;
  }

  public List<Expansion> getExpansions() {
    //we can do this assertion because of the isInstance guarantee in the constructor:
    assert this.options instanceof Expandable;
    return ((Expandable) this.options).getExpansions();
  }

  public List<Criterion> getCriterionEntries() {
    return Collections.unmodifiableList(this.criterionEntries);
  }

  protected O getOptions() {
    return this.options;
  }

  @SuppressWarnings("unchecked")
  public T limitTo(int limit) {
    this.limit = Pagination.sanitizeLimit(limit);
    return (T) this;
  }

  public Integer getLimit() {
    return limit;
  }

  @SuppressWarnings("unchecked")
  public T offsetBy(int offset) {
    this.offset = Pagination.sanitizeOffset(offset);
    return (T) this;
  }

  public Integer getOffset() {
    return offset;
  }

  public boolean isEmpty() {
    return options.isEmpty() && criterionEntries.isEmpty() && (offset == null || offset == 0) && (limit == null || limit == 0);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(128);
    for (Criterion c : criterionEntries) {
      if (sb.length() > 0) {
        sb.append(" and ");
      }
      sb.append(c);
    }

    if (offset != null) {
      if (sb.length() > 0) {
        sb.append(" ");
      }
      sb.append("offset ").append(offset);
    }

    if (limit != null) {
      if (sb.length() > 0) {
        sb.append(" ");
      }
      sb.append("limit ").append(limit);
    }

    if (!options.isEmpty() && options instanceof Expandable) {
      Expandable expandable = (Expandable) options;
      if (sb.length() > 0) {
        sb.append(" ");
      }
      sb.append("expand ").append(Strings.collectionToDelimitedString(expandable.getExpansions(), ", "));
    }

    return sb.toString();
  }

}

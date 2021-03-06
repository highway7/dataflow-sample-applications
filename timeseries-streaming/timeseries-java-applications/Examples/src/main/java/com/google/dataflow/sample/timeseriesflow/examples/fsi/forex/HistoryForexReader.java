/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.dataflow.sample.timeseriesflow.examples.fsi.forex;

import com.google.auto.value.AutoValue;
import com.google.dataflow.sample.timeseriesflow.TimeSeriesData;
import java.util.Set;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollectionTuple;
import org.apache.beam.sdk.values.TupleTag;

@AutoValue
public abstract class HistoryForexReader extends PTransform<PBegin, PCollectionTuple> {

  public abstract String getSourceFilesURI();

  public abstract Set<String> getTickers();

  public static Builder builder() {
    return new AutoValue_HistoryForexReader.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder setSourceFilesURI(String newSourceFilesURI);

    public abstract Builder setTickers(Set<String> newTickers);

    public abstract HistoryForexReader build();
  }

  // Tags to implement basic example of deadletter queue pattern
  static final TupleTag<TimeSeriesData.TSDataPoint> successfulParse =
      new TupleTag<TimeSeriesData.TSDataPoint>();
  static final TupleTag<String> deadLetterTag = new TupleTag<String>();

  @Override
  public PCollectionTuple expand(PBegin input) {
    return input
        .apply(TextIO.read().from(getSourceFilesURI()))
        .apply(new ForexCSVAdaptor.ConvertCSVForex(getTickers()));
  }
}

package org.umetadata.service;

import java.util.List;
import org.umetadata.schema.type.Function;
import org.umetadata.schema.utils.ResultList;

public class FunctionList extends ResultList<Function> {
  @SuppressWarnings("unused")
  public FunctionList() {}

  public FunctionList(List<Function> data) {
    super(data, null, null, data.size());
  }
}

package com.alorma.github.sdk.bean.dto.request;

import java.io.Serializable;

/**
 * Created by Bernat on 17/05/2015.
 */
public class EditIssueLabelsRequestDTO extends EditIssueRequestDTO implements Serializable {
  public String[] labels;
}

// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.components.runtime;

import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.common.ComponentConstants;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * A layout component allowing subcomponents to be placed in tabular form.
 *
 * @author lizlooney@google.com (Liz Looney)
 */
@SimpleObject
public class TableLayout implements Layout {

  private final android.widget.TableLayout layoutManager;
  private final Handler handler;

  private int numColumns;
  private int numRows;
  private int[] shrinkableColumns;
  private int[] stretchableColumns;

  /**
   * Creates a new table layout.
   *
   * @param context  view context
   */
  TableLayout(Context context, int numColumns, int numRows) {
    layoutManager = new android.widget.TableLayout(context);
    this.numColumns = numColumns;
    this.numRows = numRows;
    handler = new Handler();

    for (int row = 0; row < numRows; row++) {
      TableRow tableRow = new TableRow(context);
      for (int col = 0; col < numColumns; col++) {
        tableRow.addView(newEmptyCellView(), col, newEmptyCellLayoutParams());
      }
      layoutManager.addView(tableRow, row, new android.widget.TableLayout.LayoutParams());
    }
  }

  int getNumColumns() {
    return numColumns;
  }

  void setNumColumns(int newNumColumns) {
    if (newNumColumns > numColumns) {
      // Add new cells in each row.
      Context context = layoutManager.getContext();
      for (int row = 0; row < numRows; row++) {
        TableRow tableRow = (TableRow) layoutManager.getChildAt(row);
        for (int col = numColumns; col < newNumColumns; col++) {
          tableRow.addView(newEmptyCellView(), col, newEmptyCellLayoutParams());
        }
      }
      numColumns = newNumColumns;

    } else if (newNumColumns < numColumns) {
      // Remove extra cells from each row.
      for (int row = 0; row < numRows; row++) {
        TableRow tableRow = (TableRow) layoutManager.getChildAt(row);
        tableRow.removeViews(newNumColumns, numColumns - newNumColumns);
      }
      numColumns = newNumColumns;
    }
  }

  int getNumRows() {
    return numRows;
  }

  void setNumRows(int newNumRows) {
    if (newNumRows > numRows) {
      // Add new rows
      Context context = layoutManager.getContext();
      for (int row = numRows; row < newNumRows; row++) {
        TableRow tableRow = new TableRow(context);
        for (int col = 0; col < numColumns; col++) {
          tableRow.addView(newEmptyCellView(), col, newEmptyCellLayoutParams());
        }
        layoutManager.addView(tableRow, row, new android.widget.TableLayout.LayoutParams());
      }
      numRows = newNumRows;
    } else if (newNumRows < numRows) {
      // Remove extra rows
      layoutManager.removeViews(newNumRows, numRows - newNumRows);
      numRows = newNumRows;
    }
  }

  String getShrinkableColumns() {
    if(shrinkableColumns == null || shrinkableColumns.length == 0) {
      return "";
    }
    StringBuffer ret = new StringBuffer();
    ret.append(shrinkableColumns[0]);
    for(int i = 1; i < shrinkableColumns.length; i++){
      ret.append(",").append(shrinkableColumns[i]);
    }
    return ret.toString();
  }

  void setShrinkableColumns(String indexList) {
    if(indexList.isEmpty()) {
      shrinkableColumns = null;
      return;
    }
    String indices[] = indexList.split(",");
    shrinkableColumns = new int[indices.length];
    for(int i = 0; i < indices.length; i++) {
      shrinkableColumns[i] = Integer.parseInt(indices[i]);
      layoutManager.setColumnShrinkable(shrinkableColumns[i] - 1, true); // Base-1 to base-0
    }
  }

  String getStretchableColumns() {
    if(stretchableColumns == null || stretchableColumns.length == 0) {
      return "";
    }
    StringBuffer ret = new StringBuffer();
    ret.append(stretchableColumns[0]);
    for(int i = 1; i < stretchableColumns.length; i++){
      ret.append(",").append(stretchableColumns[i]);
    }
    return ret.toString();
  }

  void setStretchableColumns(String indexList) {
    if(indexList.isEmpty()) {
      stretchableColumns = null;
      return;
    }
    String indices[] = indexList.split(",");
    stretchableColumns = new int[indices.length];
    for(int i = 0; i < indices.length; i++) {
      stretchableColumns[i] = Integer.parseInt(indices[i]);
      layoutManager.setColumnStretchable(stretchableColumns[i] - 1, true); // Base-1 to base-0
    }
  }

  void setColumnCollapsed(int columnIndex, boolean isCollapsed) {
    layoutManager.setColumnCollapsed(columnIndex - 1, isCollapsed); // Base-1 to base-0
  }

  // Layout implementation

  public ViewGroup getLayoutManager() {
    return layoutManager;
  }

  public void add(AndroidViewComponent child) {
    // At this time, the child doesn't have its row and column properties. So,
    // we'll actually add the child later.
    // However, we need to create the layout parameters for the child's view because the width and
    // height properties will be set before the child is actually added to the table and setting
    // the width and height properties requires that the child's view has layout parameters.
    child.getView().setLayoutParams(newCellLayoutParams());
    addChildLater(child);
  }

  /*
   * Causes addChild to be called later.
   */
  private void addChildLater(final AndroidViewComponent child) {
    handler.post(new Runnable() {
      public void run() {
        addChild(child);
      }
    });
  }

  private void addChild(AndroidViewComponent child) {
    int row = child.Row();
    int col = child.Column();
    if (row == ComponentConstants.DEFAULT_ROW_COLUMN ||
        col == ComponentConstants.DEFAULT_ROW_COLUMN) {
      addChildLater(child);

    } else {

      if (row >= 0 && row < numRows) {
        if (col >= 0 && col < numColumns) {
          TableRow tableRow = (TableRow) layoutManager.getChildAt(row);
          tableRow.removeViewAt(col);
          View cellView = child.getView();
          tableRow.addView(cellView, col, cellView.getLayoutParams());
        } else {
          Log.e("TableLayout", "Child has illegal Column property: " + child);
        }
      } else {
        Log.e("TableLayout", "Child has illegal Row property: " + child);
      }
    }
  }

  private View newEmptyCellView() {
    return new TextView(layoutManager.getContext());
  }

  private static TableRow.LayoutParams newEmptyCellLayoutParams() {
    return new TableRow.LayoutParams(0, 0);
  }

  private static TableRow.LayoutParams newCellLayoutParams() {
    return new TableRow.LayoutParams();
  }
}
